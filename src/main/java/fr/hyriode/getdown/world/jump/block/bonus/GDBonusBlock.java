package fr.hyriode.getdown.world.jump.block.bonus;

import fr.hyriode.getdown.util.BlockTexture;
import fr.hyriode.getdown.world.jump.block.GDJumpBlock;
import fr.hyriode.hyrame.title.Title;
import fr.hyriode.hyrame.utils.Symbols;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by AstFaster
 * on 24/07/2022 at 17:47
 */
public class GDBonusBlock extends GDJumpBlock {

    public GDBonusBlock() {
        super(new BlockTexture(Material.SPONGE), 2.5D);
    }

    @Override
    public void trigger(Block block, Player player) {
        this.setAsUsed(block);

        final GDBonus bonus = this.getRandomBonus();
        final boolean malus = bonus.isMalus();
        final String appender = malus ? ChatColor.RED + Symbols.CROSS_STYLIZED_BOLD : ChatColor.GREEN + Symbols.TICK_BOLD;
        final String bonusText = appender + " " + bonus.getRarity().getDisplayName().getValue(player) + " " + appender;

        Title.sendTitle(player, bonusText, ChatColor.GRAY + bonus.getDisplayName().getValue(player), 5, 40, 5);

        bonus.getHandler().accept(player);

        player.playSound(player.getLocation(), Sound.ITEM_PICKUP, 1.0F, 1.0F);
    }

    private GDBonus getRandomBonus() {
        final double chance = ThreadLocalRandom.current().nextDouble() * 100.0D;
        double cumulative = 0.0D;

        for (GDBonus block : GDBonus.values()) {
            final GDBonus.Rarity rarity = block.getRarity();

            cumulative += rarity.getPercentage() / GDBonus.getBonuses(rarity).size();

            if (chance < cumulative) {
                return block;
            }
        }
        return this.getRandomBonus();
    }

}
