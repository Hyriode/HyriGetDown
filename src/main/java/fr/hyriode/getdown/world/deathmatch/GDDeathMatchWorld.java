package fr.hyriode.getdown.world.deathmatch;

import fr.hyriode.getdown.HyriGetDown;
import fr.hyriode.getdown.game.GDGamePlayer;
import fr.hyriode.getdown.world.GDWorld;
import fr.hyriode.hyrame.utils.LocationWrapper;
import org.bukkit.entity.Player;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.logging.Level;

/**
 * Created by AstFaster
 * on 23/07/2022 at 13:36
 */
public class GDDeathMatchWorld extends GDWorld<GDDeathMatchConfig> {

    private final Queue<LocationWrapper> spawns = new ArrayDeque<>();

    public GDDeathMatchWorld(String name) {
        super(Type.DEATH_MATCH, name, GDDeathMatchConfig.class);

        if (this.config.getPlayerSpawns().size() < 12) {
            HyriGetDown.log(Level.SEVERE, "There are not enough player spawns in the death match config (12 minimum)!");
        }

        final List<LocationWrapper> spawns = this.config.getPlayerSpawns();

        Collections.shuffle(spawns);

        this.spawns.addAll(spawns);
    }

    @Override
    public void teleportPlayers() {
        for (GDGamePlayer gamePlayer : HyriGetDown.get().getGame().getPlayers()) {
            this.teleportPlayer(gamePlayer.getPlayer());
        }
    }

    public void teleportPlayer(Player player) {
        final LocationWrapper spawn = this.spawns.poll();

        if (spawn == null) {
            return;
        }

        player.setFallDistance(0.0F);
        player.teleport(spawn.asBukkit(this.asBukkit()));

        this.spawns.add(spawn);
    }

}
