package fr.hyriode.getdown.shop;

import fr.hyriode.getdown.HyriGetDown;
import fr.hyriode.getdown.game.GDGamePlayer;
import fr.hyriode.getdown.language.GDMessage;
import fr.hyriode.hyrame.item.ItemBuilder;
import fr.hyriode.hyrame.utils.ItemUtil;
import fr.hyriode.hyrame.utils.list.ListReplacer;
import fr.hyriode.hyrame.utils.list.ListUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Created by AstFaster
 * on 23/07/2022 at 23:48
 */
public class ShopItem {

    protected final ItemStack itemStack;
    protected final int price;

    public ShopItem(ItemStack itemStack, int price) {
        this.itemStack = itemStack;
        this.price = price;
    }

    public ShopItem(Material material, int price) {
        this(new ItemStack(material), price);
    }

    public void buy(Player player) {
        final GDGamePlayer gamePlayer = HyriGetDown.get().getGame().getPlayer(player);

        if (gamePlayer == null) {
            return;
        }

        if (gamePlayer.getCoins() < this.price) {
            player.sendMessage(GDMessage.MESSAGE_SHOP_NOT_ENOUGH_COINS.asString(player));
            return;
        }

        if (ItemUtil.addItemInPlayerInventory(this.itemStack.clone(), player)) {
            gamePlayer.removeCoins(this.price);
        } else {
            player.sendMessage(GDMessage.MESSAGE_SHOP_NOT_ENOUGH_SPACE.asString(player));
        }
    }

    public ItemStack createItem(Player player) {
        return new ItemBuilder(this.itemStack.clone())
                .withLore(ListReplacer.replace(GDMessage.ITEM_SHOP_LORE.asList(player), "%price%", String.valueOf(this.price)).list())
                .withAllItemFlags()
                .build();
    }

    public ItemStack getItemStack() {
        return this.itemStack;
    }

    public int getPrice() {
        return this.price;
    }

}
