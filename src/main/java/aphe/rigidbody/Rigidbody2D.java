package aphe.rigidbody;

import aphe.forces.Transform;
import aphe.primitives.Collider2D;
import org.joml.Vector2f;

public class Rigidbody2D {
    private Transform rawTransform;
    private Collider2D collider;

    private Vector2f position = new Vector2f();
    private float rotation = 0.0f;
    private float mass = 0.0f;
    private float inverseMass = 0.0f;

    private Vector2f linAccum = new Vector2f();
    private float angAccum = 0.0f;
    private Vector2f linearVelocity = new Vector2f();
    private float angularVelocity = 0.0f;
    private float linearDamping = 0.0f;
    private float angularDamping = 0.0f;
    private boolean fixedRotation = true;
    // Coefficient of restitution
    private float cor = 1.0f;
    // to be implemented
    private float inertia = 0.0f;
    private boolean atRest;

    public static final float IMMOVABLE = 0.0f;

    public Rigidbody2D() {

    }

    public Rigidbody2D(Vector2f position) {
        this.position = position;
    }

    public Rigidbody2D(Vector2f position, float rotation) {
        this.position = position;
        this.rotation = rotation;
    }

    public Vector2f getPosition() {
        return position;
    }

    public void setTransform(Vector2f position, float rotation) {
        this.position.set(position);
        this.rotation = rotation;
    }

    public Vector2f getVelocity() {
        return linearVelocity;
    }

    public void setVelocity(Vector2f v) {
        this.linearVelocity = v;
    }

    public void physicsUpdate(float dt) {
        if (this.mass == IMMOVABLE) return;

        // Calculate linear velocity
        Vector2f acceleration = new Vector2f(linAccum).mul(this.inverseMass);
        linearVelocity.add(acceleration.mul(dt));

        // Update the linear position
        this.position.add(new Vector2f(linearVelocity).mul(dt));

        // Update the angular velocity
        float aAcceleration = angAccum * inverseMass;
        angularVelocity += aAcceleration * dt;

        // Update the angle
        this.setRotation(getRotation() + angularVelocity * dt);

        syncCollisionTransforms();
        clearAccumulators();
    }

    private void syncCollisionTransforms() {
        if (rawTransform != null) {
            rawTransform.position.set(this.position);
        }
    }

    private void clearAccumulators() {
        this.linAccum.zero();
        this.angAccum = 0.0f;
    }

    public float getRotation() {
        return rotation;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    public void setTransform(Vector2f position) {
        this.position.set(position);
    }

    public float getMass() {
        return mass;
    }

    public void setMass(float mass) {
        this.mass = mass;
        if (this.mass != IMMOVABLE) {
            this.inverseMass = 1.0f / this.mass;
        }
    }

    public float getInverseMass() {
        return inverseMass;
    }

    public void addForce(Vector2f v, float f) {
        this.linAccum.add(v);
        this.angAccum += f;
    }

    public void setRawTransform(Transform rawTransform) {
        this.rawTransform = rawTransform;
        this.position.set(rawTransform.position);
    }

    public boolean hasInfiniteMass() {
        return this.mass == IMMOVABLE;
    }

    public float getAngularVelocity() {
        return angularVelocity;
    }

    public void setAngularVelocity(float angularVelocity) {
        this.angularVelocity = angularVelocity;
    }

    public Collider2D getCollider() {
        return collider;
    }

    public void setCollider(Collider2D collider) {
        this.collider = collider;
    }

    public float getCor() {
        return cor;
    }

    public void setCor(float cor) {
        this.cor = cor;
    }

    public void setInertia(float inertia) {
        this.inertia = inertia;
    }

    public boolean isAtRest() {
        return this.atRest;
    }

    public void setAtRest(boolean atRest) {
        this.atRest = atRest;
    }
}
