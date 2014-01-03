/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package logger;

/**
 * Debug message log.
 * @author diegoj
 */
public class Log {
    
    /**
     * Activate / Deactivate the log print
     */
    public static final boolean ACTIVATED = true;
    
    public static void print(String str){
        if(ACTIVATED){
            System.out.print(str);
            System.out.flush();
        }
    }
    
    public static void print(boolean activated, String str){
        if(activated && ACTIVATED){
            System.out.print(str);
            System.out.flush();
        }
    }
    
    public static void println(String str){
        if(ACTIVATED){
            System.out.println(str);
            System.out.flush();
        }
    }
    
    public static void println(boolean activated, String str){
        if(activated && ACTIVATED){
            System.out.println(str);
            System.out.flush();
        }
    }
    
    public static void println(boolean activated, boolean condition, String str){
        if(activated && ACTIVATED && condition){
            System.out.println(str);
            System.out.flush();
        }
    }    
    
}
