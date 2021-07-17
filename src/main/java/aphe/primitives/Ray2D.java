package aphe.primitives;

import org.joml.Vector2f;

public class Ray2D {
    private Vector2f origin;
    private Vector2f direction;

    public Ray2D(Vector2f origin, Vector2f direction) {
        this.origin = origin;
        this.direction = direction;
        this.direction.normalize();
    }

    public Ray2D(Line2D line, boolean startAtEnd) {
        this.origin = startAtEnd ? line.getTo() : line.getFrom();
        this.direction = startAtEnd ? new Vector2f(line.getTo()).sub(line.getFrom()) : new Vector2f(line.getFrom()).sub(line.getTo());
        this.direction.normalize();
    }

    public Vector2f getOrigin() {
        return origin;
    }

    public Vector2f getDirection() {
        return direction;
    }
}
