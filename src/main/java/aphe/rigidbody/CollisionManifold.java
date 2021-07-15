package aphe.rigidbody;

import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;

public class CollisionManifold {
    private final Vector2f normal;
    private final List<Vector2f> contactPoints;
    private final float depth;
    private boolean isColliding;

    public CollisionManifold() {
        normal = new Vector2f();
        depth = 0.0f;
        contactPoints = new ArrayList<>();
        isColliding = false;
    }

    public CollisionManifold(Vector2f normal, float depth) {
        this.normal = normal;
        this.contactPoints = new ArrayList<>();
        this.depth = depth;
        isColliding = true;
    }

    public void addContactPoint(Vector2f contact) {
        this.contactPoints.add(contact);
    }

    public Vector2f getNormal() {
        return normal;
    }

    public List<Vector2f> getContactPoints() {
        return contactPoints;
    }

    public float getDepth() {
        return depth;
    }

    public boolean isColliding() {
        return isColliding;
    }
}
