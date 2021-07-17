package sandbox;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import aphe.primitives.Box2D;
import aphe.primitives.Circle;
import aphe.primitives.Line2D;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import static aphe.util.MyMath.rotate;

/**
 * Wrapper over all debug/physics-related draw calls.
 */
public class DebugDraw {
    static class BoxDraw {
        public static final int MAX_BOXES = 250;
        private static final int BOX_VERTEX_COUNT = 6;

        public static final List<Box2D> boxes = new ArrayList<>();
        // 6 floats per vertex, 4 vertices per box
        private static final float[] vertexArray = new float[MAX_BOXES * 6 * BOX_VERTEX_COUNT];
        private static Shader shader = AssetPool.getShader("assets/sandbox/shaders/default.glsl");

        private static int vaoID;
        private static int vboID;

        private static boolean started = false;

        public static void start() {
            // Generate the vao
            vaoID = glGenVertexArrays();
            glBindVertexArray(vaoID);

            // Create the vbo and buffer some memory
            vboID = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, vboID);
            glBufferData(GL_ARRAY_BUFFER, (long) vertexArray.length * Float.BYTES, GL_DYNAMIC_DRAW);

            // Enable the vertex array attributes
            glVertexAttribPointer(0, 2, GL_FLOAT, false, 6 * Float.BYTES, 0);
            glEnableVertexAttribArray(0);

            glVertexAttribPointer(1, 4, GL_FLOAT, false, 6 * Float.BYTES, 2 * Float.BYTES);
            glEnableVertexAttribArray(1);

            glLineWidth(2.0f);
        }

        public static void beginFrame() {
            if (!started) {
                start();
                started = true;
            }

            // Remove dead lines
            for (int i=0; i < boxes.size(); i++) {
                if (boxes.get(i).beginFrame() < 0) {
                    boxes.remove(i);
                    i--;
                }
            }
        }

        public static void draw() {
            if (boxes.size() <= 0) return;

            int index = 0;
            int[] vertexOrder = {0, 1, 3, 2, 0};
            for (Box2D box : boxes) {
                Vector2f position = new Vector2f(box.getVertices()[vertexOrder[0]]);
                Vector3f color = box.getColor();

                index = loadVertex(index, position, new Vector4f(0.0f, 0.0f, 0.0f, 0.0f));

                for (int i = 1; i < BOX_VERTEX_COUNT - 1; i++) {
                    position = new Vector2f(box.getVertices()[vertexOrder[i]]);

                    // Load position
                    index = loadVertex(index, position, new Vector4f(color, 0.4f));
                }

                index = loadVertex(index, position, new Vector4f(0.0f, 0.0f, 0.0f, 0.0f));
            }

            glBindBuffer(GL_ARRAY_BUFFER, vboID);
            glBufferSubData(GL_ARRAY_BUFFER, 0, Arrays.copyOfRange(vertexArray, 0, boxes.size() * 6 * BOX_VERTEX_COUNT));

            // Use our shader
            shader.use();
            shader.uploadMat4f("uProjection", Window.getScene().getCamera().getProjectionMatrix());
            shader.uploadMat4f("uView", Window.getScene().getCamera().getViewMatrix());

            // Bind the vao
            glBindVertexArray(vaoID);
            glEnableVertexAttribArray(0);
            glEnableVertexAttribArray(1);

            // Draw the batch
            glDrawArrays(GL_LINE_STRIP, 0, boxes.size() * 6 * BOX_VERTEX_COUNT);

            // Disable Location
            glDisableVertexAttribArray(0);
            glDisableVertexAttribArray(1);
            glBindVertexArray(0);

            // Unbind shader
            shader.detach();
        }

        private static int loadVertex(int index, Vector2f vec, Vector4f color) {
            // Load position
            vertexArray[index] = vec.x;
            vertexArray[index + 1] = vec.y;

            // Load color
            vertexArray[index + 2] = color.x;
            vertexArray[index + 3] = color.y;
            vertexArray[index + 4] = color.z;
            vertexArray[index + 5] = color.w;

            index += 6;

            return index;
        }
    }

    static class CircleDraw {
        public static final int MAX_CIRCLES = 100;
        private static final int CIRCLE_VERTEX_COUNT = 102;

        public static final List<Circle> circles = new ArrayList<>();
        // 6 floats per vertex, 101 vertices per circle
        private static final float[] vertexArray = new float[MAX_CIRCLES * 6 * CIRCLE_VERTEX_COUNT];
        private static final Shader shader = AssetPool.getShader("assets/sandbox/shaders/default.glsl");

        private static int vaoID;
        private static int vboID;

        private static boolean started = false;

        public static void start() {
            // Generate the vao
            vaoID = glGenVertexArrays();
            glBindVertexArray(vaoID);

            // Create the vbo and buffer some memory
            vboID = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, vboID);
            glBufferData(GL_ARRAY_BUFFER, (long) vertexArray.length * Float.BYTES, GL_DYNAMIC_DRAW);

            // Enable the vertex array attributes
            glVertexAttribPointer(0, 2, GL_FLOAT, false, 6 * Float.BYTES, 0);
            glEnableVertexAttribArray(0);

            glVertexAttribPointer(1, 4, GL_FLOAT, false, 6 * Float.BYTES, 2 * Float.BYTES);
            glEnableVertexAttribArray(1);
        }

        public static void beginFrame() {
            if (!started) {
                start();
                started = true;
            }

            // Remove dead circles
            for (int i=0; i < circles.size(); i++) {
                if (circles.get(i).beginFrame() < 0) {
                    circles.remove(i);
                    i--;
                }
            }
        }

        public static void draw() {
            if (circles.size() <= 0) return;

            int index = 0;
            for (Circle circle : circles) {
                Vector2f position = new Vector2f(circle.getCenter()).add(circle.getRadius(), 0);
                Vector2f center = circle.getCenter();
                Vector3f color = circle.getColor();

                rotate(center, position, circle.getRotation());

                // Load position
                index = loadVertex(index, position, new Vector4f(0.0f, 0.0f, 0.0f, 0.0f));

                for (int i = 0; i < CIRCLE_VERTEX_COUNT-2; i++) {
                    Vector2f v = new Vector2f(position);

                    rotate(center, v, 360f * (i)/(CIRCLE_VERTEX_COUNT-3));

                    index = loadVertex(index, v, new Vector4f(color, 0.4f));
                }

                index = loadVertex(index, center, new Vector4f(0.0f, 0.0f, 0.0f, 0.4f));
            }

            glBindBuffer(GL_ARRAY_BUFFER, vboID);
            glBufferSubData(GL_ARRAY_BUFFER, 0, Arrays.copyOfRange(vertexArray, 0, circles.size() * 6 * CIRCLE_VERTEX_COUNT));

            // Use our shader
            shader.use();
            shader.uploadMat4f("uProjection", Window.getScene().getCamera().getProjectionMatrix());
            shader.uploadMat4f("uView", Window.getScene().getCamera().getViewMatrix());

            // Bind the vao
            glBindVertexArray(vaoID);
            glEnableVertexAttribArray(0);
            glEnableVertexAttribArray(1);

            // Draw the batch
            glDrawArrays(GL_LINE_STRIP, 0, circles.size() * 6 * CIRCLE_VERTEX_COUNT);

            // Disable Location
            glDisableVertexAttribArray(0);
            glDisableVertexAttribArray(1);
            glBindVertexArray(0);

            // Unbind shader
            shader.detach();
        }

        private static int loadVertex(int index, Vector2f vec, Vector4f color) {
            // Load position
            vertexArray[index] = vec.x;
            vertexArray[index + 1] = vec.y;

            // Load color
            vertexArray[index + 2] = color.x;
            vertexArray[index + 3] = color.y;
            vertexArray[index + 4] = color.z;
            vertexArray[index + 5] = color.w;

            index += 6;

            return index;
        }
    }

    static class LineDraw {
        public static final int MAX_LINES = 500;

        public static final List<Line2D> lines = new ArrayList<>();
        // 6 floats per vertex, 2 vertices per line
        private static final float[] vertexArray = new float[MAX_LINES * 6 * 2];
        private static Shader shader = AssetPool.getShader("assets/sandbox/shaders/default.glsl");

        private static int vaoID;
        private static int vboID;

        private static boolean started = false;

        public static void start() {
            // Generate the vao
            vaoID = glGenVertexArrays();
            glBindVertexArray(vaoID);

            // Create the vbo and buffer some memory
            vboID = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, vboID);
            glBufferData(GL_ARRAY_BUFFER, (long) vertexArray.length * Float.BYTES, GL_DYNAMIC_DRAW);

            // Enable the vertex array attributes
            glVertexAttribPointer(0, 3, GL_FLOAT, false, 6 * Float.BYTES, 0);
            glEnableVertexAttribArray(0);

            glVertexAttribPointer(1, 3, GL_FLOAT, false, 6 * Float.BYTES, 3 * Float.BYTES);
            glEnableVertexAttribArray(1);

            glLineWidth(2.0f);
        }

        public static void beginFrame() {
            if (!started) {
                start();
                started = true;
            }

            // Remove dead lines
            for (int i=0; i < lines.size(); i++) {
                if (lines.get(i).beginFrame() < 0) {
                    lines.remove(i);
                    i--;
                }
            }
        }


        public static void draw() {
            if (lines.size() <= 0) return;

            int index = 0;
            for (Line2D line : lines) {
                for (int i=0; i < 2; i++) {
                    Vector2f position = i == 0 ? line.getFrom() : line.getTo();
                    Vector3f color = line.getColor();

                    // Load position
                    vertexArray[index] = position.x;
                    vertexArray[index + 1] = position.y;
                    vertexArray[index + 2] = -10.0f;

                    // Load the color
                    vertexArray[index + 3] = color.x;
                    vertexArray[index + 4] = color.y;
                    vertexArray[index + 5] = color.z;
                    index += 6;
                }
            }

            glBindBuffer(GL_ARRAY_BUFFER, vboID);
            glBufferSubData(GL_ARRAY_BUFFER, 0, Arrays.copyOfRange(vertexArray, 0, lines.size() * 6 * 2));

            // Use our shader
            shader.use();
            shader.uploadMat4f("uProjection", Window.getScene().getCamera().getProjectionMatrix());
            shader.uploadMat4f("uView", Window.getScene().getCamera().getViewMatrix());

            // Bind the vao
            glBindVertexArray(vaoID);
            glEnableVertexAttribArray(0);
            glEnableVertexAttribArray(1);

            // Draw the batch
            glDrawArrays(GL_LINES, 0, lines.size() * 6 * 2);

            // Disable Location
            glDisableVertexAttribArray(0);
            glDisableVertexAttribArray(1);
            glBindVertexArray(0);

            // Unbind shader
            shader.detach();
        }
    }

    public static void start() {
        BoxDraw.start();
        CircleDraw.start();
        LineDraw.start();
    }

    public static void beginFrame() {
        BoxDraw.beginFrame();
        CircleDraw.beginFrame();
        LineDraw.beginFrame();
    }

    public static void draw() {
        BoxDraw.draw();
        CircleDraw.draw();
        LineDraw.draw();
    }

    ///////////////////
    /* BOX2D-METHODS */
    ///////////////////
    public static void addBox2D(Box2D box2D) {
        if (BoxDraw.boxes.size() + 1 <= BoxDraw.MAX_BOXES)
            BoxDraw.boxes.add(box2D);
    }

    public static void addBox2D(Vector2f center, Vector2f dimensions, float rotation) {
        addBox2D(new Box2D(
                new Vector2f(center).sub(new Vector2f(dimensions).div(2)),
                new Vector2f(center).add(new Vector2f(dimensions).div(2)),
                rotation,
                new Vector3f(0.0f, 0.0f, 0.0f),
                1
        ));
    }

    public static void addBox2D(Vector2f min, Vector2f max, Vector3f color, int lifetime) {
        addBox2D(new Box2D(min, max, color, lifetime));
    }

    public static void addBox2D(Vector2f min, Vector2f max, float rotation, Vector3f color, int lifetime) {
        addBox2D(new Box2D(min, max, rotation, color, lifetime));
    }

    public static Box2D getBox2D(int index) {
        return BoxDraw.boxes.get(index);
    }

    ////////////////////
    /* CIRCLE-METHODS */
    ////////////////////
    public static void addCircle(Vector2f center, float radius, float rotation, Vector3f color, int lifetime) {
        if (CircleDraw.circles.size() >= CircleDraw.MAX_CIRCLES) return;
        CircleDraw.circles.add(new Circle(center, radius, rotation, color, lifetime));
    }

    public static void addCircle(Circle circle) {
        if (CircleDraw.circles.size() >= CircleDraw.MAX_CIRCLES) return;
        CircleDraw.circles.add(circle);
    }

    public static void addCircle(Vector2f center, float radius, Vector3f color) {
        addCircle(center, radius, 0, color, 1);
    }

    public static Circle getCircle(int index) {
        return CircleDraw.circles.get(index);
    }

    ////////////////////
    /* LINE2D-METHODS */
    ////////////////////
    public static void addLine2D(Vector2f from, Vector2f to, Vector3f color) {
        addLine2D(from, to, color, 1);
    }

    public static void addLine2D(Vector2f from, Vector2f to) {
        // TODO: ADD CONSTANTS FOR COMMON COLORS
        addLine2D(from, to, new Vector3f(0, 1, 0), 1);
    }

    public static void addLine2D(Vector2f from, Vector2f to, Vector3f color, int lifetime) {
        if (LineDraw.lines.size() >= LineDraw.MAX_LINES) return;
        LineDraw.lines.add(new Line2D(from, to, color, lifetime));
    }

    public static Line2D getLine2D(int index) {
        return LineDraw.lines.get(index);
    }
}
