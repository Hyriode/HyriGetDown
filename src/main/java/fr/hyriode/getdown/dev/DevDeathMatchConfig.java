package fr.hyriode.getdown.dev;

import fr.hyriode.getdown.world.deathmatch.GDDeathMatchConfig;
import fr.hyriode.hyrame.utils.LocationWrapper;

import java.util.Arrays;

/**
 * Created by AstFaster
 * on 23/07/2022 at 20:07
 */
public class DevDeathMatchConfig extends GDDeathMatchConfig {

    public DevDeathMatchConfig() {
        super(Arrays.asList(
                new LocationWrapper(10, 101, -18), new LocationWrapper(-18, 101, -18),
                new LocationWrapper(5, 101, 18), new LocationWrapper(6, 101, -18),
                new LocationWrapper(9, 101, 11), new LocationWrapper(17, 101, 15)));
    }

}
