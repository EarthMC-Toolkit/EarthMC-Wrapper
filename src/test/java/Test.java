import com.google.gson.JsonArray;
import net.emc.emcw.utils.API;

public class Test {
    static void sleep(int ms){
        try { Thread.sleep(ms); }
        catch (InterruptedException e) { e.printStackTrace(); }
    }

    public static void main(String[] args) {
        JsonArray players = API.playerData("aurora");
        sleep(1400);
        System.out.println("Output: \n" + players);
    }
}
