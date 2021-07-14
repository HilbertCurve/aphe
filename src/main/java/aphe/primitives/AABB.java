package aphe.primitives;

import org.joml.Vector2f;
import aphe.rigidbody.Rigidbody2D;

public class AABB {
    private Vector2f size = new Vector2f();
    private Vector2f halfSize = new Vector2f();
    private Rigidbody2D rigidbody = new Rigidbody2D();

    public AABB() {

    }

    public AABB(Vector2f min, Vector2f max) {
//      this.size = new Vector2f(max.x - min.x, max.y - min.y);
        this.size = new Vector2f(max).sub(min);
        this.halfSize = new Vector2f(size.mul(0.5f));
    }

    public Vector2f getMin() {
        return new Vector2f(this.rigidbody.getPosition()).sub(this.halfSize);
    }

    public Vector2f getMax() {
        return new Vector2f(this.rigidbody.getPosition()).add(this.halfSize);
    }

    public void setRigidbody(Rigidbody2D rb) {
        this.rigidbody = rb;
    }

    public void setSize(Vector2f size) {
        this.size.set(size);
        this.halfSize.set(new Vector2f(size).div(2));
    }
}
