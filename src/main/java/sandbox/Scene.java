package sandbox;

import aphe.PhysicsSystem2D;
import aphe.forces.Transform;
import aphe.rigidbody.Rigidbody2D;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import static sandbox.KeyListener.isKeyPressed;
import static sandbox.MouseListener.getScrollY;

public class Scene {
    private Camera camera = new Camera(new Vector2f());

    PhysicsSystem2D physics = new PhysicsSystem2D(1.0f / 60.0f, new Vector2f(0f, -9.8f));
    Transform obj1, obj2;
    Rigidbody2D rb1, rb2;

    public void start() {
        loadResources();

        obj1 = new Transform(new Vector2f(100, 200));
        obj2 = new Transform(new Vector2f(200, 300));
        rb1 = new Rigidbody2D();
        rb2 = new Rigidbody2D();
        rb1.setRawTransform(obj1);
        rb2.setRawTransform(obj2);
        rb1.setMass(100);
        rb2.setMass(200);

        physics.addRigidbody(rb1);
        physics.addRigidbody(rb2);

        this.camera = new Camera(new Vector2f());

        System.out.println("Scene started");
    }

    public void loadResources() {
        // do nothing right now
    }

    public void update(float dt) {
        DebugDraw.addBox2D(obj1.position, new Vector2f(obj1.position).add(new Vector2f(32, 32)), 0.0f, new Vector3f(1.0f, 0.0f, 0.0f), 1);
        DebugDraw.addBox2D(obj2.position, new Vector2f(obj2.position).add(new Vector2f(32, 32)), 0.0f, new Vector3f(0.0f, 1.0f, 1.0f), 1);

        physics.update(dt);

        camera.update();
    }

    public Camera getCamera() {
        return camera;
    }
}
