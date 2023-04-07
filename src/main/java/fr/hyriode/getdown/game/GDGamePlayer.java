package fr.hyriode.getdown.game;

import fr.hyriode.api.player.IHyriPlayerSession;
import fr.hyriode.getdown.HyriGetDown;
import fr.hyriode.getdown.api.GDData;
import fr.hyriode.getdown.api.GDStatistics;
import fr.hyriode.getdown.game.achievement.GDAchievement;
import fr.hyriode.getdown.game.scoreboard.*;
import fr.hyriode.getdown.language.GDMessage;
import fr.hyriode.getdown.shop.item.ShopAccessorItem;
import fr.hyriode.getdown.world.GDWorld;
import fr.hyriode.getdown.world.jump.GDJumpWorld;
import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.game.HyriGamePlayer;
import fr.hyriode.hyrame.game.protocol.HyriLastHitterProtocol;
import fr.hyriode.hyrame.scoreboard.HyriScoreboard;
import fr.hyriode.hyrame.title.Title;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Created by AstFaster
 * on 23/07/2022 at 11:34
 */
public class GDGamePlayer extends HyriGamePlayer {

    private GDData data;
    private GDStatistics statistics;

    private int coins;
    private int successfulJumps;
    private int kills;
    private int jumpDeaths;
    private int deathmatchDeaths;

    private final GDGame game;
    private final List<Integer> achievements;

    public GDGamePlayer(Player player) {
        super(player);
        this.game = HyriGetDown.get().getGame();
        this.achievements = Arrays.stream(GDAchievement.values()).map(GDAchievement::getId).collect(Collectors.toList());
    }

    public void onJumpsStart() {
        new JumpScoreboard(this.player).show();
    }

    public void onBuyStart() {
        if (!this.isOnline()) {
            return;
        }

        new BuyScoreboard(this.player).show();

        IHyrame.get().getItemManager().giveItem(this.player, 4, ShopAccessorItem.class);
    }

    public void onDeathMatchStart() {
        if (!this.isOnline()) {
            return;
        }

        new DeathMatchScoreboard(this.player).show();
    }

    public void onReconnect() {
        final GDJumpWorld world = (GDJumpWorld) this.game.getCurrentWorld();

        this.player.teleport(world.getConfig().getSpawn().asBukkit(world.asBukkit()));

        this.onJumpsStart();
        this.onCoinsUpdated();
    }

    public void onJumpDeath() {
        final GDJumpWorld jumpWorld = (GDJumpWorld) this.game.getCurrentWorld();
        final int removedCoins = 20 * (1 + (this.coins / 1000));
        final List<HyriLastHitterProtocol.LastHitter> lastHitters = this.game.getProtocolManager().getProtocol(HyriLastHitterProtocol.class).getLastHitters(this.player);

        if (lastHitters != null) {
            final HyriLastHitterProtocol.LastHitter hitter = lastHitters.get(0);

            if (hitter != null) {
                final GDGamePlayer hitterGamePlayer = hitter.asGamePlayer().cast();
                final int killCoins = (int) (this.coins * 0.05);

                if (hitterGamePlayer.isOnline()) {
                    Title.sendTitle(hitterGamePlayer.getPlayer(), ChatColor.GREEN + "+ " + killCoins + " Coins", "", 5, 40, 5);
                }

                hitterGamePlayer.addCoins(killCoins);

                this.game.getPlayers().forEach(target -> target.getPlayer().sendMessage(GDMessage.MESSAGE_JUMP_DEATH.asString(target.getPlayer())
                        .replace("%player%", this.formatNameWithTeam())
                        .replace("%killer%", hitter.asGamePlayer().formatNameWithTeam())));
            }
        }

        this.jumpDeaths++;

        this.removeCoins(removedCoins);

        this.player.setHealth(20.0F);
        this.player.teleport(jumpWorld.getConfig().getSpawn().asBukkit(jumpWorld.asBukkit()));

        Title.sendTitle(this.player, ChatColor.RED + "- " + removedCoins + " Coins", "", 5, 40, 5);
    }

    public void onDeathmatchDeath() {
        this.deathmatchDeaths++;

        // Get last hitter
        final List<HyriLastHitterProtocol.LastHitter> lastHitters = this.game.getProtocolManager().getProtocol(HyriLastHitterProtocol.class).getLastHitters(this.player);

        if (lastHitters != null) {
            final HyriLastHitterProtocol.LastHitter hitter = lastHitters.get(0);

            if (hitter != null) {
                final GDGamePlayer killer = this.game.getPlayer(hitter.getUniqueId());

                killer.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 4 * 20, 2, false, false));
                killer.addKill();
            }
        }

        // Drop inventory
        final PlayerInventory playerInventory = player.getInventory();
        final Consumer<ItemStack[]> dropConsumer = itemStacks -> {
            for (ItemStack itemStack : itemStacks) {
                if (itemStack != null && !itemStack.getType().equals(Material.AIR)) {
                    player.getWorld().dropItemNaturally(player.getLocation(), itemStack);
                }
            }
        };

        dropConsumer.accept(playerInventory.getContents());
        dropConsumer.accept(playerInventory.getArmorContents());

        IHyrame.get().getScoreboardManager().getScoreboards(SpectatorScoreboard.class).forEach(SpectatorScoreboard::update);

        // Update scoreboard
        final HyriScoreboard scoreboard = IHyrame.get().getScoreboardManager().getPlayerScoreboard(this.player);

        if (scoreboard instanceof GDScoreboard) {
            ((GDScoreboard) scoreboard).update();
        }
    }

    private void onCoinsUpdated() {
        this.game.getCoinsObjective().getScore(this.player.getName()).setScore(this.coins);

        final HyriScoreboard scoreboard = IHyrame.get().getScoreboardManager().getPlayerScoreboard(this.player);

        if (scoreboard instanceof GDScoreboard) {
            ((GDScoreboard) scoreboard).update();
        }
    }
    public int getCoins() {
        return this.coins;
    }

    public void addCoins(int coins) {
        this.coins += coins;

        this.onCoinsUpdated();
    }

    public void removeCoins(int coins) {
        this.coins -= coins;

        if (this.coins < 0) {
            this.coins = 0;
        }

        this.onCoinsUpdated();
    }

    public int getSuccessfulJumps() {
        return this.successfulJumps;
    }

    public void addSuccessfulJump() {
        this.successfulJumps++;
    }

    public int getKills() {
        return this.kills;
    }

    public void addKill() {
        this.kills++;

        for (DeathMatchScoreboard scoreboard : IHyrame.get().getScoreboardManager().getScoreboards(DeathMatchScoreboard.class)) {
            scoreboard.update();
        }
    }

    public int getDeathmatchDeaths() {
        return this.deathmatchDeaths;
    }

    public int getJumpDeaths() {
        return this.jumpDeaths;
    }

    public GDData getData() {
        return this.data;
    }

    public void setData(GDData data) {
        this.data = data;
    }

    public GDStatistics getStatistics() {
        return this.statistics;
    }

    public void setStatistics(GDStatistics statistics) {
        this.statistics = statistics;
    }

    public int getYPosition() {
        return this.isOnline() ? this.player.getLocation().getBlockY() : 256;
    }

    @Override
    public String formatNameWithTeam() {
        return this.isOnline() ? IHyriPlayerSession.get(this.uniqueId).getNameWithRank() : this.asHyriPlayer().getNameWithRank();
    }

    public List<Integer> getAchievements() {
        return this.achievements;
    }

}
