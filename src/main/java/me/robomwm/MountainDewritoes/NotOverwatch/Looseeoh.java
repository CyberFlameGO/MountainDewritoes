package me.robomwm.MountainDewritoes.NotOverwatch;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

/**
 * Created on 6/20/2017.
 *
 * @author RoboMWM
 */
public class Looseeoh implements Listener
{
    Ogrewatch ogrewatch;
    JavaPlugin instance;

    Looseeoh(JavaPlugin plugin, Ogrewatch wat)
    {
        this.ogrewatch = wat;
        this.instance = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    private void onFailedWallRideAttempt(PlayerMoveEvent event)
    {
        if (!event.getPlayer().hasMetadata("MD_WALLRIDE_LASTLOCATION"))
            return;
        Player player = event.getPlayer();
        Location location = (Location)player.getMetadata("MD_WALLRIDE_LASTLOCATION").get(0).value();
        if (location.distanceSquared(player.getLocation()) > 1)
        {
            player.teleport(location);
            player.setVelocity((Vector)player.getMetadata("MD_WALLRIDE_LASTVELOCITY").get(0).value());
        }
        player.removeMetadata("MD_WALLRIDE_LASTLOCATION", instance);
        player.removeMetadata("MD_WALLRIDE_LASTVELOCITY", instance);
    }

    @EventHandler(ignoreCancelled = true)
    void wallRide(PlayerToggleFlightEvent event)
    {
        Player player = event.getPlayer();
        if (!ogrewatch.isLucio(player))
            return;
        if (!startWallRiding(player))
        {
            player.setMetadata("MD_WALLRIDE_LASTLOCATION", new FixedMetadataValue(instance, player.getLocation()));
            player.setMetadata("MD_WALLRIDE_LASTVELOCITY", new FixedMetadataValue(instance, player.getVelocity()));
            player.setVelocity(player.getVelocity());
        }
        event.setCancelled(true);
    }

    private boolean startWallRiding(Player player)
    {
        if (player.hasMetadata("MD_WALLRIDING"))
            return false;
        if (player.isSneaking())
            return false;
        if (player.isOnGround())
            return false;

        //Block block = velocity.add(velocity).toLocation(player.getWorld()).getBlock();
        Block block = player.getLocation().getBlock();

        if (block.isLiquid())
            return false;

        //Near an adjacent, solid block? //TODO: we have to fix this. This is stupid.
        if (!block.getRelative(BlockFace.NORTH).getType().isSolid()
                && !block.getRelative(BlockFace.SOUTH).getType().isSolid()
                && !block.getRelative(BlockFace.EAST).getType().isSolid()
                && !block.getRelative(BlockFace.WEST).getType().isSolid())
            return false;

        player.sendMessage("wallriding");

        Vector ridingVector = player.getVelocity();
        //If player is not sprinting, they won't have any velocity in x or z direction
        //In this case, we'll just use the direction vector
        if (ridingVector.getX() == ridingVector.getZ())
            ridingVector = player.getLocation().getDirection().multiply(0.1D);

        if (Math.abs(ridingVector.getX()) > Math.abs(ridingVector.getZ()))
        {
            ridingVector.setZ(0);
        }
        else
        {
            ridingVector.setX(0);
        }

        ridingVector.setY(0.04D); //0.02 works for ideal conditions (no lag at all). Might try to "dynamically set" based on ping value.

        final Vector finalVector = ridingVector;
        player.sendMessage(ridingVector.toString());

        player.setMetadata("MD_WALLRIDING", new FixedMetadataValue(instance, true));
        player.setAllowFlight(false);


        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                //Increase absolute value of x or z component slowly towards 1...
                if (finalVector.getX() > 0 && finalVector.getX() < 1)
                    finalVector.setX(finalVector.getX() + 0.05);
                else if (finalVector.getX() < 0 && finalVector.getX() > -1)
                    finalVector.setX(finalVector.getX() - 0.05);
                else if (finalVector.getZ() > 0 && finalVector.getZ() < 1)
                    finalVector.setZ(finalVector.getZ() + 0.05);
                else if (finalVector.getZ() < 0 && finalVector.getZ() > -1)
                    finalVector.setZ(finalVector.getZ() - 0.05);

                if (!player.isOnline() || !ogrewatch.isLucio(player) || player.isOnGround() || player.isSneaking())
                {
                    cancelTask();
                    player.removeMetadata("MD_WALLRIDING", instance);
                    return;
                }

                Block block1 = player.getLocation().getBlock();
                Vector copy = new Vector();
                copy.add(finalVector).normalize();
                Block nextBlock = player.getLocation().add(copy).getBlock();

                if (block1.isLiquid() || nextBlock.getType().isSolid())
                {
                    cancelTask();
                    player.removeMetadata("MD_WALLRIDING", instance);
                    return;
                }

                //Near an adjacent, solid block?
                if (!block1.getRelative(BlockFace.NORTH).getType().isSolid()
                        && !block1.getRelative(BlockFace.SOUTH).getType().isSolid()
                        && !block1.getRelative(BlockFace.EAST).getType().isSolid()
                        && !block1.getRelative(BlockFace.WEST).getType().isSolid())
                {
                    cancelTask();
                    return;
                }

                player.setVelocity(finalVector);
                player.sendActionBar(finalVector.toString());
            }

            private void cancelTask()
            {
                cancel();
                player.removeMetadata("MD_WALLRIDING", instance);
                if (ogrewatch.isLucio(player))
                    player.setAllowFlight(true);
            }
        }.runTaskTimer(instance, 0L, 1L);
        return true;
    }
}
