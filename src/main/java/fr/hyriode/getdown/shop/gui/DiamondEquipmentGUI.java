package fr.hyriode.getdown.shop.gui;

import fr.hyriode.getdown.shop.ShopItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * Created by AstFaster
 * on 23/07/2022 at 23:45
 */
public class DiamondEquipmentGUI extends ShopGUI {

    public DiamondEquipmentGUI(Player owner) {
        super(owner);

        this.addItem(0, new ShopItem(Material.DIAMOND_HELMET, 70));
        this.addItem(1, new ShopItem(Material.DIAMOND_CHESTPLATE, 140));
        this.addItem(2, new ShopItem(Material.DIAMOND_LEGGINGS, 90));
        this.addItem(3, new ShopItem(Material.DIAMOND_BOOTS, 90));
    }

}
