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

        this.addItem(2, new EnchantItem(10));
        this.addItem(4, new EnchantItem(20));
        this.addItem(6, new EnchantItem(30));
    }

}
