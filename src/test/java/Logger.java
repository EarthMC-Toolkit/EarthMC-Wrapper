import io.github.emcw.utils.GsonUtil;

public class Logger {
    public static <T> void print(T obj) {
        print(obj, true);
    }

    public static <T> void print(T obj, Boolean serialize) {
        var out = serialize ? GsonUtil.serialize(obj) : obj;
        System.out.println(out);
    }
}
