package fr.hyriode.getdown.game.ui.scoreboard;

import fr.hyriode.getdown.HyriGetDown;
import fr.hyriode.getdown.game.GDGame;
import fr.hyriode.hyrame.game.scoreboard.HyriGameScoreboard;
import org.bukkit.entity.Player;

/**
 * Created by AstFaster
 * on 24/07/2022 at 13:50
 */
public abstract class GDScoreboard extends HyriGameScoreboard<GDGame> {

    public GDScoreboard(Player player, String name) {
        super(HyriGetDown.get(), HyriGetDown.get().getGame(), player, name);
    }

    public abstract void update();

}
