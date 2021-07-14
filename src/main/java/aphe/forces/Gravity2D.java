package aphe.forces;

import org.joml.Vector2f;
import aphe.rigidbody.Rigidbody2D;

public class Gravity2D implements ForceGenerator {
    private Vector2f gravity;

    public Gravity2D(Vector2f gravity) {
        this.gravity = new Vector2f(gravity);
    }

    @Override
    public void updateForce(Rigidbody2D body, float dt) {
        body.addForce(new Vector2f(gravity).mul(body.getMass()));
    }
}
