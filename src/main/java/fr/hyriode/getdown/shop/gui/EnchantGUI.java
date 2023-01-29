package fr.hyriode.getdown.shop.gui;

import fr.hyriode.getdown.shop.EnchantItem;
import org.bukkit.entity.Player;

/**
 * Created by AstFaster
 * on 23/07/2022 at 23:45
 */
public class EnchantGUI extends ShopGUI {

    public EnchantGUI(Player owner) {
        super(owner);

        this.addItem(22, new EnchantItem(1));
        this.addItem(23, new EnchantItem(10));
        this.addItem(31, new EnchantItem(20));
        this.addItem(32, new EnchantItem(30));
    }

}
