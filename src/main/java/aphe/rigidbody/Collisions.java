package aphe.rigidbody;

import aphe.primitives.*;
import aphe.util.MyMath;
import org.joml.Matrix2f;
import org.joml.Vector2f;
import java.lang.Math;

import static aphe.rigidbody.IntersectionDetector2D.*;
import static aphe.util.MyMath.*;

public class Collisions {
    public static CollisionManifold findCollisionFeatures(Collider2D c1, Collider2D c2) {
        if (c1 instanceof Circle && c2 instanceof Circle) {
            return findCollisionFeatures((Circle) c1, (Circle) c2);
        } else if (c1 instanceof Circle && c2 instanceof Box2D) {
            return findCollisionFeatures((Circle) c1, (Box2D) c2);
        } else if (c1 instanceof Box2D && c2 instanceof Circle) {
            return findCollisionFeatures((Circle) c2, (Box2D) c1);
        } else if (c1 instanceof Box2D && c2 instanceof Box2D) {
            return findCollisionFeatures((Box2D) c1, (Box2D) c2);
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

    // FIXME
    public static CollisionManifold findCollisionFeatures(Circle a, Box2D b) {
        CollisionManifold result = new CollisionManifold();
        if (!circleAndBox2D(a, b))
            return result;

        Vector2f min = new Vector2f(b.getHalfSize()).negate();
        Vector2f max = new Vector2f(b.getHalfSize());

        Vector2f center = new Vector2f(a.getCenter());
        center.sub(b.getPosition());

        /* PUSH ROTATION */
        rotate(b.getPosition(), center, -b.getRotation());

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

        /* POP ROTATION */
        rotate(new Vector2f(), closestPointToCircle, b.getRotation());
        closestPointToCircle.add(b.getPosition());

        Vector2f normal = new Vector2f(closestPointToCircle).sub(a.getCenter()).normalize();

        Vector2f rotatedPoint = new Vector2f(normal).add(a.getCenter()).mul(a.getRadius());

        Rigidbody2D rba = a.getRigidbody();
        Rigidbody2D rbb = b.getRigidbody();

        float penetrationConst = (float) normalcdf(
                rba.getMass() * rba.getVelocity().dot(normal) -
                        rbb.getMass() * rbb.getVelocity().dot(normal),
                false
        );

        float depth = rotatedPoint.distance(closestPointToCircle) * penetrationConst;

        float distanceToPoint = a.getRadius() - depth;
        Vector2f contactPoint = new Vector2f(a.getCenter())
                .add(new Vector2f(normal).mul(distanceToPoint));

        result = new CollisionManifold(normal, depth);
        result.addContactPoint(contactPoint);
        return result;
    }

    private static final int FACE_1_X = 0;
    private static final int FACE_1_Y = 1;
    private static final int FACE_2_X = 2;
    private static final int FACE_2_Y = 3;

    private static final int NO_EDGE = -1;
    private static final int EDGE0 = 0;
    private static final int EDGE1 = 1;
    private static final int EDGE2 = 2;
    private static final int EDGE3 = 3;

    // FIXME
    public static CollisionManifold findCollisionFeatures(Box2D b1, Box2D b2) {
        CollisionManifold result = new CollisionManifold();
        if (!box2DAndBox2D(b1, b2))
            return result;

        /* DO FANCY LINEAR ALGEBRA STUFF I DON'T FULLY UNDERSTAND */
        Vector2f h1 = b1.getHalfSize();
        Vector2f h2 = b2.getHalfSize();

        Vector2f pos1 = b1.getPosition();
        Vector2f pos2 = b2.getPosition();

        Matrix2f rotB1 = new Matrix2f().rotation(b1.getRotation());
        Matrix2f rotB2 = new Matrix2f().rotation(b2.getRotation());
        Matrix2f rotB1T = new Matrix2f().rotation(b2.getRotation()).transpose();
        Matrix2f rotB2T = new Matrix2f().rotation(b2.getRotation()).transpose();

        Vector2f dp = new Vector2f(pos2).sub(pos1);
        Vector2f d1 = dp.mul(rotB1T);
        Vector2f d2 = dp.mul(rotB2T);

        Matrix2f c = new Matrix2f(rotB1T).mul(rotB2);
        Matrix2f absC = new Matrix2f(abs(c.m00), abs(c.m01), abs(c.m10), abs(c.m11));
        Matrix2f absCT = new Matrix2f(absC).transpose();

        Vector2f faceB1 = new Vector2f(d1).absolute().sub(h1).sub(new Vector2f(h2).mul(absC));
        if (faceB1.x > 0.0f || faceB1.y > 0.0f)
            return result;

        Vector2f faceB2 = new Vector2f(d2).absolute().sub(new Vector2f(h1).mul(absCT)).sub(h2);
        if (faceB2.x > 0.0f || faceB2.y > 0.0f)
            return result;

        /* FIND SEPARATION DIRECTION AND MAGNITUDE; THIS IS THE SEPARATING AXIS */
        int axis = FACE_1_X;
        float separation = faceB1.x;
        Vector2f normal = d1.x > 0.0f ? rotB1.getColumn(0, new Vector2f()) : rotB1.getColumn(0, new Vector2f()).negate();

        final float relativeTol = 0.95f;
        final float absoluteTol = 0.01f;

        if (faceB1.y > relativeTol * separation + absoluteTol * h1.y) {
            axis = FACE_1_Y;
            separation = faceB1.y;
            normal = d1.y > 0.0f ? rotB1.getColumn(1, new Vector2f()) : rotB1.getColumn(1, new Vector2f()).negate();
        }

        if (faceB2.x > relativeTol * separation + absoluteTol * h2.x) {
            axis = FACE_2_X;
            separation = faceB2.x;
            normal = d2.x > 0.0f ? rotB2.getColumn(0, new Vector2f()) : rotB2.getColumn(0, new Vector2f()).negate();
        }

        if (faceB2.y > relativeTol * separation + absoluteTol * h2.y) {
            axis = FACE_2_Y;
            separation = faceB2.y;
            normal = d2.y > 0.0f ? rotB1.getColumn(1, new Vector2f()) : rotB1.getColumn(1, new Vector2f()).negate();
        }

        /* SETUP CLIPPING PLANE */

        Vector2f frontNormal = new Vector2f(), sideNormal = new Vector2f();
        ClipVertex[] incidentEdge = {new ClipVertex(), new ClipVertex()};
        float front = 0.0f, negSide = 0.0f, posSide = 0.0f;
        float negEdge = 0.0f, posEdge = 0.0f;

        switch (axis) {
            case FACE_1_X: {
                frontNormal = normal;
                front = pos1.dot(frontNormal) + h1.x;
                sideNormal = rotB1.getColumn(1, new Vector2f());
                float side = pos1.dot(sideNormal);
                negSide = -side + h1.y;
                posSide = side + h1.y;
                negEdge = EDGE2;
                posEdge = EDGE0;
                computeIncidentEdge(incidentEdge, h2, pos2, rotB2, frontNormal);
            } break;

            case FACE_1_Y: {
                frontNormal = normal;
                front = pos1.dot(frontNormal) + h1.y;
                sideNormal = rotB1.getColumn(0, new Vector2f());
                float side = pos1.dot(sideNormal);
                negSide = -side + h1.x;
                posSide = side + h1.x;
                negEdge = EDGE1;
                posEdge = EDGE3;
                computeIncidentEdge(incidentEdge, h2, pos2, rotB2, frontNormal);
            } break;

            case FACE_2_X: {
                frontNormal = new Vector2f(normal).negate();
                front = pos2.dot(frontNormal) + h2.x;
                sideNormal = rotB2.getColumn(1, new Vector2f());
                float side = pos1.dot(sideNormal);
                negSide = -side + h2.y;
                posSide = side + h2.y;
                negEdge = EDGE2;
                posEdge = EDGE0;
                computeIncidentEdge(incidentEdge, h1, pos1, rotB1, frontNormal);
            } break;

            case FACE_2_Y: {
                frontNormal = new Vector2f(normal).negate();
                front = pos1.dot(frontNormal) + h2.y;
                sideNormal = rotB2.getColumn(0, new Vector2f());
                float side = pos2.dot(sideNormal);
                negSide = -side + h2.x;
                posSide = side + h2.x;
                negEdge = EDGE1;
                posEdge = EDGE3;
                computeIncidentEdge(incidentEdge, h1, pos1, rotB1, frontNormal);
            } break;
        }

        ClipVertex[] clipPoints1 = {new ClipVertex(), new ClipVertex()};
        ClipVertex[] clipPoints2 = {new ClipVertex(), new ClipVertex()};
        int np;

        np = clipSegmentToLine(clipPoints1, incidentEdge, sideNormal.negate(), negSide, (int) negEdge);

        if (np < 2)
            return result;

        np = clipSegmentToLine(clipPoints2, clipPoints1, sideNormal, posSide, (int) posEdge);

        if (np < 2)
            return result;

        // Now clipPoints2 contains the clipping points.

        float s = 0.0f;
        for (int i = 0; i < 2; ++i) {
            s = frontNormal.dot(clipPoints2[i].v) - front;

            if (s <= 0) {
                result.addContactPoint(clipPoints2[i].v);
            }
        }

        result = new CollisionManifold(frontNormal.normalize(), s);
        return result;
    }

    private static class ClipVertex {
        Vector2f v = new Vector2f();
        int inEdge1, outEdge1, inEdge2, outEdge2;
        int value;
    }

    private static void computeIncidentEdge(ClipVertex[] c, Vector2f v, Vector2f pos, Matrix2f rot, Vector2f normal) {
        Matrix2f rotT = new Matrix2f(rot).transpose();
        Vector2f n = new Vector2f(normal).mul(rotT);
        Vector2f nAbs = n.absolute();

        if (nAbs.x > nAbs.y) {
            if (sign(n.x) > 0.0f) {
                c[0].v.set(v.x, -v.y);
                c[0].inEdge2 = EDGE2;
                c[0].outEdge2 = EDGE3;

                c[1].v.set(v.x, v.y);
                c[1].inEdge2 = EDGE3;
                c[1].outEdge2 = EDGE0;
            } else {
                c[0].v.set(-v.x, -v.y);
                c[0].inEdge2 = EDGE0;
                c[0].outEdge2 = EDGE1;

                c[1].v.set(v.x, v.y);
                c[1].inEdge2 = EDGE1;
                c[1].outEdge2 = EDGE2;
            }
        } else {
            if (sign(n.y) > 0.0f) {
                c[0].v.set(v.x, v.y);
                c[0].inEdge2 = EDGE3;
                c[0].outEdge2 = EDGE0;

                c[1].v.set(-v.x, v.y);
                c[1].inEdge2 = EDGE0;
                c[1].outEdge2 = EDGE1;
            } else {
                c[0].v.set(-v.x, -v.y);
                c[0].inEdge2 = EDGE1;
                c[0].outEdge2 = EDGE2;

                c[1].v.set(v.x, -v.y);
                c[1].inEdge2 = EDGE2;
                c[1].outEdge2 = EDGE3;
            }
        }

        c[0].v = new Vector2f(pos).add(c[0].v.mul(rot));
        c[1].v = new Vector2f(pos).add(c[1].v.mul(rot));
    }

    private static int clipSegmentToLine(ClipVertex[] vOut, ClipVertex[] vIn, Vector2f normal, float offset, int clipEdge) {
        int numOut = 0;
        float distance0 = normal.dot(vIn[0].v) - offset;
        float distance1 = normal.dot(vIn[1].v) - offset;

        if (distance0 <= 0.0f)
            vOut[numOut++] = vIn[0];
        if (distance1 <= 0.0f)
            vOut[numOut++] = vIn[1];

        if (distance0 * distance1 < 0.0f) {
            float interp = distance0 / (distance0 - distance1);
            Vector2f t = new Vector2f(vIn[1].v).sub(vIn[0].v);
            vOut[numOut].v = new Vector2f(vIn[0].v).add(t.mul(interp));
            if (distance0 > 0.0f) {
                vOut[numOut].inEdge1 = clipEdge;
                vOut[numOut].inEdge2 = NO_EDGE;
                vOut[numOut].outEdge1 = vIn[0].outEdge1;
                vOut[numOut].outEdge2 = vIn[0].outEdge2;
                vOut[numOut].value = vIn[0].value;
            } else {
                vOut[numOut].inEdge1 = vIn[1].inEdge1;
                vOut[numOut].inEdge2 = vIn[1].inEdge2;
                vOut[numOut].outEdge1 = clipEdge;
                vOut[numOut].outEdge2 = NO_EDGE;
                vOut[numOut].value = vIn[1].value;
            }
            ++numOut;
        }

        return numOut;
    }
}
