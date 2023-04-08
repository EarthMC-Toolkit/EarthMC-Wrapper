import io.github.emcw.core.EMCMap;
import io.github.emcw.core.EMCWrapper;
import io.github.emcw.entities.Nation;
import io.github.emcw.entities.Player;
import io.github.emcw.entities.Resident;
import io.github.emcw.entities.Town;

import java.util.Map;

public class Test {
    static EMCMap Aurora = new EMCWrapper(true, false).getAurora();

    public static void main(String[] args) {
        testTowns();
        testNations();
        //testPlayers();
        //testResidents();
    }

    static void testTowns() {
        Timer.start();
        Map<String, Town> multi = Aurora.Towns.all();
        Timer.stop();

        Logger.print(multi.size());
        Timer.printMillis();
    }

    static void testNations() {
        Timer.start();
        Map<String, Nation> all = Aurora.Nations.all();
        Timer.stop();

        Logger.print(all.size());
        Timer.printMillis();
    }

    static void testPlayers() {
        Timer.start();
        Map<String, Player> all = Aurora.Players.all();
        //Logger.print(p);
        Timer.stop();

        Logger.print(all.size());
        Timer.printMillis();
    }

    static void testResidents() {
        Timer.start();
        Map<String, Resident> all = Aurora.Residents.all();
        Timer.stop();

        Logger.print(all.size());
        Timer.printMillis();
    }
}