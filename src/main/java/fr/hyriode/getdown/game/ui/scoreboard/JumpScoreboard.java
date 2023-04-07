package fr.hyriode.getdown.game.ui.scoreboard;

import fr.hyriode.getdown.HyriGetDown;
import fr.hyriode.getdown.game.GDGamePlayer;
import fr.hyriode.getdown.language.GDMessage;
import fr.hyriode.getdown.world.jump.GDJumpWorld;
import fr.hyriode.hyrame.utils.Symbols;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by AstFaster
 * on 23/07/2022 at 15:09
 */
public class JumpScoreboard extends GDScoreboard {

    public JumpScoreboard(Player player) {
        super(player, "gd-jump");
        this.addCurrentDateLine(0);
        this.addBlankLine(1);
        this.addGameTimeLine(4, GDMessage.SCOREBOARD_TIME.asString(this.player));
        this.addBlankLine(5);
        this.addBlankLine(7);

        this.addUpdatableLines();

        this.addBlankLine(14);
        this.addHostnameLine();
    }

    @Override
    public void update() {
        this.addUpdatableLines();
        this.updateLines();
    }

    private void addUpdatableLines() {
        this.setLine(2, GDMessage.SCOREBOARD_JUMP_MAP.asString(this.player)
                .replace("%map%", this.game.getCurrentWorld().getName())
                .replace("%index%", String.valueOf(this.game.getWorldIndex() + 1))
                .replace("%total%", String.valueOf(HyriGetDown.get().getJumpWorlds().size())));
        this.setLine(3, GDMessage.SCOREBOARD_JUMP_DIFFICULTY.asString(this.player)
                .replace("%difficulty%", ((GDJumpWorld) this.game.getCurrentWorld()).getDifficulty().getDisplayName().getValue(this.player)));
        this.setLine(6, GDMessage.SCOREBOARD_JUMP_COINS.asString(this.player).replace("%coins%", String.valueOf(this.game.getPlayer(this.player).getCoins())));
        this.setLine(8, GDMessage.SCOREBOARD_JUMP_TOP.asString(this.player));

        final List<GDGamePlayer> bestPlayers = this.game.getPlayers().stream().sorted(Comparator.comparingInt(GDGamePlayer::getYPosition)).collect(Collectors.toList());

        for (int i = 0; i < 5; i++) {
            final GDGamePlayer gamePlayer = bestPlayers.size() > i ? bestPlayers.get(i) : null;

            this.setLine(9 + i, Symbols.HYPHEN_BULLET + " " + (
                    gamePlayer == null || !gamePlayer.isOnline() ?
                            ChatColor.values()[i] + "" + ChatColor.RESET + ChatColor.GRAY + "**********" :
                            gamePlayer.formatNameWithTeam() + (gamePlayer.getUniqueId().equals(this.player.getUniqueId()) ?
                                    GDMessage.SCOREBOARD_JUMP_YOU.asString(this.player) :
                                    ChatColor.GRAY + " (" + gamePlayer.getYPosition() + ")")));
        }
    }

    @Override
    public void hide() {
        super.hide();
    }
}
