package fr.hyriode.getdown.config;

import fr.hyriode.api.config.IHyriConfig;
import fr.hyriode.hyrame.game.waitingroom.HyriWaitingRoom;

/**
 * Created by AstFaster
 * on 23/07/2022 at 11:48
 */
public class GDConfig implements IHyriConfig {

    private final HyriWaitingRoom.Config waitingRoom;

    public GDConfig(HyriWaitingRoom.Config waitingRoom) {
        this.waitingRoom = waitingRoom;
    }

    public HyriWaitingRoom.Config getWaitingRoom() {
        return this.waitingRoom;
    }

}
