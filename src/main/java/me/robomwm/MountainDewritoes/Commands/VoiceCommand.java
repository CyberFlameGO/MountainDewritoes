package me.robomwm.MountainDewritoes.Commands;

import me.robomwm.MountainDewritoes.LazyText;
import me.robomwm.MountainDewritoes.MountainDewritoes;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SoundCategory;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created on 7/26/2017.
 *
 * @author RoboMWM
 */
public class VoiceCommand implements CommandExecutor
{
    private MountainDewritoes plugin;
    private ItemStack book;

    public VoiceCommand(MountainDewritoes mountainDewritoes)
    {
        this.plugin = mountainDewritoes;
        book = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta bookMeta = (BookMeta)book.getItemMeta();

        bookMeta.spigot().addPage(LazyText.buildPage(
                LazyText.command("⬅ ","/help","Go back to /menu"),
                ChatColor.BLACK + "Voicelines:\n",
                "Greetings:\n",
                LazyText.command("Hello ", "/v hello", "say Hi"),
                LazyText.command("Thanks ", "/v thanks", "thx"),
                "\nCallouts:\n",
                LazyText.command("Over here! ", "/v overhere"),
                LazyText.command("Dis wae! ", "/v followme"),
                LazyText.command("Look at me! ", "/v lookatme"),
                LazyText.command("Look at this! ", "/v lookatthis"),

                "\nMemes+Others:\n",
                LazyText.command("#1 ", "/v wano"),
                LazyText.command("dawae ", "/v dounodawae"),
                LazyText.command("be quiet ", "/v quiet"),
                LazyText.command("no ", "/v no"),
                LazyText.command("Ok ", "/v okay")));

        book.setItemMeta(bookMeta);
    }

    private ItemStack getBook()
    {
        return book;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        if (!(sender instanceof Player))
            return false;

        Player player = (Player)sender;

        if (player.hasMetadata("DEAD"))
            return false;

        if (args.length <= 0)
        {
            plugin.openBook(player, getBook());
            return false;
        }

        float volume = 1f;
        String voiceCommand = String.join("", args).toLowerCase();
        String voiceLine = null;

        //If it's a known/common voice line, also send an actionbar message to nearby players
        switch (voiceCommand)
        {
            case "hello":
                voiceLine = "sez hello!";
                voiceCommand = getSound(voiceCommand, 7);
                volume = 2f;
                break;
            case "followme":
                voiceLine = "sez follow me!";
                voiceCommand = getSound(voiceCommand, 2);
                volume = 3f;
                flashPlayer(player);
                break;
            case "overhere":
                voiceLine = "sez over here!";
                voiceCommand = getSound(voiceCommand, 2);
                flashPlayer(player);
                volume = 3f;
                break;
            case "thx":
            case "thank":
            case "thanks":
                voiceLine = "sez thanks!";
                volume = 2f;
                voiceCommand = getSound("thanks", 1);
                break;
            case "help":
                voiceLine = "requests assistance!";
                volume = 2f;
                break;
            case "ok":
            case "okay":
            case "acknowledge":
            case "acknowledges":
            case "acknowledged":
                voiceLine = "sez okay";
                volume = 2f;
                voiceCommand = "okay";
                break;
            case "haha":
            case "ha":
            case "lol":
            case "lulz":
            case "lul":
                voiceCommand = getSound("haha", 5);
                break;
            case "wano":
            case "wearenumberone":
            case "numberone":
            case "wearenumber1":
            case "number1":
            case "#1":
            case "1":
                voiceCommand = getSound("wano", 3);
                voiceLine = "is #1";
                break;
            case "dounodawae":
            case "dawae":
                voiceCommand = getSound("dounodawae", 1);
                break;
            case "no":
                voiceCommand = getSound("no", 9);
                break;
            case "lookatme":
                voiceCommand = getSound("lookatme", 2);
                voiceLine = "wants ur attention!!11!";
                flashPlayer(player);
                break;
            case "lookatthis":
            case "see":
                voiceCommand = getSound("lookatthis", 3);
                break;
            case "bruh":
                voiceCommand = getSound("bruh", 3);
                break;
            case "quiet":
            case "shh":
                voiceCommand = getSound("quiet", 2);
                break;
        }

        if (voiceLine != null)
            broadcastMessageNearby(player, volume, voiceLine);

        String sound = "tts." + voiceCommand;

        player.getWorld().playSound(player.getLocation(), sound, SoundCategory.VOICE, volume, 1.0f);

        return true;
    }

    private String getSound(String sound, int variations)
    {
        return sound + ThreadLocalRandom.current().nextInt(1, variations + 1);
    }

    private void flashPlayer(Player player)
    {
        if (player.isGlowing())
            return;

        new BukkitRunnable()
        {
            int i = -1;
            @Override
            public void run()
            {
                player.setGlowing(++i % 2 == 0);
                if (i >= 5)
                    cancel();
            }
        }.runTaskTimer(plugin, 0L, 10L);
    }

    private void broadcastMessageNearby(Player player, float volume, String message)
    {
        volume = volume * 16f;
        volume = volume * volume;
        message = player.getDisplayName() + " " + ChatColor.AQUA + message;
        Location location = player.getLocation();
        for (Player target : player.getWorld().getPlayers())
        {
            if (location.distanceSquared(target.getLocation()) < volume)
                plugin.getTitleManager().timedActionBar(target, 3, message);
        }
    }
}
