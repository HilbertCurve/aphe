import physics2d.primitives.AABB;
import physics2d.World;
import physics2d.primitives.Box2D;
import physics2d.rigidbody.RigidBody;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        World world = new World();
        world.add(new Box2D(new AABB(0, 0, 10, 10), 10));

        for (int i = 0; i < 1000; i++) {
            world.step(0.01f);
            System.out.println(world.entities.get(0));
            Thread.sleep(10);
        }
    }
}
