package fr.hyriode.getdown.game;

import fr.hyriode.getdown.HyriGetDown;
import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.game.waitingroom.HyriWaitingRoom;
import org.bukkit.Material;

/**
 * Created by AstFaster
 * on 23/07/2022 at 12:54
 */
public class GDWaitingRoom extends HyriWaitingRoom {

    public GDWaitingRoom(HyriGame<?> game) {
        super(game, Material.SEA_LANTERN, HyriGetDown.get().getConfiguration().getWaitingRoom());
        this.clearBlocks = false;
    }

}
