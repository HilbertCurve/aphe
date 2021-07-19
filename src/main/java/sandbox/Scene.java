package sandbox;

import aphe.PhysicsSystem2D;
import aphe.forces.Transform;
import aphe.primitives.Box2D;
import aphe.primitives.Circle;
import aphe.primitives.Collider2D;
import aphe.rigidbody.Rigidbody2D;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

import static aphe.primitives.Collider2D.DEFAULT_COLOR;
import static aphe.util.MyMath.normalcdf;

public class Scene {
    private Camera camera = new Camera(new Vector2f());

    PhysicsSystem2D physics = new PhysicsSystem2D(1.0f / 60.0f, new Vector2f(0f, -98f));

    List<Collider2D> thingsToDisplay = new ArrayList<>();

    public void start() {
        loadResources();

        addCircle(new Vector2f(50, 200), 25, 40, true);

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

    float accum = 2.0f;
    public void update(float dt) {
        for (Collider2D c : thingsToDisplay) {
            if (c instanceof Circle) {
                Renderer.addCircle(((Circle) c).getCenter(), ((Circle) c).getRadius(), ((Circle) c).getRotation(), ((Circle) c).getColor(), 1);
            } else if (c instanceof Box2D) {
                Renderer.addBox2D(((Box2D) c).getPosition(), ((Box2D) c).getSize(), ((Box2D) c).getRotation());
            }
        }

        // simple circle spawning script (not completed)
        Vector2f pos = new Vector2f(MouseListener.getX(), MouseListener.getY());
        pos.sub(Window.getWidth()/2f, Window.getHeight()/2f);
        pos.mul(1f/Window.getWidth(), -1f/Window.getHeight());
        pos.mul(32*40, 32*21);

        if (MouseListener.mouseButtonDown(0) && accum >= 2.0f) {
            accum = 0;
            addCircle(pos, 25, 20, true);
        } else if (MouseListener.mouseButtonDown(1) && accum >= 2.0f) {
            accum = 0;
            addCircle(pos, 25, Rigidbody2D.IMMOVABLE, false);
        } else if (!MouseListener.mouseButtonDown(1) && !MouseListener.mouseButtonDown(0)) {
            accum = 2.0f;
        }

        physics.fixedUpdate();

        camera.update();

        accum += dt;
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
