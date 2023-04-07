package fr.hyriode.getdown.game.ui.scoreboard;

import fr.hyriode.getdown.HyriGetDown;
import fr.hyriode.getdown.game.GDGame;
import fr.hyriode.getdown.language.GDMessage;
import fr.hyriode.getdown.world.GDWorld;
import fr.hyriode.hyrame.utils.Symbols;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created by AstFaster
 * on 23/07/2022 at 18:26
 */
public class SpectatorScoreboard extends GDScoreboard {

    public SpectatorScoreboard(Player player) {
        super(player, "gd-spectator");

        this.addCurrentDateLine(0);
        this.addBlankLine(1);
        this.addBlankLine(4);
        this.addUpdatableLines();
        this.addGameTimeLine(6, GDMessage.SCOREBOARD_TIME.asString(this.player));
        this.addBlankLine(7);
        this.addHostnameLine();
    }

    @Override
    public void update() {
        this.addUpdatableLines();
        this.updateLines();
    }

    private void addUpdatableLines() {
        final GDGame game = HyriGetDown.get().getGame();
        final GDWorld<?> world = game.getCurrentWorld();

        this.setLine(2, GDMessage.SCOREBOARD_SPECTATOR_PHASE.asString(this.player).replace("%phase%", String.valueOf(game.getCurrentPhase().getDisplay(this.player))));
        this.setLine(3, GDMessage.SCOREBOARD_SPECTATOR_MAP.asString(this.player).replace("%map%", world == null ? ChatColor.RED + Symbols.CROSS_STYLIZED_BOLD : String.valueOf(world.getName())));
        this.setLine(5, GDMessage.SCOREBOARD_SPECTATOR_PLAYERS.asString(this.player).replace("%players%", String.valueOf(game.getAlivePlayers().size())));
    }

}
