package fr.hyriode.getdown.game.scoreboard;

import fr.hyriode.getdown.language.GDMessage;
import org.bukkit.entity.Player;

/**
 * Created by AstFaster
 * on 23/07/2022 at 18:26
 */
public class DeathMatchScoreboard extends GDScoreboard {

    public DeathMatchScoreboard(Player player) {
        super(player, "gd-deathmatch");
        this.addCurrentDateLine(0);
        this.addBlankLine(1);
        this.addBlankLine(3);
        this.addUpdatableLines();
        this.addGameTimeLine(5, GDMessage.SCOREBOARD_TIME.asString(this.player));
        this.addBlankLine(6);
        this.addHostnameLine();
    }

    @Override
    public void update() {
        this.addUpdatableLines();
        this.updateLines();
    }

    private void addUpdatableLines() {
        this.setLine(2, GDMessage.SCOREBOARD_DEATH_MATCH_KILLS.asString(this.player).replace("%kills%", String.valueOf(this.game.getPlayer(this.player).getKills())));
        this.setLine(4, GDMessage.SCOREBOARD_DEATH_MATCH_PLAYERS.asString(this.player).replace("%players%", String.valueOf(this.game.getPlayers().size() - this.game.getDeadPlayers().size())));
    }

}
