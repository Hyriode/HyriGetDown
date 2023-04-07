package fr.hyriode.getdown.world.jump.block.bonus;

import fr.hyriode.api.language.HyriLanguageMessage;
import fr.hyriode.getdown.HyriGetDown;
import fr.hyriode.getdown.language.GDMessage;
import fr.hyriode.getdown.world.GDWorld;
import fr.hyriode.getdown.world.jump.GDJumpWorld;
import fr.hyriode.hyrame.actionbar.ActionBar;
import fr.hyriode.hyrame.tablist.ITabListManager;
import fr.hyriode.hyrame.utils.block.Cuboid;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Created by AstFaster
 * on 25/07/2022 at 16:42
 */
public enum GDBonus {

    REDUCED_DAMAGE("reduced-damage", Rarity.LEGENDARY, new Consumer<Player>(){

        private Player player;

        @Override
        public void accept(Player player) {
            this.player = player;

            final Handler handler = new Handler();

            addDurationBar(GDBonus.REDUCED_DAMAGE.getDisplayName(), player, 5);

            Bukkit.getServer().getPluginManager().registerEvents(handler, HyriGetDown.get());
            Bukkit.getScheduler().runTaskLaterAsynchronously(HyriGetDown.get(), () -> HandlerList.unregisterAll(handler), 5 * 20L);
        }

        class Handler implements Listener {

            @EventHandler(priority = EventPriority.HIGHEST)
            public void onDamage(EntityDamageEvent event) {
                if (!event.getEntity().getUniqueId().equals(player.getUniqueId())) {
                    return;
                }

                event.setDamage(event.getDamage() / 2);
            }

        }

    }, false),
    FIRE("fire", Rarity.LEGENDARY, new Consumer<Player>() {

        private static final int DURATION = 3;

        @Override
        public void accept(Player player) {
            addDurationBar(GDBonus.FIRE.getDisplayName(), player, DURATION);

            new BukkitRunnable() {

                private int seconds = DURATION;

                @Override
                public void run() {
                    final Block block = player.getLocation().getBlock();

                    if (block.getRelative(BlockFace.DOWN).getType() != Material.AIR) {
                        block.setType(Material.FIRE);
                    }

                    if (this.seconds == 0) {
                        this.cancel();
                        return;
                    }

                    this.seconds--;
                }

            }.runTaskTimer(HyriGetDown.get(), 0L, 20L);
        }
    }, true),

    ROCKET("rocket", Rarity.EPIC, player -> player.setVelocity(new Vector(0.0D, 1.5D, 0.0D)), true),
    TELEPORTATION("teleportation", Rarity.EPIC, player -> {
        final Location location = player.getLocation();
        final int x = location.getBlockX();
        final int y = location.getBlockY();
        final int z = location.getBlockZ();
        final GDJumpWorld world = (GDJumpWorld) HyriGetDown.get().getGame().getCurrentWorld();
        final Cuboid cuboid = new Cuboid(world.asBukkit(), x + 10, y + 10, z + 10, x - 10, y - 10, x - 10);

        final List<Block> blocks = cuboid.getBlocks();

        Block block = null;
        while (!world.isJumpBlock(block)) {
            block = blocks.get(ThreadLocalRandom.current().nextInt(0, blocks.size()));
        }

        if (block != null) {
            final Location blockLocation = block.getLocation();

            player.teleport(blockLocation.add(blockLocation.getBlockX() > 0 ? 0.5 : -0.5, 0.0, blockLocation.getBlockZ() > 0 ? 0.5 : -0.5));
        }
    }, true),
    INVISIBILITY("invisibility", Rarity.EPIC, player -> {
        final PotionEffect effect = new PotionEffect(PotionEffectType.INVISIBILITY, 20 * 20, 1, false, false);
        final ITabListManager tabListManager = HyriGetDown.get().getHyrame().getTabListManager();

        player.removePotionEffect(effect.getType());
        player.addPotionEffect(effect);

        tabListManager.hideNameTag(player);

        addDurationBar(HyriLanguageMessage.get("bonus.invisibility.name"), player, effect.getDuration() / 20);

        Bukkit.getScheduler().runTaskLater(HyriGetDown.get(), () -> tabListManager.showNameTag(player), effect.getDuration());
    }, false),
    BLINDNESS("blindness", Rarity.EPIC, () -> new PotionEffect(PotionEffectType.BLINDNESS, 8 * 20, 0, false, false), true),

    REGENERATION("regeneration", Rarity.RARE, () -> new PotionEffect(PotionEffectType.REGENERATION, 4 * 20, 2, false, false), false),
    JUMP("jump", Rarity.RARE, () -> new PotionEffect(PotionEffectType.JUMP, 5 * 20, 2, false, false), false),
    SLOW("slow", Rarity.RARE, () -> new PotionEffect(PotionEffectType.SLOW, 10 * 20, 1, false, false), true),

    EXTRA_HEARTS("extra-hearts", Rarity.COMMON, () -> new PotionEffect(PotionEffectType.ABSORPTION, 15 * 20, 1, false, false), false),
    RESISTANCE("resistance", Rarity.COMMON, () -> new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 10 * 20, 1, false, false), false),
    NAUSEA("nausea", Rarity.COMMON, () -> new PotionEffect(PotionEffectType.CONFUSION, 10 * 20, 0, false, false), true),

    ;

    private HyriLanguageMessage displayName;

    private final String name;
    private final Rarity rarity;
    private final Consumer<Player> handler;
    private final boolean malus;

    GDBonus(String name, Rarity rarity, Consumer<Player> handler, boolean malus) {
        this.name = name;
        this.rarity = rarity;
        this.handler = handler;
        this.malus = malus;
    }

    GDBonus(String name, Rarity rarity, Supplier<PotionEffect> effectSupplier, boolean malus) {
        this(name, rarity, player -> {
            final PotionEffect potionEffect = effectSupplier.get();

            if (potionEffect == null) {
                return;
            }

            player.removePotionEffect(potionEffect.getType());

            addDurationBar(HyriLanguageMessage.get("bonus." + name + ".name"), player, potionEffect.getDuration() / 20);

            player.addPotionEffect(potionEffect);
        }, malus);
    }

    private static void addDurationBar(HyriLanguageMessage bonusDisplay, Player player, int duration) {
        new BukkitRunnable() {

            private final GDWorld<?> initialWorld = HyriGetDown.get().getGame().getCurrentWorld();

            private int seconds = duration;

            @Override
            public void run() {
                if (this.seconds == 0 || initialWorld != HyriGetDown.get().getGame().getCurrentWorld()) {
                    this.cancel();
                    return;
                } else {
                    final String text = GDMessage.BONUS_ACTION_BAR.asString(player)
                            .replace("%bonus%", bonusDisplay.getValue(player))
                            .replace("%seconds%", String.valueOf(this.seconds));
                    final ActionBar actionBar = new ActionBar(text);

                    actionBar.send(player);
                }

                this.seconds--;
            }
        }.runTaskTimerAsynchronously(HyriGetDown.get(), 0L, 20L);
    }

    public Rarity getRarity() {
        return this.rarity;
    }

    public Consumer<Player> getHandler() {
        return this.handler;
    }

    public boolean isMalus() {
        return this.malus;
    }

    public HyriLanguageMessage getDisplayName() {
        return this.displayName == null ? this.displayName = HyriLanguageMessage.get("bonus." + this.name + ".name") : this.displayName;
    }

    public static List<GDBonus> getBonuses(Rarity rarity) {
        final List<GDBonus> bonuses = new ArrayList<>();

        for (GDBonus bonus : values()) {
            if (bonus.getRarity() == rarity) {
                bonuses.add(bonus);
            }
        }
        return bonuses;
    }

    public enum Rarity {

        MYTHIC("mythic", 2.15D),
        LEGENDARY("legendary", 6.35D),
        EPIC("epic", 10.5D),
        RARE("rare", 35.5D),
        COMMON("common", 45.0D);

        private HyriLanguageMessage displayName;

        private final String name;
        private final double percentage;

        Rarity(String name, double percentage) {
            this.name = name;
            this.percentage = percentage;
        }

        public double getPercentage() {
            return this.percentage;
        }

        public HyriLanguageMessage getDisplayName() {
            return this.displayName == null ? this.displayName = HyriLanguageMessage.get("bonus.rarity." + this.name + ".name") : this.displayName;
        }

    }

}
