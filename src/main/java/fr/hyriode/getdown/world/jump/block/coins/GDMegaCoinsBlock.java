package fr.hyriode.getdown.world.jump.block.coins;

import fr.hyriode.getdown.util.BlockTexture;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

/**
 * Created by AstFaster
 * on 24/07/2022 at 17:09
 */
public class GDMegaCoinsBlock extends GDCoinsBlock {

    public GDMegaCoinsBlock() {
        super(new BlockTexture(Material.DIAMOND_BLOCK), 0.2D, 90);
    }

    @Override
    public void trigger(Block block, Player player) {
        super.trigger(block, player);

        player.playSound(player.getLocation(), Sound.FIREWORK_TWINKLE, 1.0F, 1.0F);
    }

}
