package math.vector;

public class Vector2f extends VectorXf {
    public float x, y;
    public static final int length = 2;

    public Vector2f() {
        this.x = 0;
        this.y = 0;
    }

    public Vector2f(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Vector2f(Vector2f v) {
        this.x = v.x;
        this.y = v.y;
    }

    /* STATIC METHODS */
    public static float determinant(Vector2f v1, Vector2f v2) {
        return v1.x * v2.y - v2.x * v1.y;
    }

    /* NON-STATIC METHODS */
    @Override
    public Vector2f add(float f) {
        this.x += f;
        this.y += f;
        return this;
    }

    public Vector2f add(Vector2f v) {
        this.x += v.x;
        this.y += v.y;
        return this;
    }

    @Override
    public Vector2f sub(float f) {
        this.x -= f;
        this.y -= f;
        return this;
    }

    public Vector2f sub(Vector2f v) {
        this.x -= v.x;
        this.y -= v.y;
        return this;
    }

    @Override
    public Vector2f mul(float f) {
        this.x *= f;
        this.y *= f;
        return this;
    }

    @Override
    public Vector2f div(float f) {
        if (f == 0) throw new IllegalArgumentException("Division by 0 isn't implemented yet.");
        this.x /= f;
        this.y /= f;
        return this;
    }

    public void set(Vector2f v) {
        this.x = v.x;
        this.y = v.y;
    }

    public Vector2f copy() {
        return new Vector2f(this.x, this.y);
    }

    @Override
    public String toString() {
        return "Vector2f(" + this.x + ", " + this.y + ")";
    }
}
