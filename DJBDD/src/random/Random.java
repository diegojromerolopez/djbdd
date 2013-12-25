/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package random;

/**
 *
 * @author diegoj
 */
public class Random {
    
    private static int RANDOM_SEED;
    private static java.util.Random generator;
    
    public static void initSeed(int seed){
        Random.RANDOM_SEED = seed;
        Random.generator = new java.util.Random(RANDOM_SEED);
    }
    
    public static int randInt(int min, int max){
        return min + (int)(Random.generator.nextDouble() * ((max - min) + 1));
    }
    
}
