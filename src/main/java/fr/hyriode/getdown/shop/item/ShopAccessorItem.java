package fr.hyriode.getdown.shop.item;

import fr.hyriode.api.language.HyriLanguageMessage;
import fr.hyriode.getdown.HyriGetDown;
import fr.hyriode.getdown.shop.gui.MainShopGUI;
import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.item.HyriItem;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;

/**
 * Created by AstFaster
 * on 23/07/2022 at 23:27
 */
public class ShopAccessorItem extends HyriItem<HyriGetDown> {

    public ShopAccessorItem(HyriGetDown plugin) {
        super(plugin, "shop", () -> HyriLanguageMessage.get("item.shop.display"), () -> HyriLanguageMessage.get("item.shop.lore"), Material.GOLD_INGOT);
    }

    @Override
    public void onRightClick(IHyrame hyrame, PlayerInteractEvent event) {
        new MainShopGUI(event.getPlayer()).open();
    }

}
