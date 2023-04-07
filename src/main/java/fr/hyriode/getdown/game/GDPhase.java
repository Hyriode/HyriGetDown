package fr.hyriode.getdown.game;

import fr.hyriode.api.language.HyriLanguageMessage;
import org.bukkit.entity.Player;

/**
 * Created by AstFaster
 * on 24/07/2022 at 13:42
 */
public enum GDPhase {

    JUMP("jump"),
    BUY("buy"),
    DEATHMATCH("deathmatch");

    private HyriLanguageMessage display;

    private final String id;

    GDPhase(String id) {
        this.id = id;
    }

    public String getDisplay(Player player) {
        return (this.display == null ? this.display = HyriLanguageMessage.get("phase." + this.id + ".display") : this.display).getValue(player);
    }

}
