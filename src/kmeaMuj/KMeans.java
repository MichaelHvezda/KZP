package kmeaMuj;


import transforms.Point3D;

import java.awt.*;
import java.util.ArrayList;
//inspirace z  http://moderntone.blogspot.com/2013/04/a-java-implementation-of-k-means.html
public class KMeans {

    public final ArrayList<Point3D> allPoints;
    public final int k;
    private Clasters pointClasters;




    public KMeans(ArrayList<Point3D> points, int k) {
        if (k < 2){
            k=2;
        }
        this.k = k;
        this.allPoints = points;
    }

    public ArrayList<Claster> getClasters() {
        if (pointClasters == null) {
            getInicialPoints();
            inicialClasters();
            stableClasters();
        }
        return pointClasters;
    }

    private ArrayList<Point3D> getFirstPoints(){
        ArrayList<Point3D> vysledek = new ArrayList<>();
        for (int i=0;i<k;i++){
            vysledek.add(allPoints.get(i));
        }
        return vysledek;
    }

    private void getInicialPoints(){
        pointClasters = new Clasters(allPoints);
        ArrayList<Point3D> randomPoint = getFirstPoints();
        for (int i = 0; i < k; i++){
            randomPoint.get(i).setIndex(i);

            pointClasters.add(new Claster(randomPoint.get(i)));
        }
    }

    private void inicialClasters(){
       pointClasters.assignPoints();
    }

    private void stableClasters(){
       boolean change = pointClasters.updateClasters();
       while (change){
         change = pointClasters.updateClasters();
       }
    }



}
