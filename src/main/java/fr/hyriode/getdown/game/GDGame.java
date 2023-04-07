package fr.hyriode.getdown.game;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.language.HyriLanguageMessage;
import fr.hyriode.api.leaderboard.IHyriLeaderboardProvider;
import fr.hyriode.api.leveling.NetworkLeveling;
import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.api.player.IHyriPlayerSession;
import fr.hyriode.getdown.HyriGetDown;
import fr.hyriode.getdown.api.GDData;
import fr.hyriode.getdown.api.GDStatistics;
import fr.hyriode.getdown.game.achievement.AchievementsItem;
import fr.hyriode.getdown.game.achievement.GDAchievement;
import fr.hyriode.getdown.game.scoreboard.*;
import fr.hyriode.getdown.language.GDMessage;
import fr.hyriode.getdown.world.GDWorld;
import fr.hyriode.getdown.world.jump.GDJumpWorld;
import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.actionbar.ActionBar;
import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.game.HyriGameState;
import fr.hyriode.hyrame.game.HyriGameType;
import fr.hyriode.hyrame.game.protocol.HyriDeathProtocol;
import fr.hyriode.hyrame.game.protocol.HyriLastHitterProtocol;
import fr.hyriode.hyrame.game.protocol.HyriWaitingProtocol;
import fr.hyriode.hyrame.game.team.HyriGameTeam;
import fr.hyriode.hyrame.game.util.HyriGameMessages;
import fr.hyriode.hyrame.game.util.HyriRewardAlgorithm;
import fr.hyriode.hyrame.title.Title;
import fr.hyriode.hyrame.utils.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

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

    public GDGame(HyriGetDown plugin) {
        super(IHyrame.get(), plugin,
                HyriAPI.get().getConfig().isDevEnvironment() ? HyriAPI.get().getGameManager().createGameInfo(HyriGetDown.ID, "GetDown") : HyriAPI.get().getGameManager().getGameInfo(HyriGetDown.ID),
                GDGamePlayer.class,
                HyriAPI.get().getConfig().isDevEnvironment() ? GDGameType.NORMAL : HyriGameType.getFromData(GDGameType.values()));
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
    public void handleLogin(Player player) {
        super.handleLogin(player);

        final GDGamePlayer gamePlayer = this.getPlayer(player);
        final GDData data = GDData.get(player.getUniqueId());
        final GDStatistics statistics = GDStatistics.get(player.getUniqueId());
        final HyriGameTeam team = new HyriGameTeam(player.getName(), null, null, 1){
            @Override
            public String getFormattedDisplayName(Player target) {
                return gamePlayer.formatNameWithTeam();
            }
        };

        this.registerTeam(team);

        team.addPlayer(gamePlayer);
        gamePlayer.setData(data);
        gamePlayer.setStatistics(statistics);

        this.hyrame.getItemManager().giveItem(gamePlayer.getPlayer(), 4, AchievementsItem.class);
    }

    @Override
    public void handleLogout(Player player) {
        final GDGamePlayer gamePlayer = this.getPlayer(player);
        final IHyriPlayer account = gamePlayer.asHyriPlayer();
        final GDData data = gamePlayer.getData();
        final GDStatistics statistics = gamePlayer.getStatistics();
        final GDStatistics.Data statisticsData = statistics.getData(this.getType());

        if (!this.getState().isAccessible()) {
            statisticsData.addGamesPlayed(1);
            statisticsData.addKills(gamePlayer.getKills());
            statisticsData.addJumpDeaths(gamePlayer.getJumpDeaths());
            statisticsData.addEarnedCoins(gamePlayer.getCoins());
            statisticsData.addDeathmatchDeaths(gamePlayer.getDeathmatchDeaths());
            statisticsData.addSuccessfulJumps(gamePlayer.getSuccessfulJumps());
        } else {
            this.unregisterTeam(gamePlayer.getTeam());
        }

        data.update(account);
        statistics.update(account);

        super.handleLogout(player);

        if (this.getState() == HyriGameState.PLAYING) {
            this.win(this.getWinner());
        }
    }

    @Override
    public void start() {
        super.start();

        this.protocolManager.enableProtocol(new HyriLastHitterProtocol(this.hyrame, this.plugin, 8 * 20L));

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

        winner.getPlayers().forEach(p -> {
            final GDGamePlayer gamePlayer = p.cast();

            gamePlayer.getStatistics().getData(this.getType()).addVictories(1);
        });

        this.sendWinMessage(winner);

        super.win(winner);
    }

    private void sendWinMessage(HyriGameTeam winner) {
        final List<HyriLanguageMessage> positions = Arrays.asList(
                HyriLanguageMessage.get("message.game.end.1"),
                HyriLanguageMessage.get("message.game.end.2"),
                HyriLanguageMessage.get("message.game.end.3")
        );

        final List<GDGamePlayer> topKillers = new ArrayList<>(this.players);

        topKillers.sort((o1, o2) -> o2.getKills() - o1.getKills());

        final Function<Player, List<String>> killersLineProvider = player -> {
            final List<String> killersLine = new ArrayList<>();

            for (int i = 0; i <= 2; i++) {
                final String killerLine = HyriLanguageMessage.get("message.game.end.kills").getValue(player).replace("%position%", positions.get(i).getValue(player));

                if (topKillers.size() > i){
                    final GDGamePlayer topKiller = topKillers.get(i);

                    killersLine.add(killerLine.replace("%player%", topKiller.formatNameWithTeam()).replace("%kills%", String.valueOf(topKiller.getKills())));
                    continue;
                }

                killersLine.add(killerLine.replace("%player%", HyriLanguageMessage.get("message.game.end.nobody").getValue(player)).replace("%kills%", "0"));
            }

            return killersLine;
        };

        // Send message to not-playing players
        for (Player player : Bukkit.getOnlinePlayers()) {
            final GDGamePlayer gamePlayer = this.getPlayer(player);

            if (gamePlayer == null) {
                player.spigot().sendMessage(HyriGameMessages.createWinMessage(this, player, winner, killersLineProvider.apply(player), null));
            }
        }

        for (GDGamePlayer gamePlayer : this.players) {
            final IHyriPlayer account = gamePlayer.asHyriPlayer();
            final UUID playerId = gamePlayer.getUniqueId();
            final int kills = gamePlayer.getKills();
            final boolean isWinner = winner.contains(gamePlayer);
            final long hyris = account.getHyris().add(HyriRewardAlgorithm.getHyris(kills, gamePlayer.getPlayTime(), isWinner) + gamePlayer.getSuccessfulJumps() * 30L).withMessage(false).exec();
            final double xp = account.getNetworkLeveling().addExperience(HyriRewardAlgorithm.getXP(kills, gamePlayer.getPlayTime(), isWinner) + gamePlayer.getSuccessfulJumps() * 30D);

            // Update leaderboards
            final IHyriLeaderboardProvider provider = HyriAPI.get().getLeaderboardProvider();

            provider.getLeaderboard(NetworkLeveling.LEADERBOARD_TYPE, "rotating-game-experience").incrementScore(playerId, xp);
            provider.getLeaderboard(HyriGetDown.ID, "kills").incrementScore(playerId, kills);
            provider.getLeaderboard(HyriGetDown.ID, "successful-jumps").incrementScore(playerId, gamePlayer.getSuccessfulJumps());

            if (isWinner) {
                provider.getLeaderboard(HyriGetDown.ID, "victories").incrementScore(playerId, 1);
            }

            account.update();

            // Send message
            final String rewardsLine = ChatColor.LIGHT_PURPLE + "+" + hyris + " Hyris " + ChatColor.GREEN + "+" + xp + " XP";

            if (gamePlayer.isOnline()) {
                final Player player = gamePlayer.getPlayer();

                player.spigot().sendMessage(HyriGameMessages.createWinMessage(this, gamePlayer.getPlayer(), winner, killersLineProvider.apply(player), rewardsLine));
            } else if (HyriAPI.get().getPlayerManager().isOnline(playerId)) {
                HyriAPI.get().getPlayerManager().sendMessage(playerId, HyriGameMessages.createOfflineWinMessage(this, account, rewardsLine));
            }
        }
    }

    public void switchToNextJumpWorld() {
        this.worldIndex++;

        final List<GDJumpWorld> worlds = HyriGetDown.get().getJumpWorlds();
        final GDJumpWorld world = worlds.get(this.worldIndex);

        this.currentWorld = world;
        this.currentWorld.teleportPlayers();

        IHyrame.get().getScoreboardManager().getScoreboards(SpectatorScoreboard.class).forEach(SpectatorScoreboard::update);

        for (GDGamePlayer gamePlayer : this.players) {
            if (!gamePlayer.isOnline()) {
                continue;
            }

            final Player player = gamePlayer.getPlayer();

            Title.sendTitle(player, ChatColor.AQUA + this.currentWorld.getName(), world.getDifficulty().getDisplayName().getValue(player), 5, 40, 5);
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

        this.scoreboardsTask.cancel();

        IHyrame.get().getScoreboardManager().getScoreboards(SpectatorScoreboard.class).forEach(SpectatorScoreboard::update);
        IHyrame.get().getScoreboardManager().getScoreboards(JumpScoreboard.class).forEach(JumpScoreboard::hide);

        this.getPlayers().forEach(gamePlayer -> {
            if (!gamePlayer.isOnline()) {
                return;
            }

            final Player player = gamePlayer.getPlayer();

            player.sendMessage(GDMessage.MESSAGE_BUY_PHASE_NAME.asLang().getValue(player.getUniqueId()));

            for (Integer achievementId : gamePlayer.getAchievements()) {
                final GDAchievement achievement = GDAchievement.getById(achievementId);

                if (achievement == null) {
                    continue;
                }

                final int coins = achievement.getCoins();

                player.sendMessage(GDMessage.MESSAGE_ACHIEVEMENT_COMPLETED.asString(player).
                        replace("%achievement%", achievement.getDisplay(player)
                        .replace("%coins%", String.valueOf(coins))));

                gamePlayer.addCoins(coins);
                gamePlayer.getData().addCompletedAchievement(achievement);
            }
        });

        for (GDGamePlayer gamePlayer : this.players) {
            gamePlayer.onBuyStart();
        }

        new BukkitRunnable() {

            private int index = 90;

            @Override
            public void run() {
                if (this.index == 0) {
                    for (GDGamePlayer gamePlayer : players) {
                        if (!gamePlayer.isOnline()) {
                            continue;
                        }

                        gamePlayer.getPlayer().getInventory().setItem(4, null);
                    }

                    switchToDeathMatch();

                    this.cancel();
                } else if (this.index <= 5) {
                    for (GDGamePlayer gamePlayer : players) {
                        if (!gamePlayer.isOnline()) {
                            continue;
                        }

                        final Player player = gamePlayer.getPlayer();

                        player.playSound(player.getLocation(), Sound.NOTE_PLING, 1.0F, 1.0F);

                        Title.sendTitle(player, ChatColor.RED + String.valueOf(this.index), "", 5, 20, 5);
                    }
                }

                for (GDGamePlayer gamePlayer : players) {
                    if (!gamePlayer.isOnline()) {
                        continue;
                    }

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

        this.currentPhase = GDPhase.DEATHMATCH;
        this.currentWorld = HyriGetDown.get().getDeathMatchWorld();
        this.protocolManager.enableProtocol(new HyriDeathProtocol(this.hyrame, this.plugin, initial -> {
            final GDGamePlayer gamePlayer = initial.cast();

            gamePlayer.onDeathmatchDeath();

            return false;
        }, new HyriDeathProtocol.Screen(0, player -> HyriGetDown.get().getDeathMatchWorld().teleportPlayer(player)), HyriDeathProtocol.ScreenHandler.Default.class));

        IHyrame.get().getScoreboardManager().getScoreboards(SpectatorScoreboard.class).forEach(SpectatorScoreboard::update);
        IHyrame.get().getScoreboardManager().getScoreboards(BuyScoreboard.class).forEach(BuyScoreboard::hide);

        this.currentWorld.teleportPlayers();

        for (GDGamePlayer gamePlayer : this.players) {
            if (!gamePlayer.isOnline()) {
                return;
            }

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
        final List<GDGamePlayer> alivePlayers = this.getAlivePlayers();

        return alivePlayers.size() != 1 ? null : alivePlayers.get(0).getTeam();
    }

    public List<GDGamePlayer> getAlivePlayers() {
        return this.players.stream().filter(gamePlayer -> !gamePlayer.isSpectator() && gamePlayer.isOnline()).collect(Collectors.toList());
    }

    public Objective getCoinsObjective() {
        return this.coinsObjective;
    }

    public GDPhase getCurrentPhase() {
        return this.currentPhase;
    }

    @Override
    public GDGameType getType() {
        return (GDGameType) super.getType();
    }

}
