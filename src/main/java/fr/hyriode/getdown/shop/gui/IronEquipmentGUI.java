package fr.hyriode.getdown.shop.gui;

import fr.hyriode.getdown.shop.ShopItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * Created by AstFaster
 * on 23/07/2022 at 23:45
 */
public class IronEquipmentGUI extends ShopGUI {

    public IronEquipmentGUI(Player owner) {
        super(owner);

        this.addItem(0, new ShopItem(Material.IRON_HELMET, 50));
        this.addItem(1, new ShopItem(Material.IRON_CHESTPLATE, 90));
        this.addItem(2, new ShopItem(Material.IRON_LEGGINGS, 65));
        this.addItem(3, new ShopItem(Material.IRON_BOOTS, 65));
    }

}
