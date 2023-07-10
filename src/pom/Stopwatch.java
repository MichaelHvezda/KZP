package pom;

public class Stopwatch {
    private double start = 0;
    private double average = 0;
    private int count = 0;


    public Stopwatch() {
        this.start = 0;
    }

    public void startTime(){
        this.start=System.currentTimeMillis();
    }

    public void betweenTime(){
        System.out.println("StopWatch: " + (System.currentTimeMillis() - start));
    }
    public double betweenTimeRet(){
        return  System.currentTimeMillis() - start;
    }
    public void endTime(){
        double pom = System.currentTimeMillis() - start;
        start = 0;
        System.out.println("StopWatch: " + pom);
    }

    public void resetTime(){
        double pom = System.currentTimeMillis() - start;
        start = System.currentTimeMillis();
        System.out.println("StopWatch: " + pom);
    }

    public void resetTimeAvr(){
        double pom = System.currentTimeMillis() - start;
        count++;
        average += pom;
        start = System.currentTimeMillis();
        System.out.println("StopWatch: " + pom + " Avarage: " + (average/count) + " Count: " + count);
    }

    public void resetAvr(){
        start = 0;
        average = 0;
        count = 0;
    }

    //public double getStart() {
    //    return start;
    //}
//
    //public void setStart(double start) {
    //    this.start = start;
    //}


}
