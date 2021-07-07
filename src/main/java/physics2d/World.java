package physics2d;

import math.Vector2f;
import rigidbody.RigidBody;

import java.util.ArrayList;
import java.util.List;

public class World {
    public List<Object> entities = new ArrayList<>();
    private static Vector2f gravity = new Vector2f(0, 9.8f);

    public void add(Object e) {
        this.entities.add(e);
    }

    public void step(float dt) {
        for (Object e : entities) {
            if (e.getClass().equals(RigidBody.class)) ((RigidBody) e).update(dt);
        }
    }

    public static Vector2f getGravity() {
        return gravity;
    }

    public static void setGravity(Vector2f g) {
        gravity = g;
    }
}
