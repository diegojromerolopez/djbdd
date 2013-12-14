package djbdd.core;

import djbdd.core.BDD;

/**
 * Thread that collects the garbage vertices with some frequency.
 * Use:
 * {@code
 *       GCThread gcCollector = new GCThread();
 *       gcCollector.start();
 * }
 * 
 * @author diegoj
 */
public class GCThread extends Thread {
    
    /** Frequency of garbage collection execution */
    private int frequency = 1000;
    
    /**
     * Constructor of the garbage collection thread.
     */
    public GCThread(){
        this.frequency = 1000;
    }
    
    /**
     * Constructor of the garbage collection thread.
     */
    public GCThread(int frequency){
        this.frequency = frequency;
    }
    
    /**
     * Run the process of garbage collection.
     */
    @Override
    public void run(){
        try{
            while(!Thread.currentThread().isInterrupted()){
                Thread.sleep(this.frequency);
                BDD.T.gc();
            }
        }catch(Exception e){
            e.printStackTrace();
            this.interrupt();
        }
    }
    
    /**
     * Ends the garbage collection.
     */
    public void end(){
        this.interrupt();
    }
}
