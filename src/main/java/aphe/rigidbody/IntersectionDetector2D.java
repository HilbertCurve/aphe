package aphe.rigidbody;

import org.joml.Vector2f;
import aphe.primitives.*;
import org.joml.Vector4f;

import static aphe.util.MyMath.*;

public class IntersectionDetector2D {
    /* POINT-VS-PRIMITIVE TESTS */
    public static boolean pointOnLine(Vector2f point, Line2D line) {
        float dy = line.getEnd().y - line.getStart().y;
        float dx = line.getEnd().x - line.getStart().x;
        if (dx == 0f) {
            return compare(point.x, line.getStart().x) &&
                   point.y >= line.getStart().y &&
                   point.y <= line.getEnd().y;
        }
        float m = dy / dx;

        float b = line.getEnd().y - m * line.getEnd().x;

        // Check the line equation
        return point.y == m * point.x + b;
    }

    public static boolean pointAndCircle(Vector2f point, Circle circle) {
        return point.distanceSquared(circle.getCenter()) < circle.getRadius() * circle.getRadius();
    }

    public static boolean pointAndAABB(Vector2f point, AABB box) {
        Vector2f max = box.getMax();
        Vector2f min = box.getMax();

        return point.x >= min.x && point.x <= max.x &&
               point.y >= min.y && point.y <= max.y;
    }

    public static boolean pointAndBox2D(Vector2f point, Box2D box) {
        Vector2f pointLocalBoxSpace = new Vector2f(point);
        rotate(
                box.getRigidbody().getPosition(),
                pointLocalBoxSpace,
                box.getRigidbody().getRotation()
        );

        Vector2f max = box.getLocalMax();
        Vector2f min = box.getLocalMin();

        return pointLocalBoxSpace.x >= min.x &&
               pointLocalBoxSpace.x <= max.x &&
               pointLocalBoxSpace.y >= min.y &&
               pointLocalBoxSpace.y <= max.y;
    }

    /* LINE-VS-PRIMITIVE TESTS */
    public static boolean lineAndCircle(Line2D line, Circle circle) {
        if (pointAndCircle(line.getStart(), circle) || pointAndCircle(line.getEnd(), circle))
            return true;

        // circle center to line start
        Vector2f cCenterToLStart = new Vector2f(circle.getCenter()).sub(line.getStart());
        // line segment from a to b
        Vector2f ab = new Vector2f(line.getEnd()).sub(line.getStart());

        // project point (circle position) onto ab
        // parameterized position t
        float t = cCenterToLStart.dot(ab) / ab.dot(ab);

        if (t < 0.0f || t > 1.0f)
            return false;

        // find the closest point to the line segment
        Vector2f n = new Vector2f(line.getStart()).add(ab.mul(t));

        return pointAndCircle(n, circle);
    }

    public static boolean lineAndAABB(Line2D line, AABB box) {
        if (pointAndAABB(line.getStart(), box) || pointAndAABB(line.getEnd(), box)) {
            return true;
        }

        /* NORMALIZE */
        // do some math with the line
        Vector2f unitVector = new Vector2f(line.getEnd()).sub(line.getStart());
        unitVector.normalize();
        unitVector.x = (unitVector.x != 0) ? 1.0f / unitVector.x : 0f;
        unitVector.y = (unitVector.y != 0) ? 1.0f / unitVector.y : 0f;

        /* GET INTERSECTION POINTS ON THE AABB */
        // find where on the box the line intersects
        Vector2f min = box.getMin();
        min.sub(line.getStart()).mul(unitVector);
        Vector2f max = box.getMax();
        max.sub(line.getStart()).mul(unitVector);

        /* GET THE ANSWER */
        // use the previous information to see if the line intersects the box
        float tmin = max(min(min.x, max.x), min(min.y, max.y));
        float tmax = min(max(min.x, max.x), max(min.y, max.y));

        if(tmax < 0 || tmin > tmax)
            return false;

        float t = (tmin < 0f) ? tmax : tmin;
        return t > 0f && t * t < line.lengthSquared();
    }

    public static boolean lineAndBox2D(Line2D line, Box2D box) {
        /* LOCALIZE LINE (ROTATE IT TO MATCH BOX2D ROTATION) */
        float theta = box.getRigidbody().getRotation();
        Vector2f center = box.getRigidbody().getPosition();
        Vector2f localStart = new Vector2f(line.getStart());
        Vector2f localEnd = new Vector2f(line.getEnd());
        rotate(center, localStart, -theta);
        rotate(center, localEnd, -theta);

        /* PERFORM lineAndAABB ON LOCALIZED LINE */
        Line2D localLine = new Line2D(localStart, localEnd);
        AABB aabb = new AABB(box.getLocalMin(), box.getLocalMax());

        return lineAndAABB(localLine, aabb);
    }

    /* RAYCASTS */
    public static boolean raycast(Circle circle, Ray2D ray, RaycastResult result) {
        RaycastResult.reset(result);

        Vector2f originToCircle = new Vector2f(circle.getCenter()).sub(ray.getOrigin());
        float radiusSquared = circle.getRadius() * circle.getRadius();
        float originToCircleLengthSquared = originToCircle.lengthSquared();

        // project the vector from the ray origin onto the direction of the ray
        float a = originToCircle.dot(ray.getDirection());
        float bSq = originToCircleLengthSquared - (a * a);
        if (radiusSquared - bSq < 0.0f)
            return false;

        float f = (float) Math.sqrt(radiusSquared - bSq);
        float t;
        if (originToCircleLengthSquared < radiusSquared) {
            // ray starts inside circle
            t = a + f;
        } else {
            t = a - f;
        }

        if (result != null) {
            Vector2f point = new Vector2f(ray.getOrigin()).add(new Vector2f(ray.getDirection()).mul(t));
            Vector2f normal = new Vector2f(point).sub(circle.getCenter());
            normal.normalize();

            result.init(point, normal, t, true);
        }

        return true;
    }

    public static boolean raycast(Line2D line, Ray2D ray, RaycastResult result) {
        RaycastResult.reset(result);

        Vector2f intersectionPoint = intersectLines(
                ray,
                new Ray2D(line.getStart(), new Vector2f(line.getEnd()).sub(line.getStart()))
        );

        boolean lineWasHit = pointOnLine(intersectionPoint, line);
        float t = ray.getOrigin().distance(intersectionPoint);
        if (result != null) {
            result.init(intersectionPoint, ray.getDirection(), t, lineWasHit);
        }

        return lineWasHit;
    }

    public static boolean raycast(AABB box, Ray2D ray, RaycastResult result) {
        RaycastResult.reset(result);
        /* NORMALIZE */
        // do some math with the line
        Vector2f unitVector = ray.getDirection();
        unitVector.normalize();
        unitVector.x = (unitVector.x != 0) ? 1.0f / unitVector.x : 0f;
        unitVector.y = (unitVector.y != 0) ? 1.0f / unitVector.y : 0f;

        /* GET INTERSECTION POINTS ON THE AABB */
        // find where on the box the line intersects
        Vector2f min = box.getMin();
        min.sub(ray.getOrigin()).mul(unitVector);
        Vector2f max = box.getMax();
        max.sub(ray.getOrigin()).mul(unitVector);

        /* GET THE ANSWER */
        // use the previous information to see if the line intersects the box
        float tmin = max(min(min.x, max.x), min(min.y, max.y));
        float tmax = min(max(min.x, max.x), max(min.y, max.y));

        if(tmax < 0 || tmin > tmax)
            return false;

        float t = (tmin < 0f) ? tmax : tmin;
        boolean hit = t > 0f;
        if (!hit)
            return false;

        if (result != null) {
            Vector2f point = new Vector2f(ray.getOrigin()).add(new Vector2f(ray.getDirection()).mul(t));

            Vector2f normal = new Vector2f(ray.getOrigin()).sub(point);
            normal.normalize();

            result.init(point, normal, t, true);
        }

        return true;
    }

    public static boolean raycast(Box2D box, Ray2D ray, RaycastResult result) {
        RaycastResult.reset(result);

        Vector2f size = box.getHalfSize();
        Vector2f xAxis = new Vector2f(1, 0);
        Vector2f yAxis = new Vector2f(0, 1);
        rotate(xAxis, new Vector2f(0, 0), box.getRigidbody().getRotation());
        rotate(yAxis, new Vector2f(0, 0), box.getRigidbody().getRotation());

        Vector2f p = new Vector2f(box.getRigidbody().getPosition()).sub(ray.getOrigin());
        // project the direction of the ray onto each axis of the box

        Vector2f f = new Vector2f(
                xAxis.dot(ray.getDirection()),
                yAxis.dot(ray.getDirection())
        );
        // project p onto every axis of the box
        Vector2f e = new Vector2f(
                xAxis.dot(p),
                yAxis.dot(p)
        );

        float[] tArr = {0, 0, 0, 0};
        for (int i = 0; i < 2; i++) {
            if (compare(f.get(i), 0)) {
                // if the ray is parallel to the current axis, and the origin of the
                // ray is not inside we have no hit
                if (-e.get(i) - size.get(i) > 0 || -e.get(i) + size.get(i) < 0)
                    return false;
                f.setComponent(i, 0.00001f);
            }
            tArr[i * 2] = (e.get(i) + size.get(i)) / f.get(i); // tmax for this axis
            tArr[i * 2 + 1] = (e.get(i) - size.get(i)) / f.get(i); // tmin for this axis
        }

        float tmin = max(min(tArr[0], tArr[1]), min(tArr[2], tArr[3]));
        float tmax = min(max(tArr[0], tArr[1]), min(tArr[2], tArr[3]));

        float t = (tmin < 0f) ? tmax : tmin;
        boolean hit = t > 0f;
        if (!hit)
            return false;

        if (result != null) {
            Vector2f point = new Vector2f(ray.getOrigin()).add(new Vector2f(ray.getDirection()).mul(t));

            Vector2f normal = new Vector2f(ray.getOrigin()).sub(point);
            normal.normalize();

            result.init(point, normal, t, true);
        }

        return true;
    }

    /* CIRCLE-VS-PRIMITIVE TESTS */
    public static boolean circleAndLine(Circle circle, Line2D line) {
        return lineAndCircle(line, circle);
    }

    public static boolean circleAndCircle(Circle c1, Circle c2) {
        float dist = c1.getCenter().distanceSquared(c2.getCenter());
        float num = c1.getRadius() + c2.getRadius();
        return dist <= num * num;
    }

    public static boolean circleAndAABB(Circle circle, AABB box) {
        Vector2f min = box.getMin();
        Vector2f max = box.getMax();

        /* FIND CLOSEST POINT */
        Vector2f closestPointToCircle = new Vector2f(circle.getCenter());

        if (circle.getCenter().x < min.x)
            closestPointToCircle.x = min.x;
        else if (circle.getCenter().x > max.x)
            closestPointToCircle.x = max.x;

        if (circle.getCenter().y < min.y)
            closestPointToCircle.y = min.y;
        else if (circle.getCenter().y > max.y)
            closestPointToCircle.y = max.y;

        return closestPointToCircle.distanceSquared(circle.getCenter()) <= circle.getRadius() * circle.getRadius();
    }

    public static boolean circleAndBox2D(Circle circle, Box2D box) {
        Vector2f center = new Vector2f(circle.getCenter());
        Vector2f min = new Vector2f(0, 0);
        Vector2f max = new Vector2f(box.getHalfSize());

        center.sub(box.getPosition());

        /* ROTATE EVERYTHING */
        rotate(new Vector2f(), center, -box.getRotation());

        center.add(box.getHalfSize());

        /* FIND CLOSEST POINT */
        Vector2f closestPointToCircle = new Vector2f(center);

        if (closestPointToCircle.x < min.x)
            closestPointToCircle.x = min.x;
        else if (closestPointToCircle.x > max.x)
            closestPointToCircle.x = max.x;

        if (closestPointToCircle.y < min.y)
            closestPointToCircle.y = min.y;
        else if (closestPointToCircle.y > max.y)
            closestPointToCircle.y = max.y;

        return closestPointToCircle.distanceSquared(center) <= circle.getRadius() * circle.getRadius();
    }

    /* AABB-VS-PRIMITIVE TESTS */
    public static boolean aabbAndCircle(AABB box, Circle circle) {
        return circleAndAABB(circle, box);
    }

    public static boolean aabbAndAABB(AABB b1, AABB b2) {
        Vector2f[] axesToTest = {new Vector2f(0, 1), new Vector2f(1, 0)};
        for (Vector2f axis : axesToTest) {
            if (overlapOnAxis(b1, b2, axis))
                return true;
        }

        return false;
    }

    public static boolean aabbAndBox2D(AABB aabb, Box2D box) {
        Vector2f[] axesToTest = {
                new Vector2f(0, 1), new Vector2f(1, 0),
                new Vector2f(0, 1), new Vector2f(1, 0)
        };

        rotate(new Vector2f(), axesToTest[2], box.getRigidbody().getRotation());
        rotate(new Vector2f(), axesToTest[3], box.getRigidbody().getRotation());

        for (Vector2f axis : axesToTest) {
            if (overlapOnAxis(aabb, box, axis))
                return true;
        }

        return false;
    }

    public static boolean box2DAndBox2D(Box2D b1, Box2D b2) {
        Vector2f[] axesToTest = {
                new Vector2f(0, 1), new Vector2f(1, 0),
                new Vector2f(0, 1), new Vector2f(1, 0)
        };

        rotate(new Vector2f(), axesToTest[0], b1.getRigidbody().getRotation());
        rotate(new Vector2f(), axesToTest[1], b1.getRigidbody().getRotation());
        rotate(new Vector2f(), axesToTest[2], b2.getRigidbody().getRotation());
        rotate(new Vector2f(), axesToTest[3], b2.getRigidbody().getRotation());

        for (Vector2f axis : axesToTest) {
            if (overlapOnAxis(b1, b2, axis))
                return true;
        }

        return false;
    }

    /* EXTRANEOUS FUNCTIONS */
    private static boolean overlapOnAxis(AABB b1, AABB b2, Vector2f axis) {
        Vector2f i1 = getInterval(b1, axis);
        Vector2f i2 = getInterval(b2, axis);
        return (i1.y >= i2.x && i1.x <= i2.y);
    }

    private static boolean overlapOnAxis(AABB b1, Box2D b2, Vector2f axis) {
        Vector2f i1 = getInterval(b1, axis);
        Vector2f i2 = getInterval(b2, axis);
        return (i1.y >= i2.x && i1.x <= i2.y);
    }

    private static boolean overlapOnAxis(Box2D b1, Box2D b2, Vector2f axis) {
        Vector2f i1 = getInterval(b1, axis);
        Vector2f i2 = getInterval(b2, axis);
        return (i1.y >= i2.x && i1.x <= i2.y);
    }

    private static Vector2f getInterval(AABB rect, Vector2f axis) {
        Vector2f a = new Vector2f(axis);

        if (compare(axis.lengthSquared(), 1, 0.00001f)) {
            a.normalize();
        }

        Vector2f min = rect.getMin();
        Vector2f max = rect.getMax();

        Vector2f[] vertices = {
                new Vector2f(min.x, min.y), new Vector2f(min.x, max.y),
                new Vector2f(max.x, min.y), new Vector2f(max.x, max.y)
        };

        float maxAlongAxis = vertices[0].dot(a);
        float minAlongAxis = vertices[0].dot(a);
        for (int i = 1; i < 4; i++) {
            maxAlongAxis = Math.max(maxAlongAxis, vertices[i].dot(a));
            minAlongAxis = Math.min(minAlongAxis, vertices[i].dot(a));
        }

        return new Vector2f(minAlongAxis, maxAlongAxis);

        // vec.x, in this case, means the minimum of the interval. vec.y means the maximum.
    }

    public static Vector2f getInterval(Box2D box, Vector2f axis) {
        Vector2f a = new Vector2f(axis).normalize();

        Vector2f[] vertices = box.getVertices();

        float maxAlongAxis = vertices[0].dot(a);
        float minAlongAxis = vertices[0].dot(a);

        for (int i = 1; i < 4; i++) {
            maxAlongAxis = Math.max(maxAlongAxis, vertices[i].dot(a));
            minAlongAxis = Math.min(minAlongAxis, vertices[i].dot(a));
        }

        return new Vector2f(minAlongAxis, maxAlongAxis);

        // vec.x, in this case, means the minimum of the interval. vec.y means the maximum.
    }
}
