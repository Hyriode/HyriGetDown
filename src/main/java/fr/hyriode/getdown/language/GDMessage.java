package fr.hyriode.getdown.language;

import fr.hyriode.api.language.HyriLanguageMessage;
import fr.hyriode.api.player.IHyriPlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;

/**
 * Created by AstFaster
 * on 23/07/2022 at 09:51
 */
public enum GDMessage {

    GAME_DESCRIPTION("game.description"),

    SCOREBOARD_TIME("scoreboard.time"),

    SCOREBOARD_JUMP_MAP("scoreboard.jump.map"),
    SCOREBOARD_JUMP_COINS("scoreboard.jump.coins"),
    SCOREBOARD_JUMP_TOP("scoreboard.jump.top"),
    SCOREBOARD_JUMP_YOU("scoreboard.jump.you"),

    TITLE_JUMP_END("title.jump.end"),
    TITLE_JUMP_NEXT_MAP("title.jump.next-map"),

    MESSAGE_JUMP_DEATH("message.jump.death"),
    MESSAGE_JUMP_END("message.jump.end"),

    SCOREBOARD_DEATH_MATCH_KILLS("scoreboard.death-match.kills"),
    SCOREBOARD_DEATH_MATCH_PLAYERS("scoreboard.death-match.players"),

    MESSAGE_BUY_PHASE_NAME("message.buy-phase.switching"),
    ACTION_BAR_BUY_PHASE_TIME("action-bar.buy-phase.time"),

    GUI_SHOP_NAME("gui.shop.name"),

    GUI_SHOP_CATEGORY_WEAPONS_NAME("gui.shop.category.weapons"),
    GUI_SHOP_CATEGORY_LEATHER_EQUIPMENT_NAME("gui.shop.category.leather-equipment"),
    GUI_SHOP_CATEGORY_IRON_EQUIPMENT_NAME("gui.shop.category.iron-equipment"),
    GUI_SHOP_CATEGORY_DIAMOND_EQUIPMENT_NAME("gui.shop.category.diamond-equipment"),
    GUI_SHOP_CATEGORY_LEVELS_NAME("gui.shop.category.levels"),
    GUI_SHOP_CATEGORY_ENCHANTMENT_NAME("gui.shop.category.enchantment"),
    GUI_SHOP_CATEGORY_FOOD_NAME("gui.shop.category.food"),
    GUI_SHOP_CATEGORY_POTIONS_NAME("gui.shop.category.potions"),

    ITEM_SHOP_LORE("item.shop.lore"),

    ITEM_LEVELS_NAME("item.levels.name"),
    ITEM_ENCHANTMENT_NAME("item.enchantment.name"),

    MESSAGE_SHOP_NOT_ENOUGH_COINS("message.shop.not-enough-coins"),
    MESSAGE_SHOP_NOT_ENOUGH_SPACE("message.shop.not-enough-space"),

    BONUS_ACTION_BAR("bonus.action-bar.display")

    ;

    private HyriLanguageMessage languageMessage;

    private final String key;
    private final BiFunction<IHyriPlayer, String, String> formatter;

    GDMessage(String key, BiFunction<IHyriPlayer, String, String> formatter) {
        this.key = key;
        this.formatter = formatter;
    }

    GDMessage(String key, GDMessage prefix) {
        this.key = key;
        this.formatter = (target, input) -> prefix.asString(target) + input;
    }

    GDMessage(String key) {
        this(key, (target, input) -> input);
    }

    public HyriLanguageMessage asLang() {
        return this.languageMessage == null ? this.languageMessage = HyriLanguageMessage.get(this.key) : this.languageMessage;
    }

    public String asString(IHyriPlayer account) {
        return this.formatter.apply(account, this.asLang().getValue(account));
    }

    public String asString(Player player) {
        return this.asString(IHyriPlayer.get(player.getUniqueId()));
    }

    public void sendTo(Player player) {
        player.sendMessage(this.asString(player));
    }

    public List<String> asList(IHyriPlayer account) {
        return new ArrayList<>(Arrays.asList(this.asString(account).split("\n")));
    }

    public List<String> asList(Player player) {
        return this.asList(IHyriPlayer.get(player.getUniqueId()));
    }

}
