package kmeans;

import transforms.Point3D;

import java.io.*;
import java.util.*;
//vzato z  http://moderntone.blogspot.com/2013/04/a-java-implementation-of-k-means.html
public class KMeans {

    private static final Random random = new Random();
    public final ArrayList<Point3D> allPoints;
    public final int k;
    private Clusters pointClusters; //the k Clusters




    public KMeans(ArrayList<Point3D> points, int k) {
        if (k < 2)
            new Exception("The value of k should be 2 or more.").printStackTrace();
        this.k = k;

        this.allPoints = (points);
    }



    /**step 1: get random seeds as initial centroids of the k clusters
     */
    private void getInitialKRandomSeeds(){
        pointClusters = new Clusters(allPoints);
        List<Point3D> kRandomPoints = getKRandomPoints();
        for (int i = 0; i < k; i++){
            kRandomPoints.get(i).setIndex(i);
            pointClusters.add(new Cluster(kRandomPoints.get(i)));
        }
    }

    private ArrayList<Point3D> getKRandomPoints() {
        ArrayList<Point3D> kRandomPoints = new ArrayList<>();
        boolean[] alreadyChosen = new boolean[allPoints.size()];
        int size = allPoints.size();
        for (int i = 0; i < k; i++) {
            int index = -1, r = random.nextInt(size--) + 1;
            for (int j = 0; j < r; j++) {
                index++;
                while (alreadyChosen[index])
                    index++;
            }
            kRandomPoints.add(allPoints.get(index));
            alreadyChosen[index] = true;
        }
        return kRandomPoints;
    }

    /**step 2: assign points to initial Clusters
     */
    private void getInitialClusters(){
        pointClusters.assignPointsToClusters();
    }

    /** step 3: update the k Clusters until no changes in their members occur
     */
    private void updateClustersUntilNoChange(){
        boolean isChanged = pointClusters.updateClusters();
        while (isChanged)
            isChanged = pointClusters.updateClusters();
    }

    /**do K-means clustering with this method
     */
    public ArrayList<Cluster> getPointsClusters() {
        if (pointClusters == null) {
            getInitialKRandomSeeds();
            getInitialClusters();
            updateClustersUntilNoChange();
        }
        return pointClusters;
    }


}