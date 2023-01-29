package fr.hyriode.getdown.shop.gui;

import fr.hyriode.getdown.shop.ShopItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Created by AstFaster
 * on 23/07/2022 at 23:44
 */
public class WeaponsGUI extends ShopGUI {

    public WeaponsGUI(Player owner) {
        super(owner);

        this.addItem(21, new ShopItem(Material.WOOD_SWORD, 50));
        this.addItem(22, new ShopItem(Material.STONE_SWORD, 100));
        this.addItem(23, new ShopItem(Material.IRON_SWORD, 200));
        this.addItem(24, new ShopItem(Material.DIAMOND_SWORD, 300));
        this.addItem(30, new ShopItem(Material.FISHING_ROD, 250));
        this.addItem(31, new ShopItem(Material.BOW, 100));
        this.addItem(32, new ShopItem(new ItemStack(Material.ARROW, 3), 5));
    }

}
