package sandbox;

import aphe.PhysicsSystem2D;
import aphe.forces.Transform;
import aphe.primitives.Box2D;
import aphe.primitives.Circle;
import aphe.primitives.Collider2D;
import aphe.rigidbody.Rigidbody2D;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

import static aphe.util.MyMath.normalcdf;

public class Scene {
    private Camera camera = new Camera(new Vector2f());

    PhysicsSystem2D physics = new PhysicsSystem2D(1.0f / 60.0f, new Vector2f(0f, -98f));

    List<Collider2D> thingsToDisplay = new ArrayList<>();

    public void start() {
        loadResources();

        addCircle(new Circle(new Vector2f(-360, 45), 25, new Vector3f(1.0f, 0.0f, 0.0f)), 250, true);
        addCircle(new Circle(new Vector2f(-300, 45), 25, new Vector3f(1.0f, 0.0f, 0.0f)), 250, true);
        addCircle(new Circle(new Vector2f(-260, 45), 25, new Vector3f(1.0f, 0.0f, 0.0f)), 250, true);
        addCircle(new Circle(new Vector2f(-160, 45), 25, new Vector3f(1.0f, 0.0f, 0.0f)), 250, true);
        addCircle(new Circle(new Vector2f(-350, 100), 25, new Vector3f(0.0f, 0.0f, 0.0f)), 25, true);

        addBox2D(new Vector2f(0, -300), new Vector2f(800, 20), 0, Rigidbody2D.IMMOVABLE, false);
        addBox2D(new Vector2f(-400, 0), new Vector2f(20, 600), 0, Rigidbody2D.IMMOVABLE, false);
        addBox2D(new Vector2f(0, 300), new Vector2f(800, 20), 0, Rigidbody2D.IMMOVABLE, false);
        addBox2D(new Vector2f(400, 0), new Vector2f(20, 600), 0, Rigidbody2D.IMMOVABLE, false);

        this.camera = new Camera(new Vector2f());

        System.out.println("Scene started");
    }

    public void loadResources() {
        // do nothing right now, but if there were any
        // textures to grab, we'd do it here.
    }

    public void update(float dt) {
        for (Collider2D c : thingsToDisplay) {
            if (c instanceof Circle) {
                DebugDraw.addCircle(((Circle) c).getCenter(), ((Circle) c).getRadius(), ((Circle) c).getRotation(), ((Circle) c).getColor(), 1);
            } else if (c instanceof Box2D) {
                DebugDraw.addBox2D(((Box2D) c).getPosition(), ((Box2D) c).getSize(), ((Box2D) c).getRotation());
            }
        }

        physics.fixedUpdate();

        camera.update();
    }

    public Camera getCamera() {
        return camera;
    }

    public void addCircle(Vector2f center, float radius, float mass, boolean addGravity) {
        Circle c = new Circle(center, radius, new Vector3f((float) normalcdf(mass, true), 0.0f, 0.0f), 1);

        Rigidbody2D rb = new Rigidbody2D();
        rb.setRawTransform(new Transform(center));
        rb.setMass(mass);

        c.setRigidbody(rb);
        rb.setCollider(c);

        physics.addRigidbody(rb, addGravity);

        thingsToDisplay.add(c);
    }

    public void addCircle(Circle c, float mass, boolean addGravity) {
        Rigidbody2D rb = new Rigidbody2D(c.getCenter(), c.getRotation());
        rb.setRawTransform(new Transform(c.getCenter()));
        rb.setMass(mass);

        c.setRigidbody(rb);
        rb.setCollider(c);

        physics.addRigidbody(rb, addGravity);

        thingsToDisplay.add(c);
    }

    public void addBox2D(Vector2f position, Vector2f size, float rotation, float mass, boolean addGravity) {
        Box2D b = new Box2D();
        b.setSize(size);

        Rigidbody2D rb = new Rigidbody2D();
        rb.setRawTransform(new Transform(position));
        rb.setMass(mass);
        rb.setRotation(rotation);

        b.setRigidbody(rb);
        rb.setCollider(b);

        physics.addRigidbody(rb, addGravity);

        thingsToDisplay.add(b);
    }
}
