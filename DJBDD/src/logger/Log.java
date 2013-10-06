/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package logger;

/**
 *
 * @author diegoj
 */
public class Log {
    
    public static boolean ACTIVATED = false;
    
    public static void print(String str){
        if(ACTIVATED){
            System.out.print(str);
        }
    }
    
    public static void print(boolean activated, String str){
        if(activated && ACTIVATED){
            System.out.print(str);
        }
    }
    
    public static void println(String str){
        if(ACTIVATED){
            System.out.println(str);
        }
    }
    
    public static void println(boolean activated, String str){
        if(activated && ACTIVATED){
            System.out.println(str);
        }
    }
    
    public static void println(boolean activated, boolean condition, String str){
        if(activated && ACTIVATED && condition){
            System.out.println(str);
        }
    }    
    
}
