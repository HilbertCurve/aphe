package math.vector;

import java.nio.FloatBuffer;
import java.util.Iterator;

public abstract class MatrixXf implements Iterable<Float> {
    public abstract FloatBuffer getAsFloatBuffer();
    public abstract float[] toArray();
    public abstract FloatBuffer copyInto(FloatBuffer buffer);
    public abstract MatrixXf identity();
    public abstract MatrixXf set(float[] floats);
    public abstract MatrixXf set(FloatBuffer buffer);
    public abstract MatrixXf set(MatrixXf m);
}
