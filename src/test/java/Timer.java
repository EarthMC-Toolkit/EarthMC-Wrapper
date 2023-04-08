public class Timer {
    private static long startTime, endTime;
    static double elapsed;

    public static void start() {
        startTime = System.currentTimeMillis();
    }

    public static void stop() {
        endTime = System.currentTimeMillis();
        elapsed = (endTime - startTime);
    }

    public static void printMillis() {
        System.out.println("\nElapsed time: " + (elapsed / 1000) + "ms\n");
    }

    public static void printMicro() {
        System.out.println("\nElapsed time: " + (elapsed * 1000) + " micros\n");
    }

    public static void stopAndPrint() {
        stop();
        printMillis();
    }
}