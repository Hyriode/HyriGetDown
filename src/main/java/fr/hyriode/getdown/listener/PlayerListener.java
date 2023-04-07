package fr.hyriode.getdown.listener;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.event.HyriEventHandler;
import fr.hyriode.api.event.HyriEventPriority;
import fr.hyriode.getdown.HyriGetDown;
import fr.hyriode.getdown.game.achievement.GDAchievement;
import fr.hyriode.getdown.game.GDGame;
import fr.hyriode.getdown.game.GDGamePlayer;
import fr.hyriode.getdown.game.GDPhase;
import fr.hyriode.getdown.game.scoreboard.SpectatorScoreboard;
import fr.hyriode.getdown.language.GDMessage;
import fr.hyriode.getdown.shop.item.ShopAccessorItem;
import fr.hyriode.getdown.world.jump.GDJumpWorld;
import fr.hyriode.hyrame.game.HyriGameSpectator;
import fr.hyriode.hyrame.game.HyriGameState;
import fr.hyriode.hyrame.game.event.player.HyriGameDeathEvent;
import fr.hyriode.hyrame.game.event.player.HyriGameReconnectEvent;
import fr.hyriode.hyrame.game.event.player.HyriGameReconnectedEvent;
import fr.hyriode.hyrame.game.event.player.HyriGameSpectatorEvent;
import fr.hyriode.hyrame.game.waitingroom.HyriWaitingRoom;
import fr.hyriode.hyrame.item.ItemNBT;
import fr.hyriode.hyrame.listener.HyriListener;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Created by AstFaster
 * on 23/07/2022 at 14:22
 */
public class PlayerListener extends HyriListener<HyriGetDown> {

    private final List<UUID> hitCooldowns = new ArrayList<>();

    public PlayerListener(HyriGetDown plugin) {
        super(plugin);

        HyriAPI.get().getEventBus().register(this);
    }

    @HyriEventHandler
    public void onReconnect(HyriGameReconnectEvent event) {
        final GDGame game = HyriGetDown.get().getGame();

        if (game.getState() != HyriGameState.PLAYING || game.getCurrentPhase() != GDPhase.JUMP) {
            event.disallow();
        }
    }

    @HyriEventHandler
    public void onReconnected(HyriGameReconnectedEvent event) {
        final GDGamePlayer gamePlayer = (GDGamePlayer) event.getGamePlayer();

        gamePlayer.onReconnect();
    }

    @HyriEventHandler(priority = HyriEventPriority.LOWEST)
    public void onSpectator(HyriGameSpectatorEvent event) {
        final GDGame game = HyriGetDown.get().getGame();
        final HyriGameSpectator spectator = event.getSpectator();

        if (spectator instanceof GDGamePlayer && game.getState() != HyriGameState.PLAYING) {
            game.win(game.getWinner());
        } else {
            spectator.getPlayer().teleport(game.getWaitingRoom().getConfig().getSpawn().asBukkit());

            if (!(spectator instanceof GDGamePlayer)) {
                new SpectatorScoreboard(spectator.getPlayer()).show();
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        final GDGame game = HyriGetDown.get().getGame();
        final GDGamePlayer gamePlayer = game.getPlayer((Player) event.getEntity());

        if (gamePlayer == null || game.getCurrentPhase() != GDPhase.DEATHMATCH) {
            return;
        }

        event.setCancelled(false);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        final List<ItemStack> items = Arrays.asList(event.getCurrentItem(), event.getCursor());

        for (ItemStack item : items) {
            if (item == null) {
                continue;
            }

            if (new ItemNBT(item).hasTag(ShopAccessorItem.NBT_TAG)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        final GDGame game = HyriGetDown.get().getGame();

        if (game.getCurrentPhase() != GDPhase.DEATHMATCH) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        final Entity entity = event.getEntity();
        final GDGame game = HyriGetDown.get().getGame();
        final GDPhase phase = game.getCurrentPhase();

        if (phase == null) {
            return;
        }

        if (phase == GDPhase.BUY) {
            event.setCancelled(true);
            return;
        }

        if (!(entity instanceof Player) || phase == GDPhase.DEATHMATCH) {
            return;
        }

        final Player player = (Player) entity;
        final GDGamePlayer gamePlayer = game.getPlayer(player);

        if (gamePlayer == null) {
            return;
        }

        final GDJumpWorld world = (GDJumpWorld) game.getCurrentWorld();

        if (world.isSwitchingMap()) {
            event.setCancelled(true);
            return;
        }

        event.setDamage(event.getDamage() * 1.15);

        final double damage = event.getFinalDamage();

        gamePlayer.getAchievements().remove((Integer) GDAchievement.NO_DAMAGES.getId());

        if (player.getHealth() - damage <= 0.0D) {
            if (!event.isCancelled()) {
                event.setDamage(0.0D);
                event.setCancelled(true);

                gamePlayer.getAchievements().remove((Integer) GDAchievement.NO_DEATHS.getId());
                gamePlayer.onJumpDeath();
            }
        }
    }

    @EventHandler
    public void onDamageByEntity(EntityDamageByEntityEvent event) {
        final Entity entity = event.getEntity();
        final Entity dealerEntity = event.getDamager();

        if (!(entity instanceof Player) || !(dealerEntity instanceof Player)) {
            return;
        }

        final Player player = (Player) entity;
        final Player dealer = (Player) dealerEntity;
        final GDGame game = HyriGetDown.get().getGame();
        final GDPhase phase = game.getCurrentPhase();

        if (phase == GDPhase.JUMP && game.getPlayer(player) != null && game.getPlayer(dealer.getPlayer()) != null) {
            final int maxHeight = ((GDJumpWorld) game.getCurrentWorld()).getConfig().getMaximumAttackHeight();

            if (player.getLocation().getY() >= maxHeight || dealer.getLocation().getY() >= maxHeight || this.hitCooldowns.contains(dealer.getUniqueId())) {
                event.setCancelled(true);
            } else {
                final UUID dealerId = dealer.getUniqueId();

                this.hitCooldowns.add(dealerId);

                Bukkit.getScheduler().runTaskLaterAsynchronously(this.plugin, () -> this.hitCooldowns.remove(dealerId), 5 * 20L);
            }
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        final GDGame game = HyriGetDown.get().getGame();
        final GDPhase phase = game.getCurrentPhase();

        if (phase == GDPhase.BUY) {
            final HyriWaitingRoom.Config config = game.getWaitingRoom().getConfig();
            final double firstY = config.getFirstPos().getY();
            final double secondY = config.getSecondPos().getY();

            if (event.getTo().getY() < Math.min(firstY, secondY)) {
                event.getPlayer().teleport(config.getSpawn().asBukkit());
            }
        }

        if (game.getState() != HyriGameState.PLAYING || phase != GDPhase.JUMP) {
            return;
        }

        final Player player = event.getPlayer();
        final GDGamePlayer gamePlayer = game.getPlayer(player);

        if (gamePlayer == null || gamePlayer.isSpectator()) {
            return;
        }

        final GDJumpWorld world = (GDJumpWorld) game.getCurrentWorld();

        if (world == null) {
            return;
        }

        Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
            final Player newPlayer = Bukkit.getPlayer(player.getUniqueId());

            if (newPlayer == null) {
                return;
            }

            if (newPlayer.getLocation().getBlockY() <= world.getConfig().getEndHeight() && !world.isEnded() && newPlayer.getFallDistance() <= 3.0F) {
                world.onEndReached(gamePlayer);
            }
        }, 5L);

        if (world.isSwitchingMap()) {
            return;
        }

        Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
            final Location location = player.getLocation();
            final Block block = location.getBlock().getRelative(BlockFace.DOWN);

            world.checkBlock(block, player);
        }, 10L);
    }

}
