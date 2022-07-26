package fr.hyriode.getdown.world.jump.block;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

/**
 * Created by AstFaster
 * on 24/07/2022 at 18:43
 */
public class GDVoidBlock extends GDJumpBlock {

    public GDVoidBlock(double percentage) {
        super(null, percentage);
    }

    @Override
    public void trigger(Block block, Player player) {}

}
