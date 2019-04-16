package me.robomwm.MountainDewritoes.spaceship;

import me.robomwm.MountainDewritoes.Events.Key;
import me.robomwm.MountainDewritoes.Events.PlayerSteerVehicleEvent;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

/**
 * Created on 9/21/2018.
 *
 * @author RoboMWM
 */
public class Spaceship implements Listener
{
    private final Vehicle vehicle;
    private Vector thrust = new Vector();
    private Vector direction = new Vector(.3, 0, 0);
    private double pitch = 0;
    private double yaw = 0;
    private BukkitTask engine;
    private double acceleration = 1.01;
    private double maxSpeedSquared = 0.25;

    public Spaceship(Plugin plugin, Vehicle vehicle)
    {
        this.vehicle = vehicle;
        vehicle.setGravity(false);

        if (vehicle instanceof Minecart)
        {
            Minecart cart = (Minecart)vehicle;
            cart.setMaxSpeed(999);
        }

        plugin.getServer().getPluginManager().registerEvents(this, plugin);

        engine = new BukkitRunnable()
        {
            @Override
            public void run()
            {
                Vector twoD = direction.clone();
                twoD.setY(0);
                float angleToFlat = twoD.angle(direction);

                Vector rotated2D = twoD.rotateAroundY(Math.PI / 2);
                rotated2D.normalize();
                direction.rotateAroundNonUnitAxis(rotated2D, angleToFlat); //resets to 0 pitch
                direction.rotateAroundNonUnitAxis(rotated2D, pitch);
                if (yaw != 0)
                    direction.rotateAroundY(yaw);
                if (Double.isInfinite(direction.getX()))
                    direction.setX(0);
                if (Double.isInfinite(direction.getY()))
                    direction.setY(0);
                if (Double.isInfinite(direction.getZ()))
                    direction.setZ(0);
                vehicle.setVelocity(direction);
            }
        }.runTaskTimer(plugin, 1L, 1L);
    }

//    public void move(Vector direction)
//    {
//        thrust.add(direction);
//    }

    public void steer(PlayerSteerVehicleEvent event)
    {
        pitch = 0;
        yaw = 0;

        StringBuilder keys = new StringBuilder();

        for (Key key : event.getKeysPressed())
        {
            switch (key)
            {
                case LEFT:
                    yaw = Math.PI / 130;
                    break;
                case RIGHT:
                    yaw = Math.PI / -130;
                    break;
                case FORWARD:
                    pitch = Math.PI / 4;
                    break;
                case BACK:
                    pitch = Math.PI / -4;
                    break;
                case JUMP:
                    //vector.zero();
                    break;
            }
            keys.append(key);
        }

        event.getPlayer().sendActionBar(keys.toString());
    }
}

