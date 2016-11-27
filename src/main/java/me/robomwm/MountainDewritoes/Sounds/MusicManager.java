package me.robomwm.MountainDewritoes.Sounds;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by RoboMWM on 11/26/2016.
 */
class MusicManager
{
    private Map<String, MusicThing> demSongz = new HashMap<>();
    private List<MusicThing> battle = new ArrayList<>();
    private List<MusicThing> boss = new ArrayList<>();
    private List<MusicThing> chat = new ArrayList<>();
    private List<MusicThing> Christmas = new ArrayList<>();
    private List<MusicThing> mall = new ArrayList<>();
    private List<MusicThing> morning = new ArrayList<>();
    private List<MusicThing> night = new ArrayList<>();
    private List<MusicThing> records = new ArrayList<>();
    private List<MusicThing> spawn = new ArrayList<>();
    private List<MusicThing> pokemon = new ArrayList<>();
    private List<MusicThing> weather = new ArrayList<>();

    public MusicManager()
    {
        //TODO: create MusicThings and store them in demSongz
        //TODO: then obviously make copies for mall and whatnot so we can get a random song for a specific category
        battle.add(put("andyougotmesayin", 35));
        battle.add(put("SteelDeDeDrum", 60+36));
        battle.add(put("thegrandshow", 60+18));
        boss.add(put("bulletformymeme", 119));
        boss.add(put("freshsqueezedmemes100followerspecial", 60+18));
        mall.add(put("alphysshopchannel", 110));
        mall.add(put("animallobby", 46));
        mall.add(put("buysomething", 31));
        mall.add(put("fleamarket", 127));
        mall.add(put("fleamarket2", 132));
        mall.add(put("granddadinanelevator", 16));
        mall.add(put("letsshop", 58));
        mall.add(put("nintendogsong", 70));
        mall.add(put("splatoonbooyahbaseshopping", 90));
        mall.add(put("torielnospeakamericano", 120+33));
        mall.add(put("wiishopchannel", 90));
        mall.add(put("wiishoppinginsideahouse", 59));
        morning.add(put("badblood", 51));
        morning.add(put("crazyfrog", 43));
        morning.add(put("justdoit1", 128));
        morning.add(put("meow-crorow", 94));
        morning.add(put("rickroll", 40));
        morning.add(put("soldierboy", 29));
        morning.add(put("strawberryshortcake", 35));
        morning.add(put("subway", 26));
        morning.add(put("sunrise", 45));
        morning.add(put("sunrise2", 67));
        morning.add(put("spawn2", 32));
        night.add(put("minecrafttrapremix", 120+34));
        night.add(put("sonsfavorite", 42));
        night.add(put("whymca", 57));
        spawn.add(put("hotel303", 42));
        spawn.add(put("hotelbarkley", 50));
        spawn.add(put("spawn", 49)); //needs credit
        spawn.add(put("skrillexreplaceschip", 60));
        pokemon.add(put("hiddenmishaswamp", 131));
        weather.add(put("somenewfleaswallowrap", 120+17));
    }

    private MusicThing put(String name, int length)
    {
        MusicThing ok = new MusicThing(name, length);
        demSongz.put(name, ok);
        return ok;
    }

    private MusicThing randomizer(List<MusicThing> hello)
    {
        return hello.get(ThreadLocalRandom.current().nextInt(hello.size()));
    }

    /**
     * Get a specific song
     * @param song
     * @return null if song doesn't exist
     */
    public MusicThing getSong(String song)
    {
        return demSongz.get(song);
    }

    public MusicThing getMallSong()
    {
        return randomizer(mall);
    }

    public MusicThing getSpawnSong()
    {
        return randomizer(spawn);
    }

    public MusicThing getMorningSong()
    {
        return randomizer(morning);
    }

    public MusicThing getNightSong()
    {
        return randomizer(night);
    }
}
