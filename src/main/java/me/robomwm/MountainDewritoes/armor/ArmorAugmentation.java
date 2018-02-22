package me.robomwm.MountainDewritoes.armor;

import me.robomwm.MountainDewritoes.MountainDewritoes;
import me.robomwm.MountainDewritoes.NSA;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created on 1/3/2018.
 *
 * Handles the armor abilities, controls energy bar
 *
 * @author RoboMWM
 */
public class ArmorAugmentation implements Listener
{
    private MountainDewritoes instance;

    public ArmorAugmentation(MountainDewritoes plugin)
    {
        instance = plugin;
        new GoldArmor(instance, this);
        new IronArmor(instance, this);
        new DiamondArmor(instance, this);
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        plugin.getServer().getPluginManager().registerEvents(new OldFood(instance), plugin);
        ATPgeneration();
    }

    public boolean isEquipped(Player player, Material armorToMatch)
    {
        ItemStack equippedArmor = null;
        switch (armorToMatch)
        {
            case GOLD_LEGGINGS:
            case IRON_LEGGINGS:
            case DIAMOND_LEGGINGS:
            case CHAINMAIL_LEGGINGS:
                equippedArmor = player.getInventory().getLeggings();
                break;
            case GOLD_BOOTS:
            case IRON_BOOTS:
            case DIAMOND_BOOTS:
            case CHAINMAIL_BOOTS:
                equippedArmor = player.getInventory().getBoots();
                break;
        }
        return equippedArmor != null && equippedArmor.getType() == armorToMatch;
    }

    //Used by sprint ability usually
    public boolean isFullPower(PlayerToggleSprintEvent event, Material leggings)
    {
        Player player = event.getPlayer();
        if (!event.isSprinting() || player.getFoodLevel() < 20)
            return false;
        if (!this.isEquipped(player, leggings))
            return false;
        return true;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    private void onPlayerFallDamageWearingLongFallBoots(EntityDamageEvent event)
    {
        if (event.getCause() != EntityDamageEvent.DamageCause.FALL)
            return;
        if (!(event.getEntity() instanceof LivingEntity))
            return;

        LivingEntity entity = (LivingEntity)event.getEntity();

        if (entity.getEquipment() == null || entity.getEquipment().getBoots() == null)
            return;

        switch(entity.getEquipment().getBoots().getType())
        {
            case GOLD_BOOTS:
            case IRON_BOOTS:
            case DIAMOND_BOOTS:
                entity.getWorld().playSound(entity.getLocation(), "fortress.longfallboots", 1.0f, 1.0f);
                event.setCancelled(true);
        }
    }

    //Misc. gameplay changes to accomodate

    //There's no such thing as starving
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    private void onHungerDamage(EntityDamageEvent event)
    {
        if (event.getCause() == EntityDamageEvent.DamageCause.STARVATION)
            event.setCancelled(true);
    }

    //Since when does having a full stomach make your wounds heal faster?
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    private void onStomachHeal(EntityRegainHealthEvent event)
    {
        if (event.getEntityType() != EntityType.PLAYER)
            return;
        if (event.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED)
            event.setCancelled(true);
    }

    //That's ~~an energy~~ power bar, not a hunger bar.
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    private void onGettingHungry(FoodLevelChangeEvent event)
    {
        event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    private void onSprint(PlayerToggleSprintEvent event)
    {
        Player player = event.getPlayer();
        if (instance.isNoModifyWorld(player.getWorld()))
            return;
        if (event.isSprinting() && player.getFoodLevel() > 1)
            player.setFoodLevel(player.getFoodLevel() - 1);
    }

    //Refill energy bar gradually, unless sprinting
    private void ATPgeneration()
    {
        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                for (Player player : instance.getServer().getOnlinePlayers())
                {
                    if (player.getFoodLevel() >= 20 || instance.isNoModifyWorld(player.getWorld()))
                        continue;
                    if (player.isSprinting())
                        player.setFoodLevel(player.getFoodLevel() - 1);
                    else
                        player.setFoodLevel(player.getFoodLevel() + 1);
                }
            }
        }.runTaskTimer(instance, 20L, 20L);
    }

    //Cancel minute falling damage, do goomba stomp
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    private void onHardlyAnyFalling(EntityDamageEvent event)
    {
        if (event.getCause() != EntityDamageEvent.DamageCause.FALL || event.getEntityType() != EntityType.PLAYER)
            return;
        if (event.getDamage() < 5.0)
            event.setCancelled(true);
        //TODO: goomba stomp
    }

}

//trash
//Sprinting takes energy
//    private Map<Player, Long> sprinters = new HashMap<>();
//    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
//    private void onSprint(PlayerToggleSprintEvent event)
//    {
//        Player player = event.getPlayer();
//
//        if (!event.isSprinting())
//        {
//            sprinters.remove(player);
//            player.setSaturation(20f);
//            return;
//        }
//        if (player.getFoodLevel() < 1 || instance.isNoModifyWorld(player.getWorld()))
//            return;
//
//        final long time = System.currentTimeMillis();
//        sprinters.put(player, time);
//
//        player.setFoodLevel(player.getFoodLevel() - 1);
//        player.setSaturation(0f);
//
//        new BukkitRunnable()
//        {
//
//            @Override
//            public void run()
//            {
//                if (sprinters.containsKey(player) && sprinters.get(player) == time)
//                    player.setFoodLevel(player.getFoodLevel() - 2);
//                else
//                    cancel();
//            }
//        }.runTaskTimer(instance, 10L, 10L);
//    }
