package sandbox.window;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import static sandbox.window.KeyListener.isKeyPressed;
import static sandbox.window.MouseListener.getScrollY;

public class Camera extends GameObject {
    private final Matrix4f projectionMatrix, viewMatrix;
    private static final float TILE_SIZE = 32.0f;

    /**
     * Current zoom (in tiles; a value of 1.0 corresponds with a 40x21 32px-tile view).
     */
    private float zoom = 1.0f;

    public Camera(Vector2f transform) {
        super("Camera", new Transform(transform), Integer.MAX_VALUE);
        this.projectionMatrix = new Matrix4f();
        this.viewMatrix = new Matrix4f();
        setZoom(1.0f);
        adjustProjection();
    }

    public void adjustProjection() {
        projectionMatrix.identity();
        projectionMatrix.ortho(
                // I use Math.pow here to make the zoom more natural
                (TILE_SIZE * -20.0f) * (float) Math.pow(2, getZoom()),
                (TILE_SIZE * 20.0f) * (float) Math.pow(2, getZoom()),
                (TILE_SIZE * -10.5f) * (float) Math.pow(2, getZoom()),
                (TILE_SIZE * 10.5f) * (float) Math.pow(2, getZoom()),
                0.0f, 100.0f
        );
    }

    public float getZoom() {
        return zoom;
    }

    public void setZoom(float z) {
        zoom = z;
    }

    public void zoomIn(float z) {
        zoom += z;
    }

    public Matrix4f getViewMatrix() {
        Vector3f cameraFront = new Vector3f(0.0f, 0.0f, -1.0f);
        Vector3f cameraUp = new Vector3f(0.0f, 1.0f, 0.0f);
        this.viewMatrix.identity();
        viewMatrix.lookAt(
                new Vector3f(transform.position.x, transform.position.y, 1.0f),
                cameraFront.add(transform.position.x, transform.position.y, 0.0f),
                cameraUp
        );
        return this.viewMatrix;
    }

    public Matrix4f getProjectionMatrix() {
        return this.projectionMatrix;
    }

    @Override
    public int zIndex() {
        return Integer.MAX_VALUE; // it should be on top of everything.
    }
}
