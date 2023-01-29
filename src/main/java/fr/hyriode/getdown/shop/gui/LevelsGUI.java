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

        this.addItem(22, new LevelItem(1, 15));
        this.addItem(23, new LevelItem(10, 110));
        this.addItem(31, new LevelItem(20, 200));
        this.addItem(32, new LevelItem(30, 350));
    }

}
