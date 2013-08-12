/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package djbdd;

/**
 *
 * @author diegoj
 */
public class GCThread implements Runnable {
    
    private int frequency = 1000;
    private boolean stop = false;
    
    public GCThread(){
        this.frequency = 1000;
        this.stop = false;
    }
    
    public void stop(){
        this.stop = true;
    }
    
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
