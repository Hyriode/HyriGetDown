package fr.hyriode.getdown.game.ui;

import fr.hyriode.getdown.HyriGetDown;
import fr.hyriode.getdown.game.GDGamePlayer;
import fr.hyriode.getdown.language.GDMessage;
import fr.hyriode.getdown.util.LocationUtil;
import fr.hyriode.hyrame.actionbar.ActionBar;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by AstFaster
 * on 23/03/2023 at 18:42
 */
public class PlayerTracker extends BukkitRunnable {

    private final GDGamePlayer gamePlayer;

    public PlayerTracker(GDGamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }

    @Override
    public void run() {
        if (this.gamePlayer == null || !this.gamePlayer.isOnline()) {
            return;
        }

        final Player player = this.gamePlayer.getPlayer();
        final NearestPlayer nearestPlayer = this.getNearestPlayer();

        if (nearestPlayer == null) {
            this.cancel();
            return;
        }

        new ActionBar(GDMessage.PLAYER_TRACKER_BAR.asString(player)
                .replace("%player%", nearestPlayer.asGamePlayer().getPlayer().getName())
                .replace("%arrow%", LocationUtil.getArrow(player.getLocation(), nearestPlayer.asGamePlayer().getPlayer().getLocation().clone()))
                .replace("%meters%", String.valueOf((int) nearestPlayer.getDistance())))
                .send(player);
    }

    private NearestPlayer getNearestPlayer() {
        final Location playerLocation = this.gamePlayer.getPlayer().getLocation().clone();

        playerLocation.setY(0.0D);

        NearestPlayer nearestPlayer = null;
        for (GDGamePlayer target : HyriGetDown.get().getGame().getPlayers()) {
            if (!target.isOnline() || target.isSpectator() || this.gamePlayer.getTeam().contains(target)) {
                continue;
            }

            final Location targetLocation = target.getPlayer().getLocation().clone();

            if (!targetLocation.getWorld().equals(playerLocation.getWorld())) {
                continue;
            }

            targetLocation.setY(0.0D);

            final double distance = targetLocation.distance(playerLocation);

            if (nearestPlayer == null || distance < nearestPlayer.getDistance()) {
                nearestPlayer = new NearestPlayer(target, distance);
            }
        }
        return nearestPlayer;
    }

    public static class NearestPlayer {

        private final GDGamePlayer gamePlayer;
        private final double distance;

        public NearestPlayer(GDGamePlayer gamePlayer, double distance) {
            this.gamePlayer = gamePlayer;
            this.distance = distance;
        }

        public GDGamePlayer asGamePlayer() {
            return this.gamePlayer;
        }

        public double getDistance() {
            return this.distance;
        }

    }

}

