package kmeansUpraveny;

import transforms.Point3D;

import java.util.ArrayList;


public class KMeans {

    //Number of Clusters. This metric should be related to the number of points
   //private int NUM_CLUSTERS = 3;
   ////Number of Points
   //private int NUM_POINTS = 15;
   ////Min and Max X and Y
   //private static final int MIN_COORDINATE = 0;
   //private static final int MAX_COORDINATE = 10;

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

  // public static void main(String[] args) {

  //     KMeans kmeans = new KMeans();
  //     kmeans.init();
  //     kmeans.calculate();
  // }

    //Initializes the process
    public void init() {
        //Create Points


        //Create Clusters
        //Set Centroids with
        for (int i = 0; i < clustersCount; i++) {
            Cluster cluster = new Cluster(i);
            Point3D centroid = points.get(i);
            //System.out.println("sjadodhaifj " + centroid);
            cluster.setCentroid(centroid);
            clusters.add(cluster);
        }

        //Print Initial state
        //plotClusters();
    }

    private void plotClusters() {
        for (int i = 0; i < clustersCount; i++) {
            Cluster c = clusters.get(i);
            c.plotCluster();
        }
    }

    //The process to calculate the K Means, with iterating method.
    public void calculate() {
        //boolean finish = false;
        //int iteration = 0;

        // Add in new data, one at a time, recalculating centroids with each new one.
        while(change) {
            //Clear cluster state
           // change =false;
            clearClusters();

            //ArrayList<Point3D> lastCentroids = getCentroids();

            //Assign points to the closer cluster
            assignCluster();

            //Calculate new centroids.
            calculateCentroids();

           // iteration++;

            //ArrayList<Point3D> currentCentroids = getCentroids();

            //Calculates total distance between new and old Centroids
            //double distance = 0;
            //for(int i = 0; i < lastCentroids.size(); i++) {
            //    distance += Point3D.distance(lastCentroids.get(i),currentCentroids.get(i));
            //}
            //System.out.println("#################");
            //System.out.println("Iteration: " + iteration);
            //System.out.println("Centroid distances: " + distance);
            //plotClusters();

        }
    }

    private void clearClusters() {
        for(Cluster cluster : clusters) {
            cluster.clear();
        }
    }

    private ArrayList<Point3D> getCentroids() {
        ArrayList<Point3D> centroids = new ArrayList<Point3D>(clustersCount);
        for(Cluster cluster : clusters) {
            Point3D aux = cluster.getCentroid();
            Point3D point = new Point3D(aux.getX(),aux.getY(),aux.getZ());
            centroids.add(point);
        }
        return centroids;
    }

    private void assignCluster() {
        change=false;

        for(Point3D point : points) {
            double min = Double.MAX_VALUE;
            int cluster = -1;
            for(int i = 0; i < clustersCount; i++) {
                Cluster c = clusters.get(i);
                double distance = point.getSquareOfDistanceUpdated(c.getCentroid());
                //System.out.println("dist " + distance + " min " + min);
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

    private void calculateCentroids() {
        for(Cluster cluster : clusters) {
            double sumX = 0;
            double sumY = 0;
            double sumZ = 0;
            ArrayList<Point3D> list = cluster.getPoints();
            //System.out.println("id " + cluster.getId() + " size " + list.size());
            int size = list.size();

            for(Point3D point : list) {
                if(Math.abs(point.getX()-cluster.centroid.getX())>180){
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
