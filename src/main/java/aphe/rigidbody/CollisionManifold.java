package aphe.rigidbody;

import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;

public class CollisionManifold {
    private final Vector2f normal;
    private final List<Vector2f> contactPoints;
    private float depth;
    private float penetrationConst;
    private boolean isColliding;

    public CollisionManifold() {
        normal = new Vector2f();
        depth = 0.0f;
        contactPoints = new ArrayList<>();
        isColliding = false;
    }

    public CollisionManifold(Vector2f normal, float depth, float penetrationConst) {
        this.normal = normal;
        this.contactPoints = new ArrayList<>();
        this.depth = depth;
        this.penetrationConst = penetrationConst;
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

    public void setDepth(float depth) {
        this.depth = depth;
    }

    public float getPenetrationConst() {
        return penetrationConst;
    }

    public boolean isColliding() {
        return isColliding;
    }
}
