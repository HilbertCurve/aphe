package physics2d;

import math.Vector2f;

public class AABB {
    public Vector2f position; // bottom left corner
    public Vector2f size;

    public AABB() {
        this.position = new Vector2f();
        this.size = new Vector2f();
    }

    public AABB(Vector2f position, Vector2f size) {
        this.position = position;
        this.size = size;
    }

    public AABB(float x, float y, float w, float h) {
        this.position = new Vector2f(x, y);
        this.size = new Vector2f(w, h);
    }

    public boolean intersects(AABB aabb) {
        boolean xIsIn =
                !(this.position.x + this.size.x < aabb.position.x ||
                this.position.x > aabb.position.x + aabb.size.x);

        boolean yIsIn =
                !(this.position.y + this.size.y < aabb.position.y ||
                        this.position.y > aabb.position.y + aabb.size.y);

        return xIsIn && yIsIn;
    }

    @Override
    public String toString() {
        return "(" + this.position.x + ", " + this.position.y + ")";
    }
}
