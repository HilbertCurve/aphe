package math.vector;

import java.nio.FloatBuffer;
import java.util.Iterator;
import java.util.Stack;

public class Matrix4f extends MatrixXf {
    public float m00, m01, m02, m03;
    public float m10, m11, m12, m13;
    public float m20, m21, m22, m23;
    public float m30, m31, m32, m33;

    public static final int length = 16;

    public Matrix4f() {
        this.m00 = 0.0f;
        this.m01 = 0.0f;
        this.m02 = 0.0f;
        this.m03 = 0.0f;
        this.m10 = 0.0f;
        this.m11 = 0.0f;
        this.m12 = 0.0f;
        this.m13 = 0.0f;
        this.m20 = 0.0f;
        this.m21 = 0.0f;
        this.m22 = 0.0f;
        this.m23 = 0.0f;
        this.m30 = 0.0f;
        this.m31 = 0.0f;
        this.m32 = 0.0f;
        this.m33 = 0.0f;
    }

    public Matrix4f(float m00, float m01, float m02, float m03,
                    float m10, float m11, float m12, float m13,
                    float m20, float m21, float m22, float m23,
                    float m30, float m31, float m32, float m33) {
        this.m00 = m00;
        this.m01 = m01;
        this.m02 = m02;
        this.m03 = m03;
        this.m10 = m10;
        this.m11 = m11;
        this.m12 = m12;
        this.m13 = m13;
        this.m20 = m20;
        this.m21 = m21;
        this.m22 = m22;
        this.m23 = m23;
        this.m30 = m30;
        this.m31 = m31;
        this.m32 = m32;
        this.m33 = m33;
    }

    public Matrix4f(FloatBuffer f) {
        if (f.array().length < 16) {
            throw new IllegalArgumentException("Buffer must have a length of 16 or more.");
        }

        this.m00 = f.get();
        this.m01 = f.get();
        this.m02 = f.get();
        this.m03 = f.get();
        this.m10 = f.get();
        this.m11 = f.get();
        this.m12 = f.get();
        this.m13 = f.get();
        this.m20 = f.get();
        this.m21 = f.get();
        this.m22 = f.get();
        this.m23 = f.get();
        this.m30 = f.get();
        this.m31 = f.get();
        this.m32 = f.get();
        this.m33 = f.get();
    }

    public float[] toArray() {
        return new float[] {
                m00, m01, m02, m03,
                m10, m11, m12, m13,
                m20, m21, m22, m23,
                m30, m31, m32, m33
        };
    }

    /**
     * Credit: Matrix4f.java, line 6698 in JOML.
     */
    public Matrix4f ortho(float left, float right, float bottom, float top, float zNear, float zFar) {
        // calculate right matrix elements
        float rm00 = 2.0f / (right - left);
        float rm11 = 2.0f / (top - bottom);
        float rm22 = 2.0f / (zNear - zFar);
        float rm30 = (left + right) / (left - right);
        float rm31 = (top + bottom) / (bottom - top);
        float rm32 = (zFar + zNear) / (zNear - zFar);

        // perform optimized multiplication
        // compute the last column first, because other columns do not depend on it
        this.m30 = (m00 * rm30 + m10 * rm31 + m20 * rm32 + m30);
        this.m31 = (m01 * rm30 + m11 * rm31 + m21 * rm32 + m31);
        this.m32 = (m02 * rm30 + m12 * rm31 + m22 * rm32 + m32);
        this.m33 = (m03 * rm30 + m13 * rm31 + m23 * rm32 + m33);
        this.m00 = (m00 * rm00);
        this.m01 = (m01 * rm00);
        this.m02 = (m02 * rm00);
        this.m03 = (m03 * rm00);
        this.m10 = (m10 * rm11);
        this.m11 = (m11 * rm11);
        this.m12 = (m12 * rm11);
        this.m13 = (m13 * rm11);
        this.m20 = (m20 * rm22);
        this.m21 = (m21 * rm22);
        this.m22 = (m22 * rm22);
        this.m23 = (m23 * rm22);
        return this;
    }

    @Override
    public FloatBuffer getAsFloatBuffer() {
        return FloatBuffer.wrap(this.toArray());
    }

    @Override
    public FloatBuffer copyInto(FloatBuffer buffer) {
        buffer.clear().put(this.getAsFloatBuffer());
        return buffer;
    }

    @Override
    public Matrix4f identity() {
        return this.set(new float[] {
                1, 0, 0, 0,
                0, 1, 0, 0,
                0, 0, 1, 0,
                0, 0, 0, 1
        });
    }

    @Override
    public Matrix4f set(float[] floats) {
        if (floats.length > length)
            throw new IllegalArgumentException("Mismatching array lengths not supported yet.");

        this.m00 = floats[0];
        this.m01 = floats[1];
        this.m02 = floats[2];
        this.m03 = floats[3];
        this.m10 = floats[4];
        this.m11 = floats[5];
        this.m12 = floats[6];
        this.m13 = floats[7];
        this.m20 = floats[8];
        this.m21 = floats[9];
        this.m22 = floats[10];
        this.m23 = floats[11];
        this.m30 = floats[12];
        this.m31 = floats[13];
        this.m32 = floats[14];
        this.m33 = floats[15];
        return this;
    }

    @Override
    public Matrix4f set(FloatBuffer buffer) {
        if (buffer.array().length < length)
            throw new IllegalArgumentException("Mismatching array lengths not supported yet.");

        this.m00 = buffer.get();
        this.m01 = buffer.get();
        this.m02 = buffer.get();
        this.m03 = buffer.get();
        this.m10 = buffer.get();
        this.m11 = buffer.get();
        this.m12 = buffer.get();
        this.m13 = buffer.get();
        this.m20 = buffer.get();
        this.m21 = buffer.get();
        this.m22 = buffer.get();
        this.m23 = buffer.get();
        this.m30 = buffer.get();
        this.m31 = buffer.get();
        this.m32 = buffer.get();
        this.m33 = buffer.get();
        return this;
    }

    @Override
    public Matrix4f set(MatrixXf m) {
        if (m.getClass() != Matrix4f.class) {
            throw new IllegalArgumentException("Matrix copying of different sizes not supported yet.");
        }

        float[] fb = m.toArray();
        return this.set(fb);
    }

    @Override
    public Iterator<Float> iterator() {
        float[] arr = this.toArray();

        return new Iterator<>() {
            final Stack<Float> s = new Stack<>();
            {
                for (float f : arr) {
                    s.push(f);
                }
            }

            @Override
            public boolean hasNext() {
                return !s.empty();
            }

            @Override
            public Float next() {
                return s.pop();
            }
        };
    }
}
