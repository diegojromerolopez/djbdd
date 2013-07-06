package djbdd;

/**
 *
 * @author diegoj
 */
public class TimeMeasurer {
    public final static boolean MEASURE_TIME = true;
    public String context = "";
    public long startTime = 0;
    public  long endTime = 0;
    public  long elapsedTime = 0;
    
        /**
     * Converts the elapsed time to a human readable format.
     * @return String Time elapsed in human-friendly form.
     */
    protected String getElapsedTimeAsHumanText(){
        long ns = this.elapsedTime;
        long us = this.elapsedTime / 1000;
        ns = elapsedTime % 1000;
        long ms = us / 1000;
        us = us % 1000;
        long s = ms / 1000;
        ms = ms % 1000;
        long m = s / 60;
        s = s % 60;
        long h = m / 60;
        m = m % 60;
        return h+" h, "+m+" m, "+s+" s, "+ms+" ms, "+us+"µs";
    } 
    
    
    public TimeMeasurer(String context){
        this.context = context;
        if(MEASURE_TIME)
            this.startTime = System.nanoTime();
    }
    
    public long end(){
        if(MEASURE_TIME){
            this.endTime = System.nanoTime();
            this.elapsedTime = this.endTime - this.startTime;
        }
        return this.elapsedTime;
    }
    
    public void show(){
        if(MEASURE_TIME)
            System.out.println(context+" "+this.getElapsedTimeAsHumanText());
    }
}
