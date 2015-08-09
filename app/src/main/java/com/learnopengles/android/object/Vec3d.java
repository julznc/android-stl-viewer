
package com.learnopengles.android.object;

public final class Vec3d {
    public final double x;
    public final double y;
    public final double z;

    public Vec3d(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vec3d mul(double scale) {
        return new Vec3d(
                x * scale,
                y * scale,
                z * scale);
    }

    public static Vec3d sub(Vec3d t1, Vec3d t2) {
        return new Vec3d(
                t1.x - t2.x,
                t1.y - t2.y,
                t1.z - t2.z
        );
    }

    public Vec3d sub(Vec3d t1) {
        return sub(this,t1);
    }

    public static Vec3d add(Vec3d t1, Vec3d t2) {
        return new Vec3d(
                t1.x + t2.x,
                t1.y + t2.y,
                t1.z + t2.z
        );
    }

    public Vec3d add(Vec3d t1) {
        return add(this,t1);
    }

    public double length() {
        return Math.sqrt(this.x*this.x + this.y*this.y + this.z*this.z);
    }

    public Vec3d normalize() {
        double norm = 1.0 / length();
        return new Vec3d(
                this.x * norm,
                this.y * norm,
                this.z * norm
        );
    }

    public static Vec3d cross(Vec3d v1, Vec3d v2) {
        double tmpX;
        double tmpY;

        tmpX = v1.y * v2.z - v1.z * v2.y;
        tmpY = v2.x * v1.z - v2.z * v1.x;
        return new Vec3d(
                v1.x * v2.y - v1.y * v2.x,
                tmpX,
                tmpY
        );
    }

    public double dot(Vec3d v1) {
        return this.x * v1.x + this.y * v1.y + this.z * v1.z;
    }

    @Override
    public int hashCode() {
        long bits = 7L;
        bits = 31L * bits + Double.doubleToLongBits(x);
        bits = 31L * bits + Double.doubleToLongBits(y);
        bits = 31L * bits + Double.doubleToLongBits(z);
        return (int) (bits ^ (bits >> 32));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof Vec3d) {
            Vec3d v = (Vec3d) obj;
            return (x == v.x) && (y == v.y) && (z == v.z);
        }
        return false;
    }

    @Override
    public String toString() {
        return "Vec3d[" + x + ", " + y + ", " + z + "]";
    }

    public static double getAngle(Vec3d a, Vec3d b){
        double AdotB = a.dot(b);
        double A = a.length();
        double B = b.length();
        return Math.acos(AdotB / (A * B));
    }
}
