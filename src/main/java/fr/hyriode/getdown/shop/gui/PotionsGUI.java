package fr.hyriode.getdown.shop.gui;

import fr.hyriode.getdown.shop.PotionItem;
import org.bukkit.entity.Player;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

/**
 * Created by AstFaster
 * on 23/07/2022 at 23:45
 */
public class PotionsGUI extends ShopGUI {

    public PotionsGUI(Player owner) {
        super(owner);

        this.addItem(0, new PotionItem(new Potion(PotionType.INSTANT_HEAL), 20));
        this.addItem(1, new PotionItem(new Potion(PotionType.INSTANT_HEAL, 2), 40));
        this.addItem(2, new PotionItem(new Potion(PotionType.SPEED), 30));
        this.addItem(3, new PotionItem(new Potion(PotionType.FIRE_RESISTANCE), 100));
        this.addItem(4, new PotionItem(new Potion(PotionType.INSTANT_DAMAGE).splash(), 125));
    }

}
