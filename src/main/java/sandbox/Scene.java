package sandbox;

import aphe.PhysicsSystem2D;
import aphe.forces.Transform;
import aphe.primitives.Box2D;
import aphe.primitives.Circle;
import aphe.rigidbody.Rigidbody2D;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class Scene {
    private Camera camera = new Camera(new Vector2f());

    PhysicsSystem2D physics = new PhysicsSystem2D(1.0f / 60.0f, new Vector2f(0f, -98f));
    Transform obj1, obj2, floorPos;
    Rigidbody2D rb1, rb2, floor;

    public void start() {
        loadResources();

        obj1 = new Transform(new Vector2f(200, 400));
        obj2 = new Transform(new Vector2f(230, 200));
        floorPos = new Transform(new Vector2f(-2f/Window.getHeight()));

        rb1 = new Rigidbody2D();
        rb2 = new Rigidbody2D();
        floor = new Rigidbody2D();
        rb1.setRawTransform(obj1);
        rb2.setRawTransform(obj2);
        floor.setRawTransform(floorPos);
        rb1.setMass(100);
        rb2.setMass(200);
        floor.setMass(Rigidbody2D.IMMOVABLE);
        rb1.setCor(2);

        Circle c1 = new Circle();
        c1.setRadius(25.0f);
        c1.setRigidbody(rb1);

        Circle c2 = new Circle();
        c2.setRadius(50.0f);
        c2.setRigidbody(rb2);

        Box2D b = new Box2D();
        b.setSize(new Vector2f(Window.getWidth(), 20));
        b.setRigidbody(floor);

        rb1.setCollider(c1);
        rb2.setCollider(c2);
        floor.setCollider(b);

        physics.addRigidbody(rb1, true);
        physics.addRigidbody(rb2, false);
        physics.addRigidbody(floor, false);

        this.camera = new Camera(new Vector2f());

        System.out.println("Scene started");
    }

    public void loadResources() {
        // do nothing right now, but if there were any
        // textures to grab, we'd do it here.
    }

    public void update(float dt) {
        DebugDraw.addCircle(obj1.position, 25.0f, new Vector3f(1.0f, 0.0f, 0.0f), 1);
        DebugDraw.addCircle(obj2.position, 50.0f, new Vector3f(0.0f, 1.0f, 1.0f), 1);
        DebugDraw.addBox2D(floor.getPosition(), ((Box2D) floor.getCollider()).getSize(), 0);

        physics.update(dt);

        camera.update();
    }

    public Camera getCamera() {
        return camera;
    }
}
