package physics2d.primitives;

import org.joml.Vector2f;
import physics2d.rigidbody.RigidBody;

public class AABB {
    // TODO: make these private and add getters/setters
    public Vector2f min; // bottom left corner
    public Vector2f max; // top right corner
    public Vector2f size;
    private RigidBody rigidbody = null;


    public AABB() {
        this.min = new Vector2f();
        this.max = new Vector2f();
        this.size = new Vector2f();

        this.rigidbody = new Box2D(this, 10);
    }

    public AABB(Vector2f min, Vector2f size) {
        this.min = min;
        this.size = size;
        this.max = new Vector2f(min).add(size);
    }

    public AABB(float x, float y, float w, float h) {
        this.min = new Vector2f(x, y);
        this.size = new Vector2f(w, h);
        this.max = new Vector2f(min).add(size);
    }

    public boolean intersects(AABB aabb) {
        boolean xIsIn =
                !(this.max.x < aabb.min.x ||
                this.min.x > this.max.x);

        boolean yIsIn =
                !(this.max.y < aabb.min.y ||
                this.min.y > this.max.y);

        return xIsIn && yIsIn;
    }

    @Override
    public String toString() {
        return "(" + this.min.x + ", " + this.min.y + ")";
    }
}
