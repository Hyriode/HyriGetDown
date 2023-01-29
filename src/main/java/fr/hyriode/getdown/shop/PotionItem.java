package fr.hyriode.getdown.shop;

import fr.hyriode.getdown.language.GDMessage;
import fr.hyriode.hyrame.item.ItemBuilder;
import fr.hyriode.hyrame.utils.list.ListReplacer;
import fr.hyriode.hyrame.utils.list.ListUtil;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AstFaster
 * on 24/07/2022 at 14:54
 */
public class PotionItem extends ShopItem {

    public PotionItem(Potion potion, int price) {
        super(new ItemBuilder(potion).build(), price);
    }

    @Override
    public ItemStack createItem(Player player) {
        final List<String> lore = new ArrayList<>();

        lore.add("");
        lore.addAll(ListReplacer.replace(GDMessage.ITEM_SHOP_LORE.asList(player), "%price%", String.valueOf(this.price)).list());

        return new ItemBuilder(this.itemStack.clone())
                .withLore(lore)
                .build();
    }

}
