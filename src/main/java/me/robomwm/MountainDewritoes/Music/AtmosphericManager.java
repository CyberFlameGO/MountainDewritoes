package me.robomwm.MountainDewritoes.Music;

import me.robomwm.MountainDewritoes.Events.JukeboxInteractEvent;
import me.robomwm.MountainDewritoes.Events.MonsterTargetPlayerEvent;
import me.robomwm.MountainDewritoes.MountainDewritoes;
import me.robomwm.MountainDewritoes.NSA;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.block.Jukebox;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by RoboMWM on 10/8/2016.
 * It's not just a manager, it contains it all!
 * No way am I gonna split this into classes (at least not now)
 */
public class AtmosphericManager implements Listener
{
    MountainDewritoes instance;
    MemeBox memeBox;

    public AtmosphericManager(MountainDewritoes mountainDewritoes)
    {
        instance = mountainDewritoes;
        memeBox = new MemeBox(mountainDewritoes);
        new AtmosphericMusic(mountainDewritoes, this);
        instance.registerListener(this);
    }

    public void stopMusic(Player player)
    {
        player.removeMetadata("MD_LISTENING", instance);
        memeBox.stopSound(player);
    }

    public void stopMusic(Player player, double radius)
    {
        for (Entity entity : player.getNearbyEntities(radius,radius,radius))
        {
            if (entity.getType() == EntityType.PLAYER)
                stopMusic(player);
        }
    }

    /**
     * Plays sound to players, unless they're already listening to something else
     * "Thread-safe"
     * @param song Sound to play
     * @param delay How long to wait in seconds before playing the sound
     * @param players
     */
    public void playSound(MusicThing song, int delay, Collection<? extends Player> players)
    {
        new BukkitRunnable()
        {
            public void run()
            {
                for (Player player : players)
                {
                    //Skip player if they're dead
                    if (player.hasMetadata("DEAD") || player.isDead())
                        continue;
                    //Skip player if already listening to music
                    if (player.hasMetadata("MD_LISTENING"))
                    {
                        //except if priority is higher.
                        if (!song.hasHigherPriority((MusicThing)player.getMetadata("MD_LISTENING").get(0).value()))
                            continue;
                    }

                    player.setMetadata("MD_LISTENING", new FixedMetadataValue(instance, song));
                    memeBox.playSound(player, song);

                    //Schedule removal of metadata
                    new BukkitRunnable()
                    {
                        public void run()
                        {
                            if (!player.hasMetadata("MD_LISTENING"))
                                return;

                            if (song.equals((MusicThing)player.getMetadata("MD_LISTENING").get(0).value()))
                                player.removeMetadata("MD_LISTENING", instance);
                        }
                    }.runTaskLater(instance, song.getLength());
                    //player.playSound(player.getLocation(), song.getSoundName(), SoundCategory.AMBIENT, 3000000f, 1.0f);
                }
            }
        }.runTaskLater(instance, delay * 20L);
    }

    /* Helper methods */

    public void playSoundNearPlayer(MusicThing song, Player player, double radius)
    {
        Set<Player> players = new HashSet<>();
        for (Entity entity : player.getNearbyEntities(radius,radius,radius))
        {
            if (entity.getType() == EntityType.PLAYER)
                players.add((Player)entity);
        }
        players.add(player); //it seems Entity#getNearbyEntities does not include the entity in question. Haven't specifically tested though.
        playSound(song, 0, players);
        for (Player player1 : players) //eeeeh
        {
            memeBox.tellPlayerToOpenMemeBox(player1, true);
        }
    }
    public void playSound(MusicThing song, @Nullable World world)
    {
        if (world == null)
            playSound(song, 0, instance.getServer().getOnlinePlayers());
        else
            playSound(song, 0, world.getPlayers());
    }
    public void playSound(MusicThing song, int delay, Player player)
    {
        playSound(song, delay, Collections.singletonList(player));
    }

    /*Music always stops when player changes worlds*/
    @EventHandler
    private void changeWorldResetMetadata(PlayerChangedWorldEvent event)
    {
        stopMusic(event.getPlayer());
    }

    /*Music always stops when player dies*/
    @EventHandler
    private void onPlayerDeath(PlayerDeathEvent event)
    {
        stopMusic(event.getEntity());
    }

    /*In case metadata doesn't get removed for w/e reason*/
    @EventHandler
    private void onQuitResetMetadataAndStopMusic(PlayerQuitEvent event)
    {
        stopMusic(event.getPlayer());
    }
}

