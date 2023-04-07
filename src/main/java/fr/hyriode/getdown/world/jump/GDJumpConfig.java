package fr.hyriode.getdown.world.jump;

import fr.hyriode.getdown.util.BlockTexture;
import fr.hyriode.getdown.world.GDWorldConfig;
import fr.hyriode.hyrame.utils.AreaWrapper;
import fr.hyriode.hyrame.utils.LocationWrapper;

import java.util.List;

/**
 * Created by AstFaster
 * on 23/07/2022 at 13:31
 */
public class GDJumpConfig extends GDWorldConfig {

    private final LocationWrapper spawn;
    private final List<BlockTexture> blocks;
    private final AreaWrapper area;
    private final int maximumAttackHeight;
    private final int endHeight;

    public GDJumpConfig(LocationWrapper spawn, List<BlockTexture> blocks, AreaWrapper area, int maximumAttackHeight, int endHeight) {
        this.spawn = spawn;
        this.blocks = blocks;
        this.area = area;
        this.maximumAttackHeight = maximumAttackHeight;
        this.endHeight = endHeight;
    }

    public LocationWrapper getSpawn() {
        return this.spawn;
    }

    public List<BlockTexture> getBlocks() {
        return this.blocks;
    }

    public AreaWrapper getArea() {
        return this.area;
    }

    public int getMaximumAttackHeight() {
        return this.maximumAttackHeight;
    }

    public int getEndHeight() {
        return this.endHeight;
    }

}
