package asserter.examples.elevator;

import asserter.core.*;

/**
 * One example, an elevator in a two-floors building.
 * Floor:  ground / first floor
 * Going down/up: going_down / going_up
 * @author diegoj
 */
public class Elevator {
    /** Ground floor value */
    private final String GROUND_FLOOR = "ground";
    
    /** First floor value */
    private final String FIRST_FLOOR = "first_floor";
    
    /** Status stopped value */
    private final String STATUS_STOPPED = "stopped";
    
    /** Status going up value */
    private final String STATUS_GOING_UP = "going_up";
    
    /** Status going down value */
    private final String STATUS_GOING_DOWN = "going_down";
    
    /** Asseter that will be used to test if the  */
    private Asserter asserter;
    
    /** Current floor of the elevator */
    private String floor;
    
    /**
     * Status or action that will be executed by the elevator
     * according to the orders of a human
     */
    private String status;

    /* Dummy call, travelling time between floors  */
    private void travel(){
        try {
            Thread.sleep(1000);
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }
    
    /* Dummy call, travelling time between floors  */
    private void travelDown(){ this.travel(); }
    private void travelUp(){ this.travel(); }

    /* Implementation of the variables as methods */
    public boolean isGround(){ return floor.equals(GROUND_FLOOR); }
    public boolean isFirstFloor(){ return !floor.equals(GROUND_FLOOR); }
    public boolean isStopped(){ return status.equals(STATUS_STOPPED); }
    public boolean isGoingUp(){ return status.equals(STATUS_GOING_UP); }
    public boolean isGoingDown(){ return status.equals(STATUS_GOING_DOWN); }
    
    /**
     * Creation of an elevator.
     * Asumes the elevator is in the ground floor and is stopped.
     */
    public Elevator(){
        this.floor = GROUND_FLOOR;
        this.status = STATUS_STOPPED;
        /* Build the asserter with the logic formula */
        this.asserter = new Asserter("((isGoingUp && isGround) || (isGoingDown && isFirstFloor) || (isGround && isStopped) || (isFirstFloor && isStopped)) && ((isGround && !isFirstFloor) || (!isGround && isFirstFloor))", this);
    }
    
    /**
     * Go down.
     */
    public void down(){
        // Test if status==stopped
        // Test if floor=="first"
        this.status = STATUS_GOING_DOWN;
        
        // Runs the assertion process
        if(!this.asserter.run()){
            System.out.println("ERROR. You can't go down");
            return;
        }
        
        // If everything is ok, we can travel down and change the status
        // and the floor.
        this.travelDown();
        this.status = STATUS_STOPPED;
        this.floor = GROUND_FLOOR;
        System.out.println("The elevator is now in the ground floor, stopped.");
    }
    
    /**
     * Go up.
     */    
    public void up(){
        // Test if status==stopped
        // Test if floor=="ground"
        this.status = STATUS_GOING_UP;
        
        // Runs the assertion process
        if(!this.asserter.run()){
            System.out.println("ERROR. You can't go up");
            return;
        }
        
        // If everything is ok, we can travel up and change the status
        // and the floor.
        this.travelUp();
        this.status = STATUS_STOPPED;
        this.floor = FIRST_FLOOR;
        System.out.println("The elevator is now in the first floor, stopped.");
    }
    
    /**
     * Test main.
     */
    public static void main(String args[]){
    
        System.out.println("=================================================");
        System.out.println("=================================================");
        System.out.println("SIMULATION OF AN ELEVATOR");
        
        // Logical variables implemented as methods of Elevator
        String[] variables = {"isGround", "isFirstFloor", "isStopped", "isGoingUp", "isGoingDown"};
        
        // Init of the assertion process
        Asserter.init(variables);
        
        // New elevator
        Elevator elevator = new Elevator();
        
        // Start the elevator
        System.out.println("Elevator in "+elevator.floor+", "+elevator.status+". ");
        
        // This can't be accomplished because the elevator
        // is in the ground floor
        elevator.down();
            
        // The elevator goes up
        elevator.up();
            
        // The elevator can't go up because it is in the first floor
        elevator.up();
        
        System.out.println("=================================================");
        System.out.println("=================================================");
    }
    
}
