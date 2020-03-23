package kmeaMuj;

import transforms.Point3D;

import java.util.ArrayList;

public class Clasters extends ArrayList<Claster> {
    private ArrayList<Point3D> allPoints;
    private boolean changed;

    public Clasters(ArrayList<Point3D> allPoints){
        this.allPoints = allPoints;
    }

    public Integer getNearestCluster(Point3D point){
        double minSquare = Double.MAX_VALUE;
        int itsIndex = -1;
        for (int i = 0 ; i < size(); i++){
            double square = point.getSquareOfDistance(get(i).getCentroid());
            if (square < minSquare){
                minSquare = square;
                itsIndex = i;
            }
        }

        return itsIndex;
    }

    public boolean updateClasters(){
        for (Claster claster : this){
            claster.updateCentroid();
            claster.getPoints().clear();
        }
        changed = false;
        assignPoints();
        return changed;
    }

    public void assignPoints(){
        for (Point3D point : allPoints){
            int previousIndex = point.getIndex();
            int newIndex = getNearestCluster(point);
            if (previousIndex != newIndex)
                changed = true;
            Claster target = get(newIndex);
            point.setIndex(newIndex);
            target.getPoints().add(point);
        }
    }
}
