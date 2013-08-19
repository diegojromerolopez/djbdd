package djbdd;

/**
 * Thread that collects the garbage vertices with some frequency.
 * Use:
 * {@code
 *       GCThread gcCollector = new GCThread();
 *       Thread gcThread = new Thread(gcCollector);
 *       gcThread.start();
 * }
 * 
 * @author diegoj
 */
public class GCThread implements Runnable {
    
    /** Frequency of garbage collection execution */
    private int frequency = 1000;
    
    /** Should this garbage collection process stop? */
    private boolean stop = false;
    
    /**
     * Constructor of the garbage collection thread.
     */
    public GCThread(){
        this.frequency = 1000;
        this.stop = false;
    }
    
    /**
     * Stops the process.
     */
    public void stop(){
        this.stop = true;
    }
    
    /**
     * Run the process of garbage collection.
     */
    @Override
    public void run(){
        try{
            while(!this.stop){
                Thread.sleep(this.frequency);
                BDD.T.gc();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
