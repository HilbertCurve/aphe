package physics2d.primitives;

import org.joml.Vector2f;
import physics2d.primitives.AABB;
import physics2d.rigidbody.RigidBody;

public class Box2D extends RigidBody {
    public Vector2f p0, p3, p1, p2;
    /*
    *   p1-------->p2
    *   ^           |
    *   |           |
    *   |           v
    *   p0<--------p3
    */

    public Box2D(AABB aabb, float mass) {
        super(aabb, mass);
        p0 = new Vector2f(aabb.min);
        p1 = new Vector2f(aabb.min.x, aabb.max.y);
        p2 = new Vector2f(aabb.max);
        p3 = new Vector2f(aabb.max.x, aabb.min.y);
    }
}
