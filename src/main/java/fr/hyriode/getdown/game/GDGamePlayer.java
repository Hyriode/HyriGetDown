package fr.hyriode.getdown.game;

import fr.hyriode.getdown.HyriGetDown;
import fr.hyriode.getdown.game.scoreboard.BuyScoreboard;
import fr.hyriode.getdown.game.scoreboard.DeathMatchScoreboard;
import fr.hyriode.getdown.game.scoreboard.GDScoreboard;
import fr.hyriode.getdown.game.scoreboard.JumpScoreboard;
import fr.hyriode.getdown.language.GDMessage;
import fr.hyriode.getdown.shop.item.ShopAccessorItem;
import fr.hyriode.getdown.world.GDWorld;
import fr.hyriode.getdown.world.jump.GDJumpWorld;
import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.game.HyriGamePlayer;
import fr.hyriode.hyrame.scoreboard.HyriScoreboard;
import fr.hyriode.hyrame.title.Title;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by AstFaster
 * on 23/07/2022 at 11:34
 */
public class GDGamePlayer extends HyriGamePlayer {

    private int coins;
    private int kills;

    private final GDGame game;

    public GDGamePlayer(Player player) {
        super(player);
        this.game = HyriGetDown.get().getGame();
    }

    public void onJumpsStart() {
        new JumpScoreboard(this.player).show();
    }

    public void onBuyStart() {
        new BuyScoreboard(this.player).show();

        IHyrame.get().getItemManager().giveItem(this.player, 4, ShopAccessorItem.class);
    }

    public void onDeathMatchStart() {
        new DeathMatchScoreboard(this.player).show();
    }

    public void onReconnect() {
        final GDJumpWorld world = (GDJumpWorld) this.game.getCurrentWorld();

        this.player.teleport(world.getConfig().getSpawn().asBukkit(world.asBukkit()));

        this.onJumpsStart();
        this.onCoinsUpdated();
    }

    public void onDeath(double damage) {
        final GDWorld<?> world = this.game.getCurrentWorld();

        if (world == null) {
            return;
        }

        if (world.getType() == GDWorld.Type.JUMP) {
            final GDJumpWorld jumpWorld = (GDJumpWorld) world;
            final int removedCoins = (int) (20 + damage / this.player.getMaxHealth() * ThreadLocalRandom.current().nextInt(10, 15));

            this.game.getPlayers().forEach(target -> GDMessage.MESSAGE_JUMP_DEATH.asString(target.getPlayer())
                    .replace("%player%", this.asHyriPlayer().getNameWithRank()));

            this.removeCoins(removedCoins);

            this.player.setHealth(20.0F);
            this.player.teleport(jumpWorld.getConfig().getSpawn().asBukkit(jumpWorld.asBukkit()));

            Title.sendTitle(this.player, ChatColor.RED + "- " + removedCoins + " Coins", "", 5, 40, 5);
        }
    }

    private void onCoinsUpdated() {
        this.game.getCoinsObjective().getScore(this.player.getName()).setScore(this.coins);

        final HyriScoreboard scoreboard = IHyrame.get().getScoreboardManager().getPlayerScoreboard(this.player);

        if (scoreboard instanceof GDScoreboard) {
            ((GDScoreboard) scoreboard).update();
        }
    }

    private void onKillsUpdated() {
        for (DeathMatchScoreboard scoreboard : IHyrame.get().getScoreboardManager().getScoreboards(DeathMatchScoreboard.class)) {
            scoreboard.update();
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

    public int getKills() {
        return this.kills;
    }

    public void addKill() {
        this.kills++;

        this.onKillsUpdated();
    }

    public int getYPosition() {
        return this.isOnline() ? this.player.getLocation().getBlockY() : 256;
    }

    @Override
    public String formatNameWithTeam() {
        return this.asHyriPlayer().getNameWithRank();
    }

}
