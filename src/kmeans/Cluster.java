
package kmeans;
import transforms.Point3D;

import java.util.*;

public class Cluster {

    private final ArrayList<Point3D> points;
    private Point3D centroid;

    public Cluster(Point3D firstPoint) {
        points = new ArrayList<Point3D>();
        centroid = firstPoint;
    }

    public Point3D getCentroid(){
        return centroid;
    }

    public void updateCentroid(){
        double newx = 0d, newy = 0d, newz = 0d;
        for (Point3D point : points){
            newx += point.getX(); newy += point.getY(); newz += point.getZ();
        }
        centroid = new Point3D(newx / points.size(), newy / points.size(), newz / points.size());
    }

    public ArrayList<Point3D> getPoints() {
        return points;
    }

    public String toString(){
        StringBuilder builder = new StringBuilder(getCentroid() +" This cluster contains the following points:\n");
//        for (Point3D point : points)
//            builder.append(point.toString() + ",\n");
        return builder.deleteCharAt(builder.length() - 2).toString();
    }
}
