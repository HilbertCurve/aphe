package math.vector;

public class Vector4f extends VectorXf {
    public float x, y, z, w;
    public static final int length = 4;

    public Vector4f() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
        this.w = 0;
    }

    public Vector4f(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    @Override
    public Vector4f add(float f) {
        this.x += f;
        this.y += f;
        this.z += f;
        this.w += f;
        return this;
    }

    @Override
    public Vector4f sub(float f) {
        this.x -= f;
        this.y -= f;
        this.z -= f;
        this.w -= f;
        return this;
    }

    @Override
    public Vector4f mul(float f) {
        this.x *= f;
        this.y *= f;
        this.z *= f;
        this.w *= f;
        return this;
    }

    @Override
    public Vector4f div(float f) {
        if (f == 0) {
            throw new IllegalArgumentException("Division by 0 not supported yet.");
        }

        this.x /= f;
        this.y /= f;
        this.z /= f;
        this.w /= f;
        return this;
    }
}
