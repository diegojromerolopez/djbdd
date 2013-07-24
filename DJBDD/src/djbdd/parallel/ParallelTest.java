/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package djbdd.parallel;

import djbdd.parallel.*;
import java.util.concurrent.*;
import java.util.*;

/**
 *
 * @author diegoj
 */
public class ParallelTest {

    int k;

    public ParallelTest() {
        k = 0;
        Parallel.For(0, 10, new LoopBody<Integer>() {

            public void run(Integer i) {
                k += i;
                System.out.println(i);
            }
        });
        System.out.println("Sum = " + k);
    }

    public static void main(String[] argv) {
        ParallelTest test = new ParallelTest();
    }
}