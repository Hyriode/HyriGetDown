package fr.hyriode.getdown.shop.gui;

import fr.hyriode.getdown.shop.ShopItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * Created by AstFaster
 * on 23/07/2022 at 23:45
 */
public class ArmorGUI extends ShopGUI {

    public ArmorGUI(Player owner) {
        super(owner);

        this.addItem(12, new ShopItem(Material.LEATHER_HELMET, 20));
        this.addItem(13, new ShopItem(Material.CHAINMAIL_HELMET, 35));
        this.addItem(14, new ShopItem(Material.IRON_HELMET, 50));
        this.addItem(15, new ShopItem(Material.DIAMOND_HELMET, 80));

        this.addItem(21, new ShopItem(Material.LEATHER_CHESTPLATE, 36));
        this.addItem(22, new ShopItem(Material.CHAINMAIL_CHESTPLATE, 63));
        this.addItem(23, new ShopItem(Material.IRON_CHESTPLATE, 90));
        this.addItem(24, new ShopItem(Material.DIAMOND_CHESTPLATE, 150));

        this.addItem(30, new ShopItem(Material.LEATHER_LEGGINGS, 28));
        this.addItem(31, new ShopItem(Material.CHAINMAIL_LEGGINGS, 49));
        this.addItem(32, new ShopItem(Material.IRON_LEGGINGS, 70));
        this.addItem(33, new ShopItem(Material.DIAMOND_LEGGINGS, 120));

        this.addItem(39, new ShopItem(Material.LEATHER_BOOTS, 20));
        this.addItem(40, new ShopItem(Material.CHAINMAIL_BOOTS, 35));
        this.addItem(41, new ShopItem(Material.IRON_BOOTS, 50));
        this.addItem(42, new ShopItem(Material.DIAMOND_BOOTS, 80));
    }

}
