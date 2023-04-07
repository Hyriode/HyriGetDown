package fr.hyriode.getdown.game.achievement;

import fr.hyriode.api.language.HyriLanguageMessage;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum GDAchievement {

    NO_DEATHS(Material.TNT, "no-deaths", 150),
    NO_DAMAGES(Material.FLINT_AND_STEEL, "no-damages", 200),

    ;

    private HyriLanguageMessage display;
    private HyriLanguageMessage description;

    private final ItemStack icon;
    private final String name;
    private final int coins;

    GDAchievement(ItemStack icon, String name, int coins) {
        this.icon = icon;
        this.name = name;
        this.coins = coins;
    }

    GDAchievement(Material icon, String name, int coins) {
        this(new ItemStack(icon), name, coins);
    }

    public static GDAchievement getById(int id) {
        for (GDAchievement achievement : GDAchievement.values()) {
            if (achievement.getId() == id) {
                return achievement;
            }
        }
        return null;
    }

    public ItemStack getIcon() {
        return this.icon.clone();
    }

    public int getId() {
        return this.ordinal();
    }

    public String getDisplay(Player player) {
        return (this.display == null ? this.display = HyriLanguageMessage.get("achievement." + this.name + ".name") : this.display).getValue(player);
    }

    public List<String> getDescription(Player player) {
        return new ArrayList<>(Arrays.asList((this.description == null ? this.description = HyriLanguageMessage.get("achievement." + this.name + ".description") : this.description).getValue(player).split("\n")));
    }

    public int getCoins() {
        return this.coins;
    }

}
