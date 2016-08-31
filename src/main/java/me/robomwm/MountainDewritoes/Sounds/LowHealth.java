package me.robomwm.MountainDewritoes.Sounds;

import me.robomwm.MountainDewritoes.MountainDewritoes;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.HashSet;

/**
 * Created by RoboMWM on 8/30/2016.
 */
public class LowHealth implements Listener
{
    HashSet<Player> alreadyLowHealth = new HashSet<>();
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR) //playing sound effect based on damage info. Not making any changes.
    void onPlayerOuchie(EntityDamageEvent event)
    {
        //Only care about players
        if (event.getEntityType() != EntityType.PLAYER)
            return;

        Player player = (Player)event.getEntity();

        //Only play the low health sound once, until the player is no longer at low health
        if (alreadyLowHealth.contains(player))
        {
            double health = player.getHealth() - event.getFinalDamage();
            if (health >= 10.0)
            {
                player.stopSound("fortress.lowhealth");
                alreadyLowHealth.remove(player);
            }
            return;
        }

        if (player.getFoodLevel() >= 20 && player.getSaturation() > 0)
            return; //ignore rapid health regeneration

        double health = player.getHealth() - event.getFinalDamage();
        if (health <= 4.0)
        {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "playsound fortress.lowhealth player " + player.getName() + " 0 0 0 3000000");
            alreadyLowHealth.add(player);
        }
    }
    @EventHandler(ignoreCancelled = true)
    void resetLowHealthIndicator(PlayerRespawnEvent event)
    {
        alreadyLowHealth.remove(event.getPlayer());
    }
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    void onRegainHealth(EntityRegainHealthEvent event)
    {
        //Only care about players
        if (event.getEntityType() != EntityType.PLAYER)
            return;

        Player player = (Player)event.getEntity();

        if (!alreadyLowHealth.contains(player))
            return;
        double health = player.getHealth() + event.getAmount();

        if (health >= 10.0)
        {
            player.stopSound("fortress.lowhealth");
            alreadyLowHealth.remove(player);
        }
    }
}