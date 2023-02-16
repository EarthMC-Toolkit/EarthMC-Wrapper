import com.google.gson.JsonObject;
import net.emc.emcw.core.EMCMap;
import net.emc.emcw.core.EMCWrapper;
import net.emc.emcw.objects.Player;
import net.emc.emcw.utils.GsonUtil;

public class Test {
    static void sleep(int ms){
        try { Thread.sleep(ms); }
        catch (InterruptedException e) { e.printStackTrace(); }
    }

    public static void main(String[] args) {
        EMCMap aurora = new EMCWrapper().Aurora;
        JsonObject firstOp = (JsonObject) aurora.onlinePlayers().asList().get(1);

        sleep(1400);

        Player p = aurora.getOnlinePlayer(firstOp);
        System.out.println("Output: \n" + GsonUtil.stringify(p));
    }
}
