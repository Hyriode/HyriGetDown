package fr.hyriode.getdown.dev;

import fr.hyriode.getdown.util.BlockTexture;
import fr.hyriode.getdown.world.jump.GDJumpConfig;
import fr.hyriode.getdown.world.jump.GDJumpDifficulty;
import fr.hyriode.hyrame.utils.AreaWrapper;
import fr.hyriode.hyrame.utils.LocationWrapper;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Created by AstFaster
 * on 23/07/2022 at 20:00
 */
public class DevJumpConfig extends GDJumpConfig {

    public DevJumpConfig() {
        super(
                new LocationWrapper(0.5, 101, 0.5, 0, 0),
                textures(),
                new AreaWrapper(new LocationWrapper(-35, 0, -35), new LocationWrapper(35, 98, 35)),
                100, 1
        );
    }

    private static List<BlockTexture> textures() {
        final List<BlockTexture> textures = new ArrayList<>();
        final Consumer<Byte> clay = data -> textures.add(new BlockTexture(Material.STAINED_CLAY, data));

        clay.accept((byte) 1);
        clay.accept((byte) 3);
        clay.accept((byte) 4);
        clay.accept((byte) 5);
        clay.accept((byte) 6);
        clay.accept((byte) 9);
        clay.accept((byte) 11);
        clay.accept((byte) 13);
        clay.accept((byte) 14);

        return textures;
    }

}
