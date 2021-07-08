package physics2d.rigidbody;

import org.joml.Matrix2f;
import org.joml.Vector2f;
import physics2d.primitives.AABB;
import physics2d.World;

public abstract class RigidBody {
    public AABB aabb;
    float mass, inertia, angle, angVelocity, torque;
    Vector2f force;
    Vector2f linVelocity;

    public RigidBody(AABB aabb, float mass) {
        this.aabb = aabb;
        this.mass = mass;
        this.angle = 0;
        this.angVelocity = 0;
        this.torque = 0;
        this.force = new Vector2f();
        this.linVelocity = new Vector2f();
        this.calcInertia();
        // no need to calculate torque right now, calcTorque() would return 0.
    }

    public void calcInertia() {
        float w = aabb.size.x;
        float h = aabb.size.y;

        this.inertia = mass * (w * w + h * h) / 12;
    }

    public void calcTorque() {
        Vector2f r = new Vector2f(aabb.size.x / 2, aabb.size.y / 2);

        this.torque = new Matrix2f(this.force, r).determinant();
    }

    public void update(float dt) {
        this.force = World.getGravity();
        this.calcInertia();
        this.calcTorque();
        Vector2f linAccel = new Vector2f(force).div(mass);
        float angAccel = torque / inertia;

        linVelocity.add(linAccel.mul(dt));
        angVelocity += angAccel * dt;

        aabb.min.add(new Vector2f(linVelocity).mul(dt));
        aabb.max.add(new Vector2f(linVelocity).mul(dt));

        angle += angVelocity * dt;
    }

    @Override
    public String toString() {
        return "RigidBody{" +
                "aabb=" + aabb +
                ", angle=" + angle +
                '}';
    }
}
