package fr.hyriode.getdown.shop.gui;

import fr.hyriode.getdown.shop.ShopItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * Created by AstFaster
 * on 23/07/2022 at 23:45
 */
public class LeatherEquipmentGUI extends ShopGUI {

    public LeatherEquipmentGUI(Player owner) {
        super(owner);

        this.addItem(0, new ShopItem(Material.LEATHER_HELMET, 20));
        this.addItem(1, new ShopItem(Material.LEATHER_CHESTPLATE, 50));
        this.addItem(2, new ShopItem(Material.LEATHER_LEGGINGS, 35));
        this.addItem(3, new ShopItem(Material.LEATHER_BOOTS, 35));
    }

}
