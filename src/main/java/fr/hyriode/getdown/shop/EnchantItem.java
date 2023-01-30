package fr.hyriode.getdown.shop;

import fr.hyriode.getdown.HyriGetDown;
import fr.hyriode.getdown.language.GDMessage;
import fr.hyriode.hyrame.enchantment.EnchantmentGUI;
import fr.hyriode.hyrame.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

/**
 * Created by AstFaster
 * on 24/07/2022 at 22:20
 */
public class EnchantItem extends ShopItem {

    private final int level;

    public EnchantItem(int level) {
        super(new ItemStack(Material.ENCHANTMENT_TABLE, level), -1);
        this.level = level;
    }

    @Override
    public void buy(Player player) {
        new EnchantmentGUI.Builder(HyriGetDown.get(), player)
                .withCloseScenario(EnchantmentGUI.CloseScenario.GIVE_ITEMS)
                .withCosts(this.level)
                .withResetEnchantments(true)
                .withMultipleEnchantments(true)
                .build().open();
    }

    @Override
    public ItemStack createItem(Player player) {
        return new ItemBuilder(super.createItem(player))
                .withName(GDMessage.ITEM_ENCHANTMENT_NAME.asString(player).replace("%level%", String.valueOf(this.level)))
                .withLore(new ArrayList<>())
                .build();
    }

}
