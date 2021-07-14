package aphe.util;

import org.joml.Matrix2f;
import org.joml.Vector2f;
import aphe.primitives.Ray2D;
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

    public static AngleMode mode = AngleMode.RADIANS;

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
    @Deprecated(since = "i found out it's slow as hell")
    public static strictfp float invSqrt0(float num) {
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

    public static float invSqrt(double num) {
        return (float) (1.0f / Math.sqrt(num));
    }

    public static strictfp float sqrt(float num) {
        return (float) Math.sqrt(num);
    }

    @SuppressWarnings("ManualMinMaxCalculation") // I want to expose how it works
    public static float min(float a, float b) {
        return a > b ? b : a;
    }

    @SuppressWarnings("ManualMinMaxCalculation")
    public static float max(float a, float b) {
        return a > b ? a : b;
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

    public static float cos(Vector2f vec1, Vector2f vec2) {
        Vector2f v1 = new Vector2f(vec1).normalize();
        Vector2f v2 = new Vector2f(vec2).normalize();

        return v1.dot(v2);
    }

    public static float tan(float x) {
        // TODO: implement faster tan(float x)
        return sin(x) / cos(x);
    }

    /**
     * Rotates a point (Vector2f) {@code point} around another point {@code pivot} by {@code thetaDeg} degrees.<br>
     * This is done in a 3-part process:
     * <ul>
     *     <li>Push translating {@code pivot} to (0, 0), with {@code point} being applied the same translation.</li>
     *     <li>Calculate the new location of {@code point} using trig (see below for more info).</li>
     *     <li>Pop the aforementioned translation.</li>
     * </ul>
     * The trig is derived by converting the rect coords to polar coords using arctan (ew, how expensive). Then, it
     * adds {@code thetaDeg} to the polar coords' angle. When we convert these coords into rect coords, the arctan cancels
     * out with the normal tan! This leaves us with the two values {@code a} and {@code b}, which we use to calculate
     * the new coords of the rotated point (using our fancy {@code invSqrt()} method).
     *
     * @param pivot central point to be rotated around.
     * @param point point that is rotated.
     * @param thetaDeg float in degrees that {@code point} will rotate around {@code pivot}.
     *
     * @see MyMath#sin(float x)
     * @see MyMath#cos(float x)
     */
    @Deprecated(since = "affine transform")
    public static void rotate0(Vector2f pivot, Vector2f point, float thetaDeg) {
        /* PUSH TRANSLATION */
        Vector2f tPoint = new Vector2f(point.sub(pivot));
        // Vector2f popT = new Vector2f(pivot);

        /* ROTATE */
        Vector2f rPoint = new Vector2f();
        thetaDeg %= 360;

        int slopeSign = sign(tPoint.x * tPoint.y);
        float scale = sqrt(tPoint.y * tPoint.y + tPoint.x * tPoint.x);

        if (tPoint.x == 0) {
            if (tPoint.y > 0) {
                rPoint.x = cos(90 + thetaDeg) * scale * slopeSign;
                rPoint.y = sin(90 + thetaDeg) * scale * slopeSign;
            } else if (tPoint.y < 0) {
                rPoint.x = cos(-90 + thetaDeg) * scale * slopeSign;
                rPoint.y = sin(-90 + thetaDeg) * scale * slopeSign;
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
            float tanTheta = tan(thetaDeg);
            float a = slope + tanTheta;
            float b = 1 - slope * tanTheta;
            int newQuadrant;
            int signX;
            int signY;

            if (sign(a * b) == slopeSign) {
                if (thetaDeg > -90 && thetaDeg < 90) {
                    newQuadrant = currentQuadrant;
                } else {
                    newQuadrant = (currentQuadrant + 2) % 4;
                }
            } else {
                if (thetaDeg < -90 || thetaDeg > 270) {
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

            float a2 = a * a;
            float b2 = b * b;

            rPoint.x = invSqrt(a2 / b2 + 1) * scale * signX;
            rPoint.y = invSqrt(b2 / a2 + 1) * scale * signY;
        }

        /* POP TRANSLATION */
        point.set(rPoint.add(pivot));
    }

    public static void rotate(Vector2f pivot, Vector2f point, float thetaDeg) {
        float a = (float) Math.cos(thetaDeg * DEGREES_TO_RADIANS);
        float b = (float) Math.sin(thetaDeg * DEGREES_TO_RADIANS);

        Matrix2f m = new Matrix2f(
                a, -b,
                b, a
        );

        /* PUSH TRANSLATION */
        point.sub(pivot);
        // Vector2f popT = new Vector2f(pivot);

        /* ROTATE */
        point.mul(m);

        /* POP TRANSLATION */
        point.add(pivot);
    }

    /**
     * Finds the point of intersection between two infinitely long lines.
     */
    public static Vector2f intersectLines(Ray2D l1, Ray2D l2) {
        /* CALCULATE LINE EQUATIONS */
        Vector2f l1d = l1.getDirection();
        Vector2f l1o = l1.getOrigin();
        Vector2f l2d = l2.getDirection();
        Vector2f l2o = l2.getOrigin();

        float m1 = l1d.y / l1d.x;
        float m2 = l2d.y / l2d.x;

        float b1 = l1o.y - m1 * l1o.x;
        float b2 = l2o.y - m2 * l2o.x;

        /* CONSTRUCT CRAMER'S RULE MATRICES */
        float d1 = new Matrix2f(
                b1, 1,
                b2, 1
        ).determinant();
        float d2 = new Matrix2f(
                -m1, 1,
                -m2, 1
        ).determinant();
        float d3 = new Matrix2f(
                -m1, b1,
                -m2, b2
        ).determinant();

        return new Vector2f(d1/d2, d3/d2);
    }

    public static boolean compare(float a, float b, float epsilon) {
        return abs(a - b) <= epsilon * max(1.0f, max(abs(a), abs(b)));
    }

    public static boolean compare(Vector2f vec1, Vector2f vec2, float epsilon) {
        return compare(vec1.x, vec2.x, epsilon) && compare(vec1.y, vec2.y, epsilon);
    }

    public static boolean compare(float a, float b) {
        return abs(a - b) <= Float.MIN_VALUE * max(1.0f, max(abs(a), abs(b)));
    }

    public static boolean compare(Vector2f vec1, Vector2f vec2) {
        return compare(vec1.x, vec2.x) && compare(vec1.y, vec2.y);
    }

    public static float abs(float a) {
        return a * sign(a);
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
