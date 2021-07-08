package physics2d;

import org.joml.Vector2f;
import physics2d.rigidbody.RigidBody;

import java.util.ArrayList;
import java.util.List;

public class World {
    public List<RigidBody> entities = new ArrayList<>();
    private static Vector2f gravity = new Vector2f(0, -128f);
    public static final Vector2f CENTER = new Vector2f(0, 0);


    public void add(RigidBody e) {
        this.entities.add(e);
    }

    public void step(float dt) {
        for (RigidBody e : entities) {
            e.update(dt);
        }
    }

    public static Vector2f getGravity() {
        return gravity;
    }

    public static void setGravity(Vector2f g) {
        gravity = g;
    }
}
