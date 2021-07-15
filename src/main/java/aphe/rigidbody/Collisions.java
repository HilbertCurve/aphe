package aphe.rigidbody;

import aphe.primitives.Box2D;
import aphe.primitives.Circle;
import aphe.primitives.Collider2D;
import org.joml.Vector2f;
import java.lang.Math;

import static aphe.rigidbody.IntersectionDetector2D.circleAndCircle;
import static aphe.util.MyMath.*;

public class Collisions {
    public static CollisionManifold findCollisionFeatures(Collider2D c1, Collider2D c2) {
        if (c1 instanceof Circle && c2 instanceof Circle) {
            return findCollisionFeatures((Circle) c1, (Circle) c2);
        } else if (c1 instanceof Circle && c2 instanceof Box2D) {
            return findCollisionFeatures((Circle) c1, (Box2D) c2);
        } else if (c1 instanceof Box2D && c2 instanceof Circle) {
            return findCollisionFeatures((Circle) c2, (Box2D) c1);
        } else {
            System.err.println("ERROR: Colliders of types " + c1.getClass() + " and " + c2.getClass() + " are not supported yet or will not be supported.");
            return null;
        }
    }

    public static CollisionManifold findCollisionFeatures(Circle a, Circle b) {
        CollisionManifold result = new CollisionManifold();
        float sumRadii = a.getRadius() + b.getRadius();
        Vector2f distance = new Vector2f(b.getCenter()).sub(a.getCenter());
        // if not colliding, return empty result
        if (!circleAndCircle(a, b))
            return result;

        /*
        * How to calculate penetration const:
        * The ratio of two equal-momentum objects is 1, which would
        * correspond to a const of 0.5f. The ratio between
        * an immovable object's momentum and any non-immovable object
        * would be infinite, corresponding to a const of 1. The inverse
        * of this would correspond to a const of 0; therefore, the function I
        * will define to balance this out will be the following:
        *
        * float const = (float) Math.exp(\euler's number\, (-1/2) * (momentum1/momentum2)) * invSqrt(2*PI);
        *
        * (this is the normalcdf() function)
        *
        * NOTE: invSqrt(2 * PI) will be computed at compile-time
        */
        Vector2f normal = new Vector2f(distance).normalize();

        Rigidbody2D rba = a.getRigidbody();
        Rigidbody2D rbb = b.getRigidbody();

        float penetrationConst = (float) normalcdf(
                rba.getMass() * rba.getVelocity().dot(normal) -
                rbb.getMass() * rbb.getVelocity().dot(normal),
                false
        );

        float depth = Math.abs(distance.length() - sumRadii) * penetrationConst;

        float distanceToPoint = a.getRadius() - depth;
        Vector2f contactPoint = new Vector2f(a.getCenter())
                .add(new Vector2f(normal).mul(distanceToPoint));

        result = new CollisionManifold(normal, depth);
        result.addContactPoint(contactPoint);
        return result;
    }

    public static CollisionManifold findCollisionFeatures(Circle a, Box2D b) {
        return null;
    }
}
