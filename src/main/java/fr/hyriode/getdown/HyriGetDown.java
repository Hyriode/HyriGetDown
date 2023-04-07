package fr.hyriode.getdown;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.world.IHyriWorld;
import fr.hyriode.api.world.IHyriWorldManager;
import fr.hyriode.getdown.config.GDConfig;
import fr.hyriode.getdown.dev.DevConfig;
import fr.hyriode.getdown.game.GDGame;
import fr.hyriode.getdown.world.GDWorld;
import fr.hyriode.getdown.world.deathmatch.GDDeathMatchConfig;
import fr.hyriode.getdown.world.deathmatch.GDDeathMatchWorld;
import fr.hyriode.getdown.world.jump.GDJumpWorld;
import fr.hyriode.hyggdrasil.api.server.HyggServer;
import fr.hyriode.hyrame.HyrameLoader;
import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.plugin.IPluginProvider;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * Created by AstFaster
 * on 23/07/2022 at 09:42
 */
public class HyriGetDown extends JavaPlugin {

    public static final String NAME = "GetDown";
    public static final String ID = "getdown";

    public static final String JUMPS_ID = "maps-jump";
    public static final String DEATH_MATCHES_ID = "maps-deathmatches";

    private static HyriGetDown instance;

    private IHyrame hyrame;
    private GDConfig config;
    private GDGame game;

    private List<GDJumpWorld> jumpWorlds;
    private GDDeathMatchWorld deathMatchWorld;

    @Override
    public void onEnable() {
        instance = this;

        final ChatColor color = ChatColor.GOLD;
        final ConsoleCommandSender sender = Bukkit.getConsoleSender();

        sender.sendMessage(color + "   _____      _   _____                      ");
        sender.sendMessage(color + "  / ____|    | | |  __ \\                     ");
        sender.sendMessage(color + " | |  __  ___| |_| |  | | _____      ___ __  ");
        sender.sendMessage(color + " | | |_ |/ _ \\ __| |  | |/ _ \\ \\ /\\ / / '_ \\ ");
        sender.sendMessage(color + " | |__| |  __/ |_| |__| | (_) \\ V  V /| | | |");
        sender.sendMessage(color + "  \\_____|\\___|\\__|_____/ \\___/ \\_/\\_/ |_| |_|");

        log("Starting " + NAME + "...");

        this.hyrame = HyrameLoader.load(new Provider());
        this.config = HyriAPI.get().getConfig().isDevEnvironment() ? new DevConfig() : HyriAPI.get().getServer().getConfig(GDConfig.class);
        this.game = new GDGame(this);

        this.loadWorlds();

        this.hyrame.getGameManager().registerGame(() -> this.game);

        HyriAPI.get().getServer().setState(HyggServer.State.READY);
    }

    private void loadWorlds() {
        this.jumpWorlds = new ArrayList<>();

        if (HyriAPI.get().getConfig().isDevEnvironment()) {
            this.jumpWorlds.add(new GDJumpWorld("first"));
            this.jumpWorlds.add(new GDJumpWorld("second"));
            this.jumpWorlds.add(new GDJumpWorld("third"));
            this.deathMatchWorld = new GDDeathMatchWorld("deathmatch");
        } else {
            final IHyriWorldManager worldManager = HyriAPI.get().getWorldManager();
            final List<IHyriWorld> availableJumps = worldManager.getWorlds(ID, JUMPS_ID)
                    .stream()
                    .filter(IHyriWorld::isEnabled)
                    .collect(Collectors.toList());
            final List<IHyriWorld> availableDeathMatches = worldManager.getWorlds(ID, DEATH_MATCHES_ID)
                    .stream()
                    .filter(IHyriWorld::isEnabled)
                    .collect(Collectors.toList());

            if (availableJumps.size() < 3) {
                if (availableJumps.size() < 1) {
                    log(Level.SEVERE, "There are not enough maps for jumps (1 minimum)!");
                    Bukkit.shutdown();
                    return;
                }

                for (int i = 0; i <= 3 - availableJumps.size(); i++) {
                    availableJumps.add(availableJumps.get(0));
                }
            }

            if (availableDeathMatches.size() < 1) {
                log(Level.SEVERE, "There are not enough maps for death match (1 minimum)!");
                Bukkit.shutdown();
                return;
            }

            Collections.shuffle(availableJumps);
            Collections.shuffle(availableDeathMatches);

            for (int i = 0; i < 3; i++) {
                final GDJumpWorld world = new GDJumpWorld(availableJumps.get(i).getName());

                this.jumpWorlds.add(world);
            }

            this.deathMatchWorld = new GDDeathMatchWorld(availableDeathMatches.get(0).getName());
        }

        for (GDJumpWorld world : this.jumpWorlds) {
            world.load();
        }

        this.deathMatchWorld.load();
    }

    @Override
    public void onDisable() {
        log("Stopping " + NAME + "...");

        this.hyrame.getGameManager().unregisterGame(this.game);
    }

    public static void log(Level level, String message) {
        String prefix = ChatColor.GOLD + "[" + NAME + "] ";

        if (level == Level.SEVERE) {
            prefix += ChatColor.RED;
        } else if (level == Level.WARNING) {
            prefix += ChatColor.YELLOW;
        } else {
            prefix += ChatColor.RESET;
        }

        Bukkit.getConsoleSender().sendMessage(prefix + message);
    }

    public static void log(String msg) {
        log(Level.INFO, msg);
    }

    public IHyrame getHyrame() {
        return this.hyrame;
    }

    public GDConfig getConfiguration() {
        return this.config;
    }

    public GDGame getGame() {
        return this.game;
    }

    public List<GDJumpWorld> getJumpWorlds() {
        return this.jumpWorlds;
    }

    public GDDeathMatchWorld getDeathMatchWorld() {
        return this.deathMatchWorld;
    }

    public static HyriGetDown get() {
        return instance;
    }

    private class Provider implements IPluginProvider {

        private static final String PACKAGE = "fr.hyriode.getdown";

        @Override
        public JavaPlugin getPlugin() {
            return HyriGetDown.this;
        }

        @Override
        public String getId() {
            return "getdown";
        }

        @Override
        public String[] getCommandsPackages() {
            return new String[] {PACKAGE};
        }

        @Override
        public String[] getListenersPackages() {
            return new String[] {PACKAGE};
        }

        @Override
        public String[] getItemsPackages() {
            return new String[] {PACKAGE};
        }

        @Override
        public String getLanguagesPath() {
            return "/lang/";
        }

    }

}
