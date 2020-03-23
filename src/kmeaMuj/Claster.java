package kmeaMuj;

import transforms.Point3D;

import java.util.ArrayList;

public class Claster {

    private final ArrayList<Point3D> points;
    private Point3D centroid;

    public Claster(Point3D firstPoint) {
        points = new ArrayList<Point3D>();
        centroid = firstPoint;
    }

    public void updateCentroid(){
        double pomX = 0;
        double pomY = 0;
        double pomZ = 0;
        //double pom = centroid.getX();
        for (Point3D point3D : points){

            if(Math.abs(point3D.getX()-centroid.getX())>180){
                pomX = pomX + (point3D.getX()-360);
                pomY += point3D.getY();
                pomZ += point3D.getZ();
            }else {
                pomX += point3D.getX();
                pomY += point3D.getY();
                pomZ += point3D.getZ();
            }


        }
        int size = points.size();
        centroid = new Point3D(((pomX / size)+360)%360,pomY / size,pomZ / size);
    }

    public ArrayList<Point3D> getPoints() {
        return points;
    }

    public Point3D getCentroid() {
        return centroid;
    }
}
