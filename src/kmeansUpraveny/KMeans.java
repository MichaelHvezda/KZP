package kmeansUpraveny;

import transforms.Point3D;

import java.util.ArrayList;

// prevzato a upraveno z https://www.dataonfocus.com/k-means-clustering-java-code/

public class KMeans {

    private ArrayList<Point3D> points;
    private ArrayList<Cluster> clusters;
    private int clustersCount;
    private boolean change =true;


    public KMeans(ArrayList<Point3D> listOfPoints,int numberOfClusters) {
        this.points = listOfPoints;
        this.clusters = new ArrayList<Cluster>();
        clustersCount = numberOfClusters;
        init();
        calculate();
    }

    //Initializes the process
    public void init() {

        //Set Centroids with
        for (int i = 0; i < clustersCount; i++) {
            Cluster cluster = new Cluster(i);
            Point3D centroid = points.get(i);
            cluster.setCentroid(centroid);
            clusters.add(cluster);
        }
    }

    private void plotClusters() {
        for (int i = 0; i < clustersCount; i++) {
            Cluster c = clusters.get(i);
            c.plotCluster();
        }
    }

    //The process to calculate the K Means, with iterating method.
    public void calculate() {

        // Add in new data, one at a time, recalculating centroids with each new one.
        while(change) {
            //Clear cluster state
            clearClusters();


            //Assign points to the closer cluster
            assignCluster();

            //Calculate new centroids.
            calculateCentroids();
        }
    }

    private void clearClusters() {
        for(Cluster cluster : clusters) {
            cluster.clear();
        }
    }

    //prirazeni bodu do skupin
    private void assignCluster() {
        change=false;

        for(Point3D point : points) {
            double min = Double.MAX_VALUE;
            int cluster = -1;
            for(int i = 0; i < clustersCount; i++) {
                Cluster c = clusters.get(i);
                double distance = point.getSquareOfDistanceUpdated(c.getCentroid());
                    if(distance < min){
                    min = distance;
                    cluster = i;
                }
            }
            if(point.getIndex()!=cluster){
                change=true;
            }
            point.setIndex(cluster);
            clusters.get(cluster).addPoint(point);
        }
    }

    //vypocitani nove centroidy
    private void calculateCentroids() {
        for(Cluster cluster : clusters) {
            double sumX = 0;
            double sumY = 0;
            double sumZ = 0;
            ArrayList<Point3D> list = cluster.getPoints();
            int size = list.size();

            for(Point3D point : list) {
                if(Math.abs(point.getX()-cluster.centroid.getX())>(360/clustersCount)){
                    sumX += (point.getX()-360);
                    sumY += point.getY();
                    sumZ += point.getZ();
                }else {
                    sumX += point.getX();
                    sumY += point.getY();
                    sumZ += point.getZ();
                }

            }

            if(!(size==0)){
                clusters.get(cluster.getId()).setCentroid(new Point3D(((sumX / size)+360)%360,sumY / size,sumZ / size));
            }

        }
    }

    public ArrayList<Point3D> getPoints() {
        return points;
    }

    public void setPoints(ArrayList<Point3D> points) {
        this.points = points;
    }

    public ArrayList<Cluster> getClusters() {
        return clusters;
    }

    public void setClusters(ArrayList<Cluster> clusters) {
        this.clusters = clusters;
    }

    public int getClustersCount() {
        return clustersCount;
    }

    public void setClustersCount(int clustersCount) {
        this.clustersCount = clustersCount;
    }
}
