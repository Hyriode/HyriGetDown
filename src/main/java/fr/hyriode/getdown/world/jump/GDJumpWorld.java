package fr.hyriode.getdown.world.jump;

import com.mongodb.lang.Nullable;
import fr.hyriode.getdown.HyriGetDown;
import fr.hyriode.getdown.game.GDGame;
import fr.hyriode.getdown.game.GDGamePlayer;
import fr.hyriode.getdown.language.GDMessage;
import fr.hyriode.getdown.util.BlockTexture;
import fr.hyriode.getdown.world.GDWorld;
import fr.hyriode.getdown.world.jump.block.GDJumpBlock;
import fr.hyriode.getdown.world.jump.block.GDVoidBlock;
import fr.hyriode.getdown.world.jump.block.bonus.GDBonusBlock;
import fr.hyriode.getdown.world.jump.block.coins.GDMegaCoinsBlock;
import fr.hyriode.getdown.world.jump.block.coins.GDNormalCoinsBlock;
import fr.hyriode.hyrame.game.HyriGamePlayer;
import fr.hyriode.hyrame.title.Title;
import fr.hyriode.hyrame.utils.LocationWrapper;
import fr.hyriode.hyrame.utils.PlayerUtil;
import fr.hyriode.hyrame.utils.WorldUtil;
import fr.hyriode.hyrame.utils.block.Cuboid;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

/**
 * Created by AstFaster
 * on 23/07/2022 at 13:36
 */
public class GDJumpWorld extends GDWorld<GDJumpConfig> {

    private final GDJumpDifficulty difficulty;

    private final List<GDJumpBlock> blocks;

    private final BlockFace[] validBlockFaces = {    BlockFace.NORTH,
            BlockFace.EAST,
            BlockFace.SOUTH,
            BlockFace.WEST,
            BlockFace.NORTH_EAST,
            BlockFace.NORTH_WEST,
            BlockFace.SOUTH_EAST,
            BlockFace.SOUTH_WEST};

    private boolean ended;
    private boolean switchingMap;

    public GDJumpWorld(String name) {
        super(Type.JUMP, name, GDJumpConfig.class);
        this.difficulty = GDJumpDifficulty.values()[ThreadLocalRandom.current().nextInt(GDJumpDifficulty.values().length)];
        this.blocks = new ArrayList<>();

        this.registerBlock(new GDBonusBlock());
        this.registerBlock(new GDMegaCoinsBlock());
        this.registerBlock(new GDNormalCoinsBlock());

        // Void block
        this.registerBlock(new GDVoidBlock(100.0D - this.blocks.stream().mapToDouble(GDJumpBlock::getPercentage).sum()));
    }

    @SuppressWarnings("deprecation")
    @Override
    public void load() {
        super.load();

        // Load chunks
        for (Chunk chunk : WorldUtil.getChunksAround(this.config.getSpawn().asBukkit(this.asBukkit()).getChunk(), Bukkit.getViewDistance())) {
            if (!chunk.isLoaded()) {
                chunk.load(false);
            }
        }

        // Generate blocks
        final List<BlockTexture> blocks = this.config.getBlocks();
        final ThreadLocalRandom random = ThreadLocalRandom.current();
        final Cuboid cuboid = this.config.getArea().asArea(this.asBukkit()).toCuboid();

        for (Block block : cuboid.getBlocks()) {
            if (!this.checkAroundBlock(block) || block.getType() != Material.AIR || random.nextDouble() > this.difficulty.getBlocksPercentage()) {
                continue;
            }

            final GDJumpBlock jumpBlock = this.getRandomBlock();

            if (jumpBlock == null) {
                continue;
            }

            BlockTexture texture = jumpBlock.getTexture();
            if (texture == null) {
                texture = blocks.get(random.nextInt(blocks.size()));
            }

            block.setMetadata(GDJumpBlock.METADATA, new FixedMetadataValue(HyriGetDown.get(), jumpBlock));

            block.setType(texture.getMaterial());
            block.setData(texture.getData());
        }
    }

    private boolean checkAroundBlock(Block block) {
        Block upBlock = block;
        Block downBlock = block;
        Block leftBlock = block;
        Block rightBlock = block;
        for (int i = 0; i < 2; i++) {
            upBlock = upBlock.getRelative(BlockFace.UP);
            downBlock = downBlock.getRelative(BlockFace.DOWN);
            leftBlock = leftBlock.getRelative(BlockFace.WEST);
            rightBlock = rightBlock.getRelative(BlockFace.EAST);

            if (upBlock.getType() != Material.AIR) {
                return false;
            }

            if (downBlock.getType() != Material.AIR) {
                return false;
            }

            if (leftBlock.getType() != Material.AIR) {
                return false;
            }

            if (rightBlock.getType() != Material.AIR) {
                return false;
            }
        }
        return true;
    }

    private GDJumpBlock getRandomBlock() {
        final double chance = ThreadLocalRandom.current().nextDouble() * 100.0D;
        double cumulative = 0.0D;

        for (GDJumpBlock block : this.blocks) {
            cumulative += block.getPercentage();

            if (chance < cumulative) {
                return block;
            }
        }
        return null;
    }

    @Override
    public void teleportPlayers() {
        final World world = this.asBukkit();
        final Location spawn = this.config.getSpawn().asBukkit(world);

        for (HyriGamePlayer gamePlayer : HyriGetDown.get().getGame().getPlayers()) {
            if (!gamePlayer.isOnline()) {
                continue;
            }

            final Player player = gamePlayer.getPlayer();

            PlayerUtil.resetPlayer(player);

            player.setFallDistance(0.0F);
            player.teleport(spawn);
        }
    }

    public void onEndReached(@Nullable GDGamePlayer gamePlayer) {
        this.ended = true;
        if(gamePlayer != null) {
            gamePlayer.addSuccessfulJump();
            gamePlayer.addCoins(this.difficulty.getCoinsReward());
        }
        final Consumer<Consumer<Player>> players = consumer -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                consumer.accept(player);
            }
        };
        final GDGame game = HyriGetDown.get().getGame();
        final Runnable switchMapTimer = () -> new BukkitRunnable() {

            private int index = 10;

            @Override
            public void run() {
                players.accept(target -> target.setLevel(this.index));

                if (this.index == 0) {
                    game.switchToNextJumpWorld();
                    this.cancel();
                    return;
                } else if (this.index == 5) {
                    players.accept(target -> Title.sendTitle(target, GDMessage.TITLE_JUMP_NEXT_MAP.asString(target), game.getNextWorld().getName(), 15, 60, 15));
                }

                this.index--;
            }
        }.runTaskTimer(HyriGetDown.get(), 0L, 20L);

        players.accept(target -> {
            final Location location = target.getLocation();

            target.playSound(location, Sound.FIREWORK_TWINKLE, 1.0F, 1.3F);
            target.playSound(location, Sound.LEVEL_UP, 1.0F, 1.0F);
            if(gamePlayer != null) {
                target.sendMessage(GDMessage.MESSAGE_JUMP_END.asString(target).replace("%player%", gamePlayer.formatNameWithTeam()));
            } else {
                target.sendMessage(GDMessage.MESSAGE_JUMP_TIMEOUT.asString(target));
            }

        });

        new BukkitRunnable() {

            private int index = 5;

            @Override
            public void run() {
                if (this.index == 0) {
                    this.cancel();

                    switchingMap = true;

                    players.accept(target -> Title.sendTitle(target, GDMessage.TITLE_JUMP_END.asString(target), gamePlayer.formatNameWithTeam(), 15, 60, 15));

                    if (game.getNextWorld().getType() == Type.DEATH_MATCH) {
                        game.switchToBuyPhase();
                    } else {
                        switchMapTimer.run();
                    }
                    return;
                } else {
                    players.accept(target -> {
                        if (target.getUniqueId().equals(gamePlayer.getUniqueId())) {
                            return;
                        }

                        Title.sendTitle(target, ChatColor.RED + String.valueOf(this.index), "", 15, 60, 15);
                    });
                }

                this.index--;
            }
        }.runTaskTimer(HyriGetDown.get(), 0, 20L);
    }

    public void checkBlock(Block block, Player player) {
        final List<MetadataValue> values = block.getMetadata(GDJumpBlock.METADATA);

        if (values == null || values.size() == 0) {
            return;
        }

        final GDJumpBlock jumpBlock = (GDJumpBlock) values.get(0).value();

        jumpBlock.trigger(block, player);
    }

    public boolean isJumpBlock(Block block) {
        if (block == null) {
            return false;
        }
        final List<MetadataValue> values = block.getMetadata(GDJumpBlock.METADATA);

        return values != null && values.size() > 0;
    }


    public void registerBlock(GDJumpBlock block) {
        this.blocks.add(block);
    }

    public boolean isEnded() {
        return this.ended;
    }

    public boolean isSwitchingMap() {
        return this.switchingMap;
    }

    public GDJumpDifficulty getDifficulty() {
        return this.difficulty;
    }

}
