package fr.hyriode.getdown.game;

import fr.hyriode.api.language.HyriLanguageMessage;
import fr.hyriode.api.leaderboard.HyriLeaderboardScope;
import fr.hyriode.api.leveling.NetworkLeveling;
import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.getdown.HyriGetDown;
import fr.hyriode.getdown.api.GDStatistics;
import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.game.waitingroom.HyriWaitingRoom;
import fr.hyriode.hyrame.utils.DurationFormatter;
import fr.hyriode.hyrame.utils.Symbols;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.function.Function;

/**
 * Created by AstFaster
 * on 23/07/2022 at 12:54
 */
public class GDWaitingRoom extends HyriWaitingRoom {

    private static final Function<String, HyriLanguageMessage> LANG_DATA = name -> HyriLanguageMessage.get("waiting-room.npc." + name);

    public GDWaitingRoom(HyriGame<?> game) {
        super(game, Material.SEA_LANTERN, HyriGetDown.get().getConfiguration().getWaitingRoom());
        this.clearBlocks = false;

        this.addLeaderboard(new Leaderboard(NetworkLeveling.LEADERBOARD_TYPE, "getdown-experience",
                player -> HyriLanguageMessage.get("leaderboard.experience.display").getValue(player))
                .withScopes(HyriLeaderboardScope.DAILY, HyriLeaderboardScope.WEEKLY, HyriLeaderboardScope.MONTHLY));
        this.addLeaderboard(new Leaderboard(HyriGetDown.ID, "kills", player -> HyriLanguageMessage.get("leaderboard.kills.display").getValue(player)));
        this.addLeaderboard(new Leaderboard(HyriGetDown.ID, "victories", player -> HyriLanguageMessage.get("leaderboard.victories.display").getValue(player)));
        this.addLeaderboard(new Leaderboard(HyriGetDown.ID, "successful-jumps", player -> HyriLanguageMessage.get("leaderboard.successful-jumps.display").getValue(player)));

        this.addStatistics(22, GDGameType.NORMAL);
    }

    private void addStatistics(int slot, GDGameType gameType) {
        final NPCCategory normal = new NPCCategory(HyriLanguageMessage.from(gameType.getDisplayName()));

        normal.addData(new NPCData(LANG_DATA.apply("kills"), account -> String.valueOf(this.getStatistics(gameType, account).getKills())));
        normal.addData(new NPCData(LANG_DATA.apply("jump-deaths"), account -> String.valueOf(this.getStatistics(gameType, account).getJumpDeaths())));
        normal.addData(new NPCData(LANG_DATA.apply("deathmatch-deaths"), account -> String.valueOf(this.getStatistics(gameType, account).getDeathmatchDeaths())));
        normal.addData(new NPCData(LANG_DATA.apply("successful-jumps"), account -> String.valueOf(this.getStatistics(gameType, account).getSuccessfulJumps())));
        normal.addData(new NPCData(LANG_DATA.apply("earned-coins"), account -> String.valueOf(this.getStatistics(gameType, account).getEarnedCoins())));
        normal.addData(new NPCData(LANG_DATA.apply("victories"), account -> String.valueOf(this.getStatistics(gameType, account).getVictories())));
        normal.addData(new NPCData(LANG_DATA.apply("games-played"), account -> String.valueOf(this.getStatistics(gameType, account).getGamesPlayed())));
        normal.addData(new NPCData(LANG_DATA.apply("played-time"), account -> this.formatPlayedTime(account, account.getStatistics().getPlayTime(HyriGetDown.ID + "#" + gameType.getName()))));

        this.addNPCCategory(slot, normal);
    }

    private String formatPlayedTime(IHyriPlayer account, long playedTime) {
        return playedTime < 1000 ? ChatColor.RED + Symbols.CROSS_STYLIZED_BOLD : new DurationFormatter()
                .withSeconds(false)
                .format(account.getSettings().getLanguage(), playedTime);
    }

    private GDStatistics.Data getStatistics(GDGameType gameType, IHyriPlayer account) {
        return ((GDGamePlayer) this.game.getPlayer(account.getUniqueId())).getStatistics().getData(gameType);
    }

}
