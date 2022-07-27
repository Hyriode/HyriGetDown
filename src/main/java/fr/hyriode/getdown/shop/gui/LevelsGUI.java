package fr.hyriode.getdown.shop.gui;

import fr.hyriode.getdown.shop.LevelItem;
import org.bukkit.entity.Player;

/**
 * Created by AstFaster
 * on 23/07/2022 at 23:45
 */
public class LevelsGUI extends ShopGUI {

    public LevelsGUI(Player owner) {
        super(owner);

        this.addItem(1, new LevelItem(1, 15));
        this.addItem(3, new LevelItem(10, 110));
        this.addItem(5, new LevelItem(20, 200));
        this.addItem(7, new LevelItem(30, 350));
    }

}
