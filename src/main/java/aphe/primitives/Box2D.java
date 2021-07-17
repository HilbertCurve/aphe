package aphe.primitives;

import org.joml.Vector2f;
import org.joml.Vector3f;
import aphe.rigidbody.Rigidbody2D;
import aphe.util.MyMath;

public class Box2D extends Collider2D {
    private Vector2f size = new Vector2f();
    private Vector2f halfSize = new Vector2f();
    private Rigidbody2D rigidbody = null;
    private Vector3f color = new Vector3f(0.0f, 0.0f, 0.0f);
    private int lifetime; // TODO: remove me
    private Vector2f[] vertices = null;

    public Box2D() {

    }

    public Box2D(Vector2f min, Vector2f max) {
        this.size = new Vector2f(max).sub(min);
        this.halfSize = new Vector2f(size.mul(0.5f));
        this.rigidbody = new Rigidbody2D(new Vector2f(min).add(halfSize));
        rigidbody.setInertia(rigidbody.getMass() * (size.dot(size)) / 12);
    }

    public Box2D(Vector2f min, Vector2f max, Vector3f color) {
        this.size = new Vector2f(max).sub(min);
        this.halfSize = new Vector2f(size.mul(0.5f));
        this.rigidbody = new Rigidbody2D(new Vector2f(min).add(halfSize));
        rigidbody.setInertia(rigidbody.getMass() * (size.dot(size)) / 12);
        this.color = color;
    }

    public Box2D(Vector2f min, Vector2f max, Vector3f color, int lifetime) {
        this.size = new Vector2f(max).sub(min);
        this.halfSize = new Vector2f(size.mul(0.5f));
        this.rigidbody = new Rigidbody2D(new Vector2f(min).add(halfSize));
        rigidbody.setInertia(rigidbody.getMass() * (size.dot(size)) / 12);
        this.color = color;
        this.lifetime = lifetime;
    }

    public Box2D(Vector2f min, Vector2f max, float rotation, Vector3f color, int lifetime) {
        this.size = new Vector2f(max).sub(min);
        this.halfSize = new Vector2f(size.mul(0.5f));
        this.rigidbody = new Rigidbody2D(new Vector2f(min).add(halfSize), rotation);
        rigidbody.setInertia(rigidbody.getMass() * (size.dot(size)) / 12);
        this.color = color;
        this.lifetime = lifetime;
    }


    public Vector2f getLocalMin() {
        return new Vector2f(this.getPosition()).sub(this.halfSize);
    }

    public Vector2f getLocalMax() {
        return new Vector2f(this.getPosition()).add(this.halfSize);
    }

    public Vector2f[] getVertices() {
        if (vertices == null) {
            Vector2f min = getLocalMin();
            Vector2f max = getLocalMax();

            vertices = new Vector2f[]{
                    new Vector2f(min.x, min.y),
                    new Vector2f(min.x, max.y),
                    new Vector2f(max.x, min.y),
                    new Vector2f(max.x, max.y)
            };

            for (Vector2f vert : vertices) {
                MyMath.rotate(this.rigidbody.getPosition(), vert, this.rigidbody.getRotation());
            }
        }

        return vertices;
    }

    public Vector2f getSize() {
        return size;
    }

    public void setSize(Vector2f size) {
        this.size.set(size);
        this.halfSize.set(new Vector2f(size).div(2));
    }

    public Rigidbody2D getRigidbody() {
        return rigidbody;
    }

    public void setRigidbody(Rigidbody2D rb) {
        this.rigidbody = rb;
    }

    public Vector2f getPosition() {
        return rigidbody.getPosition();
    }

    public float getRotation() {
        return rigidbody.getRotation();
    }

    public Vector3f getColor() {
        return color;
    }

    public void setColor(Vector3f color) {
        this.color = color;
    }

    public Vector2f getHalfSize() {
        return halfSize;
    }

    public int beginFrame() {
        this.lifetime--;
        return this.lifetime;
    }
}
