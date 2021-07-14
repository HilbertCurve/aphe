package aphe.util;

public class FunctionWrapper {
    public static double functionTime(Runnable r) {
        double d = System.nanoTime();

        r.run();

        return System.nanoTime() - d;
    }
}
