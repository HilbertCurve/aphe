package aphe;

import org.joml.Vector2f;
import aphe.forces.ForceRegistry;
import aphe.forces.Gravity2D;
import aphe.rigidbody.Rigidbody2D;

import java.util.ArrayList;
import java.util.List;

public class PhysicsSystem2D {
    private ForceRegistry forceRegistry;
    private List<Rigidbody2D> rigidbodies;
    private Gravity2D gravity;
    private float fixedUpdate;

    public PhysicsSystem2D(float fixedUpdateDt, Vector2f gravity) {
        this.forceRegistry = new ForceRegistry();
        this.rigidbodies = new ArrayList<>();
        this.gravity = new Gravity2D(gravity);
        this.fixedUpdate = fixedUpdateDt;
    }

    public void update(float dt) {
        forceRegistry.updateForces(dt);

        // Update the velocities of all rigidbodies
        for (Rigidbody2D rigidbody : rigidbodies)
            rigidbody.physicsUpdate(dt);
    }

    public void fixedUpdate() {
        update(fixedUpdate);
    }

    public void addRigidbody(Rigidbody2D body) {
        this.rigidbodies.add(body);
        this.forceRegistry.add(body, gravity);
    }
}
