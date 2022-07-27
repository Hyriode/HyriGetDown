package fr.hyriode.getdown.game;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.language.HyriLanguageMessage;
import fr.hyriode.getdown.HyriGetDown;
import fr.hyriode.getdown.game.scoreboard.BuyScoreboard;
import fr.hyriode.getdown.game.scoreboard.DeathMatchScoreboard;
import fr.hyriode.getdown.game.scoreboard.GDScoreboard;
import fr.hyriode.getdown.game.scoreboard.JumpScoreboard;
import fr.hyriode.getdown.language.GDMessage;
import fr.hyriode.getdown.world.GDWorld;
import fr.hyriode.getdown.world.jump.GDJumpWorld;
import fr.hyriode.hyrame.actionbar.ActionBar;
import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.game.HyriGamePlayer;
import fr.hyriode.hyrame.game.HyriGameType;
import fr.hyriode.hyrame.game.protocol.HyriDeathProtocol;
import fr.hyriode.hyrame.game.protocol.HyriLastHitterProtocol;
import fr.hyriode.hyrame.game.protocol.HyriWaitingProtocol;
import fr.hyriode.hyrame.game.team.HyriGameTeam;
import fr.hyriode.hyrame.title.Title;
import fr.hyriode.hyrame.utils.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Created by AstFaster
 * on 23/07/2022 at 11:34
 */
public class GDGame extends HyriGame<GDGamePlayer> {

    private GDPhase currentPhase;

    private final Scoreboard scoreboard;
    private Objective coinsObjective;

    private BukkitTask scoreboardsTask;

    private int worldIndex = -1;
    private GDWorld<?> currentWorld;

    public GDGame() {
        super(HyriGetDown.get().getHyrame(), HyriGetDown.get(),
                HyriAPI.get().getConfig().isDevEnvironment() ? HyriAPI.get().getGameManager().createGameInfo(HyriGetDown.ID, "GetDown") : HyriAPI.get().getGameManager().getGameInfo(HyriGetDown.ID),
                GDGamePlayer.class,
                HyriAPI.get().getConfig().isDevEnvironment() ? GDGameType.SOLO : HyriGameType.getFromData(GDGameType.values()));
        this.waitingRoom = new GDWaitingRoom(this);
        this.usingGameTabList = false;
        this.scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        this.reconnectionTime = 120;
        this.description = GDMessage.GAME_DESCRIPTION.asLang();

        final Objective oldObjective = this.scoreboard.getObjective("coins");

        if (oldObjective != null) {
            oldObjective.unregister();
        }
    }

    @Override
    public void postRegistration() {
        super.postRegistration();

        this.protocolManager.getProtocol(HyriWaitingProtocol.class).withTeamSelector(false);
    }

    @Override
    public void handleLogin(Player p) {
        super.handleLogin(p);

        final GDGamePlayer gamePlayer = this.getPlayer(p);

        if (gamePlayer == null) {
            return;
        }

        final HyriGameTeam team = new HyriGameTeam(this, p.getName(), null, null, 1);

        this.registerTeam(team);

        team.addPlayer(gamePlayer);
    }

    @Override
    public void handleLogout(Player player) {
        super.handleLogout(player);

        this.win(this.getWinner());
    }

    @Override
    public void start() {
        super.start();

        this.coinsObjective = this.scoreboard.registerNewObjective("coins", "dummy");
        this.coinsObjective.setDisplaySlot(DisplaySlot.PLAYER_LIST);

        this.currentPhase = GDPhase.JUMP;

        this.switchToNextJumpWorld();

        for (GDGamePlayer gamePlayer : this.players) {
            gamePlayer.onJumpsStart();
        }

        this.scoreboardsTask = Bukkit.getScheduler().runTaskTimerAsynchronously(HyriGetDown.get(), () -> {
            for (JumpScoreboard scoreboard : this.hyrame.getScoreboardManager().getScoreboards(JumpScoreboard.class)) {
                scoreboard.update();
            }
        }, 20L, 20L);
    }

    @Override
    public void win(HyriGameTeam winner) {
        if (winner == null) {
            return;
        }

        this.currentPhase = null;

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.teleport(HyriGetDown.get().getConfiguration().getWaitingRoom().getSpawn().asBukkit());
        }

        super.win(winner);
    }

    public void switchToNextJumpWorld() {
        this.worldIndex++;

        final List<GDJumpWorld> worlds = HyriGetDown.get().getJumpWorlds();
        final GDJumpWorld world = worlds.get(this.worldIndex);

        this.currentWorld = world;

        this.currentWorld.teleportPlayers();

        for (GDGamePlayer gamePlayer : this.players) {
            if (!gamePlayer.isOnline()) {
                continue;
            }

            final Player player = gamePlayer.getPlayer();

            Title.sendTitle(player, ChatColor.AQUA + this.currentWorld.getName(), world.getConfig().getDifficulty().getDisplayName().getValue(player), 5, 40, 5);
        }
    }

    public void switchToBuyPhase() {
        this.currentPhase = GDPhase.BUY;
        this.currentWorld = null;

        for (Player player : Bukkit.getOnlinePlayers()) {
            PlayerUtil.resetPlayer(player);

            player.setFallDistance(0.0F);
            player.teleport(HyriGetDown.get().getConfiguration().getWaitingRoom().getSpawn().asBukkit());
        }

        for (GDScoreboard scoreboard : this.hyrame.getScoreboardManager().getScoreboards(JumpScoreboard.class)) {
            scoreboard.hide();
        }

        this.sendMessageToAll(GDMessage.MESSAGE_BUY_PHASE_NAME::asString);

        for (GDGamePlayer gamePlayer : this.players) {
            gamePlayer.onBuyStart();
        }

        new BukkitRunnable() {

            private int index = 90;

            @Override
            public void run() {
                if (this.index == 0) {
                    for (GDGamePlayer gamePlayer : players) {
                        gamePlayer.getPlayer().getInventory().setItem(4, null);
                    }

                    switchToDeathMatch();

                    this.cancel();
                } else if (this.index <= 5) {
                    for (GDGamePlayer gamePlayer : players) {
                        final Player player = gamePlayer.getPlayer();

                        player.playSound(player.getLocation(), Sound.NOTE_PLING, 1.0F, 1.0F);

                        Title.sendTitle(player, ChatColor.RED + String.valueOf(this.index), "", 5, 20, 5);
                    }
                }

                for (GDGamePlayer gamePlayer : players) {
                    final Player player = gamePlayer.getPlayer();
                    final ActionBar actionBar = new ActionBar(GDMessage.ACTION_BAR_BUY_PHASE_TIME.asString(player).replace("%seconds%", String.valueOf(this.index)));

                    actionBar.send(player);
                }

                this.index--;
            }
        }.runTaskTimer(this.plugin, 0L, 20L);
    }

    public void switchToDeathMatch() {
        this.coinsObjective.unregister();

        this.currentPhase = GDPhase.DEATH_MATCH;
        this.currentWorld = HyriGetDown.get().getDeathMatchWorld();
        this.scoreboardsTask.cancel();
        this.protocolManager.enableProtocol(new HyriLastHitterProtocol(this.hyrame, this.plugin, 8 * 20L));
        this.protocolManager.enableProtocol(new HyriDeathProtocol(this.hyrame, this.plugin, gamePlayer -> {
            final Player player = gamePlayer.getPlayer();
            final List<HyriLastHitterProtocol.LastHitter> lastHitters = this.protocolManager.getProtocol(HyriLastHitterProtocol.class).getLastHitters(player);

            if (lastHitters != null) {
                final HyriLastHitterProtocol.LastHitter hitter = lastHitters.get(0);

                if (hitter != null) {
                    final GDGamePlayer killer = this.getPlayer(hitter.getUniqueId());

                    killer.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 5 * 20, 2, false, false));
                    killer.addKill();
                }
            }

            final PlayerInventory playerInventory = player.getInventory();
            final Consumer<ItemStack[]> dropConsumer = itemStacks -> {
                for (ItemStack itemStack : itemStacks) {
                    if(itemStack != null) {
                        player.getWorld().dropItemNaturally(player.getLocation(), itemStack);
                    }
                }
            };

            dropConsumer.accept(playerInventory.getContents());
            dropConsumer.accept(playerInventory.getArmorContents());

            return false;
        }));

        this.currentWorld.teleportPlayers();

        for (GDScoreboard scoreboard : this.hyrame.getScoreboardManager().getScoreboards(BuyScoreboard.class)) {
            scoreboard.hide();
        }

        for (GDGamePlayer gamePlayer : this.players) {
            gamePlayer.onDeathMatchStart();
        }

        this.scoreboardsTask = Bukkit.getScheduler().runTaskTimerAsynchronously(HyriGetDown.get(), () -> {
            for (DeathMatchScoreboard scoreboard : this.hyrame.getScoreboardManager().getScoreboards(DeathMatchScoreboard.class)) {
                scoreboard.update();
            }
        }, 20L, 20L);
    }

    public GDWorld<?> getNextWorld() {
        final int index = this.worldIndex + 1;
        final List<GDJumpWorld> jumpWorlds = HyriGetDown.get().getJumpWorlds();

        if (jumpWorlds.size() > index) {
            return jumpWorlds.get(index);
        }
        return HyriGetDown.get().getDeathMatchWorld();
    }

    public GDWorld<?> getCurrentWorld() {
        return this.currentWorld;
    }

    public int getWorldIndex() {
        return this.worldIndex;
    }

    public HyriGameTeam getWinner() {
        final List<HyriGamePlayer> alivePlayers = new ArrayList<>();

        for (HyriGamePlayer gamePlayer : this.players) {
            if (!gamePlayer.isSpectator() && gamePlayer.isOnline()) {
                alivePlayers.add(gamePlayer);
            }
        }
        return alivePlayers.size() != 1 ? null : alivePlayers.get(0).getTeam();
    }

    public Objective getCoinsObjective() {
        return this.coinsObjective;
    }

    public GDPhase getCurrentPhase() {
        return this.currentPhase;
    }

}
