
package com.learnopengles.android.object;

import java.util.Arrays;


public class Triangle {
    private final Vec3d[] vertices;
    private final Vec3d normal;

    public Triangle(Vec3d v1, Vec3d v2, Vec3d v3){
        vertices = new Vec3d[3];
        vertices[0] = v1;
        vertices[1] = v2;
        vertices[2] = v3;
        Vec3d edge1 = v2.sub(v1);
        Vec3d edge2 = v3.sub(v1);
        normal = Vec3d.cross(edge1, edge2).normalize();
    }

    public void translate(Vec3d translation){
        for(int i = 0; i < vertices.length; i++){
            vertices[i] = vertices[i].add(translation);
        }
    }

    @Override public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("Triangle[");
        for(Vec3d v : vertices){
            sb.append(v.toString());
        }
        sb.append("]");
        return sb.toString();
    }

    public Vec3d[] getVertices(){
        return vertices;
    }

    public Vec3d getNormal(){
        return normal;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Triangle other = (Triangle) obj;
        if (!Arrays.deepEquals(this.vertices, other.vertices)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + Arrays.deepHashCode(this.vertices);
        return hash;
    }
}
