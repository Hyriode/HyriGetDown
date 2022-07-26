package fr.hyriode.getdown.config;

import fr.hyriode.hyrame.game.waitingroom.HyriWaitingRoom;
import fr.hyriode.hystia.api.config.IConfig;

/**
 * Created by AstFaster
 * on 23/07/2022 at 11:48
 */
public class GDConfig implements IConfig {

    private final HyriWaitingRoom.Config waitingRoom;

    public GDConfig(HyriWaitingRoom.Config waitingRoom) {
        this.waitingRoom = waitingRoom;
    }

    public HyriWaitingRoom.Config getWaitingRoom() {
        return this.waitingRoom;
    }

}
