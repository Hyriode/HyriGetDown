package fr.hyriode.getdown.dev;

import fr.hyriode.getdown.config.GDConfig;
import fr.hyriode.hyrame.game.waitingroom.HyriWaitingRoom;
import fr.hyriode.hyrame.utils.LocationWrapper;

/**
 * Created by AstFaster
 * on 23/07/2022 at 11:49
 */
public class DevConfig extends GDConfig {

    public DevConfig() {
        super(new HyriWaitingRoom.Config(
                new LocationWrapper(0.5, 70, 0.5, 90, 0),
                new LocationWrapper(-68, 135, 55),
                new LocationWrapper(30, 51, -55),
                new LocationWrapper(-4.5, 70, 3.5, -125, 0)
                )
        );
    }

}
