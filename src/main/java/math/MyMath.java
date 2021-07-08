package math;

import org.joml.Vector2f;
import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * Worthless class optimizing math operations based on both C/C++ implementations found online
 * and algorithms derived by myself.
 */
public final class MyMath {
    public enum AngleMode {
        RADIANS,
        DEGREES
    }

    public static AngleMode mode = AngleMode.DEGREES;

    /**
     * Used for better optimization.
     */
    private static Unsafe u;
    private static Field f;

    public static final float PI = 3.14159265359f;

    public static final float DEGREES_TO_RADIANS = PI / 180;
    public static final float RADIANS_TO_DEGREES = 180 / PI;

    static {
        try {
            f = Unsafe.class.getDeclaredField("theUnsafe");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        try {
            f.trySetAccessible();
            u = (Unsafe) f.get(f);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Just don't.
     */
    private MyMath() { }

    /**
     * Credit: <a href=https://www.youtube.com/watch?v=p8u_k2LIZyo>Fast Inverse Square Root — A Quake III Algorithm</a>
     */
    public static strictfp float invSqrt(float num) {
        if (num < 0) {
            throw new IllegalArgumentException("invSqrt(" + num + "): complex operations not supported yet.");
        }

        long iAddress = u.allocateMemory(Long.BYTES);
        long i;
        float x2, y;
        x2 = num * 0.5F;
        u.putFloat(iAddress, num);
        i = u.getLong(iAddress);
        i = 0x5f3759df - (i >> 1);
        u.putLong(iAddress, i);
        y = u.getFloat(iAddress);
        y = y * (1.5F - (x2 * y * y));
        y = y * (1.5F - (x2 * y * y));
        y = y * (1.5F - (x2 * y * y));

        u.freeMemory(iAddress);

        return y < 0 ? -y : y;
    }

    public static strictfp float sqrt(float num) {
        return 1 / invSqrt(num);
    }

    @SuppressWarnings("ManualMinMaxCalculation") // I want to expose how it works
    public static float min(float x, float y) {
        return x > y ? y : x;
    }

    @SuppressWarnings("ManualMinMaxCalculation")
    public static float max(float x, float y) {
        return x > y ? x : y;
    }

    /**
     * Calculates {@code sin(x)} to a minimum accuracy of 5 digits.
     * @param x Any value in degrees.
     * @return {@code sin(x)}
     */
    public static float sin(float x) {
        // modulate x (to keep return value within maximum accuracy)
        if (x < -180) {
            x %= 180;
            x = 180 + x;
        } else if (x > 180) {
            x %= 180;
            x = -180 + x;
        }

        if (mode == AngleMode.DEGREES)
            x *= DEGREES_TO_RADIANS;

        float a = x*x*x/6;
        float b = a*x*x/20;
        float c = b*x*x/42;
        float d = c*x*x/72;
        float e = d*x*x/110;
        float f = e*x*x/154;
        float g = f*x*x/210;
        float h = g*x*x/272;
        return x - a + b - c + d - e + f - g + h;
    }

    public static float cos(float x) {
        // modulate x (to keep return value within maximum accuracy)
        if (x < -180) {
            x %= 180;
            x = 180 + x;
        } else if (x > 180) {
            x %= 180;
            x = -180 + x;
        }

        if (mode == AngleMode.DEGREES)
            x *= DEGREES_TO_RADIANS;

        float b = x*x/2;
        float c = b*x*x/12;
        float d = c*x*x/30;
        float e = d*x*x/56;
        float f = e*x*x/90;
        float g = f*x*x/132;
        float h = g*x*x/182;
        return 1 - b + c - d + e - f + g - h;
    }

    public static float tan(float x) {
        // TODO: implement faster tan(float x)
        return sin(x) / cos(x);
    }

    /**
     * Rotates a point (Vector2f) {@code point} around another point {@code pivot} by {@code theta} degrees.<br>
     * This is done in a 3-part process:
     * <ul>
     *     <li>Push translating {@code pivot} to (0, 0), with {@code point} being applied the same translation.</li>
     *     <li>Calculate the new location of {@code point} using trig (see below for more info).</li>
     *     <li>Pop the aforementioned translation.</li>
     * </ul>
     * The trig is derived by converting the rect coords to polar coords using arctan (ew, how expensive). Then, it
     * adds {@code theta} to the polar coords' angle. When we convert these coords into rect coords, the arctan cancels
     * out with the normal tan! This leaves us with the two values {@code a} and {@code b}, which we use to calculate
     * the new coords of the rotated point (using our fancy {@code invSqrt()} method).
     *
     * @param pivot central point to be rotated around.
     * @param point point that is rotated.
     * @param theta float in degrees that {@code point} will rotate around {@code pivot}.
     *
     * @see MyMath#invSqrt(float x)
     * @see MyMath#sin(float x)
     * @see MyMath#cos(float x)
     */
    public static void rotate(Vector2f pivot, Vector2f point, float theta) {
        /* PUSH TRANSLATION */
        Vector2f tPoint = new Vector2f(point.sub(pivot));
        // Vector2f popT = new Vector2f(pivot);

        /* ROTATE */
        Vector2f rPoint = new Vector2f();
        theta %= 360;

        int slopeSign = sign(tPoint.x * tPoint.y);
        float scale = sqrt(tPoint.y * tPoint.y + tPoint.x * tPoint.x);

        if (tPoint.x == 0) {
            if (tPoint.y > 0) {
                rPoint.x = cos(90 + theta) * scale * slopeSign;
                rPoint.y = sin(90 + theta) * scale * slopeSign;
            } else if (tPoint.y < 0) {
                rPoint.x = cos(-90 + theta) * scale * slopeSign;
                rPoint.y = sin(-90 + theta) * scale * slopeSign;
            }
        } else {
            int currentQuadrant = slopeSign == 1 ? (sign(tPoint.x) == 1 ? 0 : 2) : (sign(tPoint.x) == 1 ? 3 : 1);

            /*
            * Quadrants:
            *           |
            *     1     |    0
            *           |
            * ----------|----------
            *           |
            *     2     |    3
            *           |
            */

            float slope = (tPoint.y / tPoint.x);
            float tanTheta = tan(theta);
            float a = slope + tanTheta;
            float b = 1 - slope * tanTheta;
            int newQuadrant;
            int signX;
            int signY;

            if (sign(a * b) == slopeSign) {
                if (theta > -90 && theta < 90) {
                    newQuadrant = currentQuadrant;
                } else {
                    newQuadrant = (currentQuadrant + 2) % 4;
                }
            } else {
                if (theta < -90 || theta > 270) {
                    newQuadrant = (currentQuadrant - 1) % 4;
                } else {
                    newQuadrant = (currentQuadrant + 1) % 4;
                }
            }

            switch (newQuadrant) {
                case 1:
                    signX = -1;
                    signY = 1;
                    break;
                case 2:
                    signX = -1;
                    signY = -1;
                    break;
                case 3:
                    signX = 1;
                    signY = -1;
                    break;
                default:
                    signX = 1;
                    signY = 1;
                    break;
            }

            // minor optimization
            float a2 = a * a;
            float b2 = b * b;

            rPoint.x = invSqrt(a2 / b2 + 1) * scale * signX;
            rPoint.y = invSqrt(b2 / a2 + 1) * scale * signY;
        }

        /* POP TRANSLATION */
        point.set(rPoint.add(pivot));
    }

    /**
     * Checks the sign of a number.
     * @param x A signed number.
     * @return -1 if {@code x} is negative; +1 otherwise.
     */
    public static int sign(double x) {
        return x < 0 ? -1 : 1;
    }
}
