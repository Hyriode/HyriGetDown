package fr.hyriode.getdown.shop.item;

import fr.hyriode.api.language.HyriLanguageMessage;
import fr.hyriode.getdown.HyriGetDown;
import fr.hyriode.getdown.shop.gui.MainShopGUI;
import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.item.HyriItem;
import fr.hyriode.hyrame.item.ItemNBT;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

/**
 * Created by AstFaster
 * on 23/07/2022 at 23:27
 */
public class ShopAccessorItem extends HyriItem<HyriGetDown> {

    public static final String NBT_TAG = "GetDown-Store";

    public ShopAccessorItem(HyriGetDown plugin) {
        super(plugin, "shop", () -> HyriLanguageMessage.get("item.shop.display"), null, Material.GOLD_INGOT);
    }

    @Override
    public ItemStack onPreGive(IHyrame hyrame, Player player, int slot, ItemStack itemStack) {
        return new ItemNBT(itemStack).setBoolean(NBT_TAG, true).build();
    }

    @Override
    public void onRightClick(IHyrame hyrame, PlayerInteractEvent event) {
        new MainShopGUI(event.getPlayer()).open();
    }

}
