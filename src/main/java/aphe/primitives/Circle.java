package aphe.primitives;

import org.joml.Vector2f;
import org.joml.Vector3f;
import aphe.rigidbody.Rigidbody2D;

public class Circle {
    private float radius = 1.0f;
    private Rigidbody2D rigidbody = null;
    private Vector3f color;
    private int lifetime;

    public Circle() {

    }

    public Circle(Vector2f center, float radius) {
        this.radius = radius;
        this.rigidbody = new Rigidbody2D(center);
    }

    public Circle(Vector2f center, Vector2f point) {
        this.radius = center.distance(point);
        this.rigidbody = new Rigidbody2D(center);
    }

    public Circle(Vector2f center, float radius, Vector3f color, int lifetime) {
        this.radius = radius;
        this.rigidbody = new Rigidbody2D(center);
        this.color = color;
        this.lifetime = lifetime;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public Vector2f getCenter() {
        return rigidbody.getPosition();
    }

    public float getRotation() {
        return rigidbody.getRotation();
    }

    public Vector3f getColor() {
        return color;
    }

    public void setRigidbody(Rigidbody2D rb) {
        this.rigidbody = rb;
    }

    public int beginFrame() {
        this.lifetime--;
        return this.lifetime;
    }
}
