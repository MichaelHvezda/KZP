package kmeansNeupraveny;

import transforms.Point3D;

import java.util.ArrayList;


public class Cluster {

    public ArrayList<Point3D> points;
    public Point3D centroid;
    public int id;

    //Creates a new Cluster
    public Cluster(int id) {
        this.id = id;
        this.points = new ArrayList<Point3D>();
        this.centroid = new Point3D();
    }

    public ArrayList<Point3D> getPoints() {
        return points;
    }

    public void addPoint(Point3D point) {
        points.add(point);
    }

    public void setPoints(ArrayList<Point3D> points) {
        this.points = points;
    }

    public Point3D getCentroid() {
        return centroid;
    }

    public void setCentroid(Point3D centroid) {
        this.centroid = centroid;
    }

    public int getId() {
        return id;
    }

    public void clear() {
        points.clear();
    }

    public void plotCluster() {
        System.out.println("[Cluster: " + id+"]");
        System.out.println("[Centroid: " + centroid + "]");
        System.out.println("[Points: " + points.size());
    }

}
