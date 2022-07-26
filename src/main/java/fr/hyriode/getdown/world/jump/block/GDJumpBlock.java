package fr.hyriode.getdown.world.jump.block;

import fr.hyriode.getdown.HyriGetDown;
import fr.hyriode.getdown.util.BlockTexture;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

/**
 * Created by AstFaster
 * on 24/07/2022 at 17:03
 */
public abstract class GDJumpBlock {

    public static final String METADATA = "GD-JumpBlock";

    protected final BlockTexture texture;
    protected final double percentage;

    public GDJumpBlock(BlockTexture texture, double percentage) {
        this.texture = texture;
        this.percentage = percentage;
    }

    public abstract void trigger(Block block, Player player);

    protected void setAsUsed(Block block) {
        block.setType(Material.BEDROCK);
        block.removeMetadata(METADATA, HyriGetDown.get());
    }

    public double getPercentage() {
        return this.percentage;
    }

    public BlockTexture getTexture() {
        return this.texture;
    }

}
