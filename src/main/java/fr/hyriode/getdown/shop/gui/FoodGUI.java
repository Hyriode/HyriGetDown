package fr.hyriode.getdown.shop.gui;

import fr.hyriode.getdown.shop.ShopItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Created by AstFaster
 * on 23/07/2022 at 23:45
 */
public class FoodGUI extends ShopGUI {

    public FoodGUI(Player owner) {
        super(owner);

        this.addItem(22, new ShopItem(new ItemStack(Material.APPLE, 4), 2));
        this.addItem(23, new ShopItem(new ItemStack(Material.COOKED_BEEF, 8), 3));
        this.addItem(31, new ShopItem(Material.CAKE, 4));
        this.addItem(32, new ShopItem(Material.GOLDEN_APPLE, 20));
    }

}
