package physics2d;

import math.Vector2f;

public class Particle {
    public Vector2f position;
    public Vector2f velocity = new Vector2f();
    public float mass;

    public Particle() {
        this.position = new Vector2f();
        this.mass = 1;
    }
    public Particle(Vector2f position, float mass) {
        this.position = position;
        this.mass = mass;
    }

    public void accel(Vector2f v, float dt) {
        Vector2f vAdjusted = new Vector2f(v);
        vAdjusted.mul(dt).div(this.mass);
        this.velocity.add(v);
    }

    public void update(float dt) {
        this.position.add(velocity.mul(dt));
        this.accel(World.getGravity(), dt);
    }

    @Override
    public String toString() {
        return position.toString();
    }
}
