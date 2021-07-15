package aphe.util;

import jdk.jfr.Description;
import jdk.jfr.StackTrace;

import java.lang.reflect.Method;
import java.util.Arrays;

public class FunctionWrapper {
    /**
     * Returns the time it takes to complete a function (passed in as a
     * no-args lambda expression). Used primarily to compare the speeds
     * of different implementations of a function.<br><br>
     *
     * The first few times this is run in a test return inaccurate results,
     * likely due to class instantiation or something like that.
     */
    public static double functionTime(Runnable r) {
        double d = System.nanoTime();

        r.run();

        return System.nanoTime() - d;
    }

    @Description("unfinished")
    public static Method getInvoker(Runnable r) throws ClassNotFoundException, NoSuchMethodException {
        Runnable finalR = r;
        final String[] classInvoker = {""};
        final String[] methodInvoker = {""};
        r = () -> {
            finalR.run();
            try {
                throw new Exception();
            } catch (Exception e) {
                StackTraceElement[] ste = e.getStackTrace();
                classInvoker[0] = ste[0].getClassName();
                methodInvoker[0] = ste[0].getMethodName();
            }
        };
        Class<?> clazz = Class.forName(classInvoker[0]);

        return clazz.getDeclaredMethod(methodInvoker[0]);
    }
}
