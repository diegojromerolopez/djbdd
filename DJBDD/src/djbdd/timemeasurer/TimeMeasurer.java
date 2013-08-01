package djbdd.timemeasurer;

/**
 *
 * @author diegoj
 */
public class TimeMeasurer {
    
    /** Switch to stop measuring times */
    public final static boolean MEASURE_TIME = true;
    
    /** Overload switch to measure time */
    public boolean force_measurement = false;
    
    /** Message to warn developers if MEASURE_TIME = false */
    public final static String DISABLED_MEASUREMENT_CONTEXT = "IT IS NOT MEASURING";
    
    /** Context of the time measurement: method name, class, etc. */
    public String context = "";
    
    /** Start time in nanoseconds */
    public long startTime = 0;
    
    /** End time in nanoseconds */
    public  long endTime = 0;
    
    /** Elapsed time in nanoseconds */
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
        this.context = DISABLED_MEASUREMENT_CONTEXT;
        this.force_measurement = false;
        if(MEASURE_TIME){
            this.startTime = System.nanoTime();
            this.context = context;
        }
    }
    
    public TimeMeasurer(String context, boolean force_measurement){
        this.context = DISABLED_MEASUREMENT_CONTEXT;
        this.force_measurement = force_measurement;
        if(MEASURE_TIME || this.force_measurement){
            this.startTime = System.nanoTime();
            this.context = context;
        }
    }
    
    public TimeMeasurer end(){
        if(MEASURE_TIME || this.force_measurement){
            this.endTime = System.nanoTime();
            this.elapsedTime = this.endTime - this.startTime;
        }
        return this;
    }
    
    public void show(){
        if(MEASURE_TIME || this.force_measurement)
            System.out.println(context+" "+this.getElapsedTimeAsHumanText());
    }
    
    public long getElapsedTime(){
        return this.elapsedTime;
    }
}
