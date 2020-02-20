package klicovani3;

public class VysecKalkulator {
    private float min;
    private float max;

    public void vlozHodnotu(float a){

        if(max==0 && min ==0){
            min=a;
            max=a;
        }else {
            if(!(min<=a && a<=max)){
                System.out.println(a+" aaaa "+min +" " + max);
                if (min>=a){
                    min=a;
                    System.out.println(a+" min");
                }

                if(max<=a){
                    max=a;
                    System.out.println(a+" max");
                }

            }
        }

    }

    public float vratOtoceni(){
        return ((min+max)/2);
    }

    public float vratVysec(){
        if(min==0 && 0==max){
            return 0;
        }else {
            return (float) (max-vratOtoceni()+Math.PI/100);
        }

    }
    public float vratPrebarveni(){
        if(min==0 && 0==max){
            return 0;
        }else {
            return (float) (max-vratOtoceni()+Math.PI/5);
        }

    }

    public VysecKalkulator() {
        this.min = 0;
        this.max = 0;
    }

    public VysecKalkulator(float min, float max) {
        this.min = min;
        this.max = max;
    }

    public float getMin() {
        return min;
    }

    public void setMin(float min) {
        this.min = min;
    }

    public float getMax() {
        return max;
    }

    public void setMax(float max) {
        this.max = max;
    }
}
