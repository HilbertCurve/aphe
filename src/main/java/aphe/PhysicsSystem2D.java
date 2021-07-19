package aphe;

import aphe.primitives.Collider2D;
import aphe.rigidbody.CollisionManifold;
import aphe.rigidbody.Collisions;
import org.joml.Vector2f;
import aphe.forces.ForceRegistry;
import aphe.forces.Gravity2D;
import aphe.rigidbody.Rigidbody2D;
import sandbox.DebugDraw;

import java.util.ArrayList;
import java.util.List;

import static aphe.primitives.Collider2D.DEFAULT_COLOR;
import static aphe.rigidbody.IntersectionDetector2D.colliderAndCollider;

public class PhysicsSystem2D {
    private ForceRegistry forceRegistry;
    private Gravity2D gravity;

    private List<Rigidbody2D> rigidbodies;
    private List<Rigidbody2D> bodies1;
    private List<Rigidbody2D> bodies2;
    private List<CollisionManifold> collisions;

    private float fixedUpdate;
    private final float IMPULSE_ITERATIONS = 6;

    public PhysicsSystem2D(float fixedUpdateDt, Vector2f gravity) {
        this.forceRegistry = new ForceRegistry();
        this.gravity = new Gravity2D(gravity);
        this.rigidbodies = new ArrayList<>();
        this.bodies1 = new ArrayList<>();
        this.bodies2 = new ArrayList<>();
        this.collisions = new ArrayList<>();

        this.fixedUpdate = fixedUpdateDt;
    }

    public void update(float dt) {
        bodies1.clear();
        bodies2.clear();
        collisions.clear();

        // Update the forces
        forceRegistry.updateForces(dt);

        // Find any collisions
        // FIXME: THIS IS THE WORST WAY TO FIND COLLISIONS!!
        int size = rigidbodies.size();
        for (int i = 0; i < size; i++) {
            for (int j = i; j < size; j++) {
                if (i == j) continue;

                CollisionManifold result = new CollisionManifold();
                Rigidbody2D r1 = rigidbodies.get(i);
                Rigidbody2D r2 = rigidbodies.get(j);
                Collider2D c1 = r1.getCollider();
                Collider2D c2 = r2.getCollider();

                if (c1 != null && c2 != null && !(r1.hasInfiniteMass() && r2.hasInfiniteMass())) {
                    result = Collisions.findCollisionFeatures(c1, c2);
                }

                if (result != null && result.isColliding()) {
                    bodies1.add(r1);
                    bodies2.add(r2);
                    collisions.add(result);
                }
            }
        }

        // Resolve collisions via iterative impulse resolution
        // iterate a certain amount of times to get an approximate solution
        for (int k = 0; k < IMPULSE_ITERATIONS; k++) {
            for (int i = 0; i < collisions.size(); i++) {
                int jSize = collisions.get(i).getContactPoints().size();
                for (int j = 0; j < jSize; j++) {
                    Rigidbody2D r1 = bodies1.get(i);
                    Rigidbody2D r2 = bodies2.get(i);
                    applyImpulse(r1, r2, collisions.get(i));
                }
            }
        }

        // Update the velocities of all rigidbodies
        for (Rigidbody2D rigidbody : rigidbodies)
            rigidbody.physicsUpdate(dt);
    }

    private void applyImpulse(Rigidbody2D a, Rigidbody2D b, CollisionManifold m) {
        // Linear velocity
        float invMass1 = a.getInverseMass();
        float invMass2 = b.getInverseMass();
        float invMassSum = invMass1 + invMass2;
        if (invMassSum == 0f) return;

        // Relative velocity
        Vector2f relativeVel = new Vector2f(b.getVelocity()).sub(a.getVelocity());
        Vector2f relativeNormal = new Vector2f(m.getNormal()).normalize();
        // Moving away from each other? Do nothing
        if (relativeVel.dot(relativeNormal) > 0.0f) return;

        float e = Math.min(a.getCor(), b.getCor());
        float numerator = (-(1.0f + e) * relativeVel.dot(relativeNormal));
        float j = numerator / invMassSum;
        if (m.getContactPoints().size() > 0 && j != 0.0f)
            j /= (float) m.getContactPoints().size(); //FIXME: distribute even more evenly across contact points.

        Vector2f impulse = new Vector2f(relativeNormal).mul(j);
        if (impulse.lengthSquared() < 0.01f) {
            b.setVelocity(a.getVelocity());
            return;
        }
        a.setVelocity(
                new Vector2f(a.getVelocity())
                        .add(new Vector2f(impulse)
                                .mul(invMass1).mul(-1f))
        );
        b.setVelocity(
                new Vector2f(b.getVelocity())
                        .add(new Vector2f(impulse)
                                .mul(invMass2))
        );

        /*// find "average" contact point; this approximates where to apply angular momentum
        Vector2f arm = new Vector2f();
        arm = m.getContactPoints().get(0);

        arm.sub(b.getPosition());
        float torque = arm.x * impulse.y - arm.y * impulse.x;

        a.setAngularVelocity(a.getAngularVelocity() + torque * invMass1);
        b.setAngularVelocity(b.getAngularVelocity() - torque * invMass2);
        DebugDraw.addLine2D(new Vector2f(0,0), arm, DEFAULT_COLOR, 20);
        System.out.println(torque);*/

        // FIXME: stationary collisions are broken
        if (colliderAndCollider(a.getCollider(), b.getCollider())) {
//            a.getPosition().add(new Vector2f(m.getNormal()).mul(m.getDepth() / 2));
//            b.getPosition().add(new Vector2f(m.getNormal()).mul(m.getDepth() / 2));
        }
    }

    public void fixedUpdate() {
        update(fixedUpdate);
    }

    public void addRigidbody(Rigidbody2D body, boolean addGravity) {
        this.rigidbodies.add(body);
        if (addGravity)
            this.forceRegistry.add(body, gravity);
    }
}
