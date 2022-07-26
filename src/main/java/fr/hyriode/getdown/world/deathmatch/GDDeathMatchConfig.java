package fr.hyriode.getdown.world.deathmatch;

import fr.hyriode.getdown.world.GDWorldConfig;
import fr.hyriode.hyrame.utils.LocationWrapper;

import java.util.List;

/**
 * Created by AstFaster
 * on 23/07/2022 at 13:32
 */
public class GDDeathMatchConfig extends GDWorldConfig {

    private final List<LocationWrapper> playerSpawns;

    public GDDeathMatchConfig(List<LocationWrapper> playerSpawns) {
        this.playerSpawns = playerSpawns;
    }

    public List<LocationWrapper> getPlayerSpawns() {
        return this.playerSpawns;
    }

}
