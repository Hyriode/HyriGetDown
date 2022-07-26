package fr.hyriode.getdown.world.jump.block.coins;

import fr.hyriode.getdown.HyriGetDown;
import fr.hyriode.getdown.game.GDGamePlayer;
import fr.hyriode.getdown.util.BlockTexture;
import fr.hyriode.getdown.world.jump.GDJumpConfig;
import fr.hyriode.getdown.world.jump.GDJumpWorld;
import fr.hyriode.getdown.world.jump.block.GDJumpBlock;
import fr.hyriode.hyrame.packet.PacketUtil;
import fr.hyriode.hyrame.title.Title;
import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

/**
 * Created by AstFaster
 * on 24/07/2022 at 17:05
 */
public abstract class GDCoinsBlock extends GDJumpBlock {

    private final int minCoins;

    public GDCoinsBlock(BlockTexture texture, double percentage, int minCoins) {
        super(texture, percentage);
        this.minCoins = minCoins;
    }

    @Override
    public void trigger(Block block, Player player) {
        final GDJumpConfig config = ((GDJumpWorld) HyriGetDown.get().getGame().getCurrentWorld()).getConfig();
        final GDGamePlayer gamePlayer = HyriGetDown.get().getGame().getPlayer(player);
        final int maxY = config.getSpawn().asBukkit().getBlockY();
        final int minY = config.getEndHeight();
        final int playerY = player.getLocation().getBlockY();
        final double progress = (double) (maxY - minY) / playerY;
        final int bonus = (int) (10 * progress);
        final int coins = this.minCoins + bonus;

        gamePlayer.addCoins(coins);

        this.setAsUsed(block);

        final Location blockLocation = block.getLocation().clone().add(0.0D, 1.0D, 0.0D);

        Title.sendTitle(player, ChatColor.GREEN + "+ " + coins + " Coins", "", 5, 40, 5);
        PacketUtil.sendPacket(player, new PacketPlayOutWorldParticles(EnumParticle.SPELL, true, blockLocation.getBlockX(), blockLocation.getBlockY(), blockLocation.getBlockZ(), 0.05F, 0.1F, 0.05F, 1.0F, 150));
    }

}
