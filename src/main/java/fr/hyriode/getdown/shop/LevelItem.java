package fr.hyriode.getdown.shop;

import fr.hyriode.getdown.HyriGetDown;
import fr.hyriode.getdown.game.GDGamePlayer;
import fr.hyriode.getdown.language.GDMessage;
import fr.hyriode.hyrame.item.ItemBuilder;
import fr.hyriode.hyrame.utils.ExperienceUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Created by AstFaster
 * on 24/07/2022 at 01:27
 */
public class LevelItem extends ShopItem {

    private final int level;

    public LevelItem(int level, int price) {
        super(new ItemStack(Material.EXP_BOTTLE, level), price);
        this.level = level;
    }

    @Override
    public ItemStack createItem(Player player) {
        return new ItemBuilder(super.createItem(player))
                .withName(GDMessage.ITEM_LEVELS_NAME.asString(player).replace("%level%", String.valueOf(this.level)))
                .build();
    }

    @Override
    public void buy(Player player) {
        final GDGamePlayer gamePlayer = HyriGetDown.get().getGame().getPlayer(player);

        if (gamePlayer == null) {
            return;
        }

        if (gamePlayer.getCoins() < this.price) {
            player.sendMessage(GDMessage.MESSAGE_SHOP_NOT_ENOUGH_COINS.asString(player));
            return;
        }

        gamePlayer.removeCoins(this.price);

        player.giveExp(ExperienceUtil.getExpToLevel(this.level) + 1);
    }
}
