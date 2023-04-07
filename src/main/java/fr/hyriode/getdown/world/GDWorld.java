package fr.hyriode.getdown.world;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.getdown.HyriGetDown;
import fr.hyriode.getdown.dev.DevDeathMatchConfig;
import fr.hyriode.getdown.dev.DevJumpConfig;
import fr.hyriode.getdown.world.jump.GDJumpDifficulty;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by AstFaster
 * on 23/07/2022 at 12:40
 */
public abstract class GDWorld<T extends GDWorldConfig> {

    protected World bukkitWorld;

    protected final Type type;
    protected final String name;
    protected final T config;

    public GDWorld(Type type, String name, Class<T> configClass) {
        this.type = type;
        this.name = name;

        if (HyriAPI.get().getConfig().isDevEnvironment()) {
            this.config = (this.type == Type.JUMP) ? configClass.cast(new DevJumpConfig()) : configClass.cast(new DevDeathMatchConfig());
        } else {
            this.config = HyriAPI.get().getConfigManager().getConfig(configClass, HyriGetDown.ID, this.type.getWorldsId(), this.name);
        }
    }

    public void load() {
        HyriGetDown.log("Loading '" + this.name + "' world (type: " + this.type.name() + ")...");

        final String folderName = this.name + "-" + UUID.randomUUID().toString().split("-")[0];

        if (!HyriAPI.get().getConfig().isDevEnvironment()) {
            HyriAPI.get().getWorldManager().getWorld(HyriAPI.get().getServer().getType(), this.type.getWorldsId(), this.name).load(new File(folderName));
        }

       this.bukkitWorld = new WorldCreator(folderName).createWorld();
    }

    public abstract void teleportPlayers();

    public Type getType() {
        return this.type;
    }

    public String getName() {
        return this.name;
    }

    public T getConfig() {
        return this.config;
    }

    public World asBukkit() {
        return this.bukkitWorld;
    }

    public enum Type {

        JUMP(HyriGetDown.JUMPS_ID),
        DEATH_MATCH(HyriGetDown.DEATH_MATCHES_ID);

        private final String worldsId;

        Type(String worldsId) {
            this.worldsId = worldsId;
        }

        public String getWorldsId() {
            return this.worldsId;
        }

    }

}
