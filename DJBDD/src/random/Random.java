package random;

/**
 * Wrapper to the random methods of java.util.Random.
 * @author diegoj
 */
public class Random {
    
    public final int seed;
    public final java.util.Random generator;
    private static Random RANDOM_SINGLETON = null;
    
    private Random(int seed){
        this.seed = seed;
        this.generator = new java.util.Random(this.seed);
    }
    
    public static void init(int seed){
        if(RANDOM_SINGLETON == null){
            RANDOM_SINGLETON = new Random(seed);
        }
        else{
            System.err.println("Random.init has been called twice");
        }
    }
    
    public static int randInt(int min, int max){
        return min + (int)(RANDOM_SINGLETON.generator.nextDouble() * ((max - min) + 1));
    }
    
    public static double rand(){
        return RANDOM_SINGLETON.generator.nextDouble();
    }
    
    public static java.util.Random getRandom(){
        return RANDOM_SINGLETON.generator;
    }
    
}
