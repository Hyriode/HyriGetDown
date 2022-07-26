package fr.hyriode.getdown.shop.gui;

import fr.hyriode.getdown.shop.ShopItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * Created by AstFaster
 * on 23/07/2022 at 23:44
 */
public class WeaponsGUI extends ShopGUI {

    public WeaponsGUI(Player owner) {
        super(owner);

        this.addItem(0, new ShopItem(Material.WOOD_SWORD, 50));
        this.addItem(1, new ShopItem(Material.STONE_SWORD, 100));
        this.addItem(2, new ShopItem(Material.IRON_SWORD, 200));
        this.addItem(3, new ShopItem(Material.FISHING_ROD, 200));
        this.addItem(4, new ShopItem(Material.BOW, 100));
        this.addItem(5, new ShopItem(Material.ARROW, 5));
    }

}
