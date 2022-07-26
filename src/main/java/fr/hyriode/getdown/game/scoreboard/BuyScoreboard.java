package fr.hyriode.getdown.game.scoreboard;

import fr.hyriode.getdown.language.GDMessage;
import org.bukkit.entity.Player;

/**
 * Created by AstFaster
 * on 24/07/2022 at 13:46
 */
public class BuyScoreboard extends GDScoreboard {

    public BuyScoreboard(Player player) {
        super(player, "gd-buy");
        this.addCurrentDateLine(0);
        this.addBlankLine(1);
        this.addGameTimeLine(2, GDMessage.SCOREBOARD_TIME.asString(this.player));
        this.addBlankLine(3);

        this.addUpdatableLines();

        this.addBlankLine(5);
        this.addHostnameLine();
    }

    @Override
    public void update() {
        this.addUpdatableLines();
        this.updateLines();
    }

    private void addUpdatableLines() {
        this.setLine(4, GDMessage.SCOREBOARD_JUMP_COINS.asString(this.player).replace("%coins%", String.valueOf(this.game.getPlayer(this.player).getCoins())));
    }

}
