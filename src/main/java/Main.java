import math.Vector2f;
import physics2d.AABB;
import physics2d.Particle;
import physics2d.World;
import rigidbody.RigidBody;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        World world = new World();
        world.add(new RigidBody(new AABB(0, 0, 10, 10), 10));

        for (int i = 0; i < 1000; i++) {
            world.step(0.01f);
            System.out.println(world.entities.get(0));
            Thread.sleep(10);
        }
    }
}
