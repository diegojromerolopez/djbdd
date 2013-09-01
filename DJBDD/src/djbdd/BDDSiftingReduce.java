package djbdd;

import java.util.*;

/**
 * Reduction method by Sifting Algorithm ()
 * TODO: finish this method.
 * DO NOT USE, IT DOES NOT WORK!!!
 * @author diegoj
 */
public class BDDSiftingReduce {
    /*
    private static Vertex addWithoutRedundant(int var, Vertex low, Vertex high) {
        if (low.index == high.index) {
            return low;
        }
        return BDD.T.add(var, low, high);
    }

    private static boolean swapVertex(Vertex v, int varJ) {
        boolean swapWasMade = false;
        int varI = v.variable;

        Vertex low = v.low();
        Vertex high = v.high();
        //int varJ = varI+1;

        Vertex A = null;
        Vertex B = null;
        if (!low.isLeaf()) {
            A = low.low();
            B = low.high();
        } else {
            A = low;
            B = low;
        }

        Vertex C = null;
        Vertex D = null;
        if (!high.isLeaf()) {
            C = high.low();
            D = high.high();
        } else {
            C = high;
            D = high;
        }

        Vertex newLow = null;
        Vertex newHigh = null;

        // Case a:
        if (low != null && low.variable == varJ && (high == null || high.variable != varJ)) {
            //System.out.println("LOW "+low);
            //System.out.println("HIGH "+high);
            //System.out.println("A "+A);
            //System.out.println("B "+B);
            //System.out.println("C "+C);
            newLow = addWithoutRedundant(varI, A, C);
            newHigh = addWithoutRedundant(varI, B, C);
            BDD.T.setVertex(v, varJ, newLow, newHigh);
            swapWasMade = true;
        } // Case b:
        else if ((low == null || low.variable != varJ) && (high != null && high.variable == varJ)) {
            newLow = addWithoutRedundant(varI, A, B);
            newHigh = addWithoutRedundant(varI, A, C);
            BDD.T.setVertex(v, varJ, newLow, newHigh);
            swapWasMade = true;
        } // Case c:
        else if ((low != null && low.variable == varJ) && (high != null && high.variable == varJ)) {
            newLow = addWithoutRedundant(varI, A, C);
            newHigh = addWithoutRedundant(varI, B, D);
            BDD.T.setVertex(v, varJ, newLow, newHigh);
            swapWasMade = true;
        } // Case d:
        else if ((low == null || low.variable != varJ) && (high == null || high.variable != varJ)) {
            swapWasMade = false;
        } // Case e:
        else if ((low == null || low.variable != varJ) && high == null) {
            swapWasMade = false;
        }

        return swapWasMade;
    }

    public static void swap(int varI, int varJ) {
        //System.out.println("SWAPPING OF VERTICES WHOSE VAR IS "+varI+" ("+BDD.variables().get(varI)+") BY "+varJ+"("+BDD.variables().get(varJ)+")");
        ArrayList<Vertex> vertices = new ArrayList<Vertex>(BDD.T.getVerticesWhoseVariableIs(varI));
        for (int i = 0; i < vertices.size(); i++) {
            Vertex v = vertices.get(i);
            //System.out.println("Let's swap "+v);
            boolean swapWasMade = swapVertex(v, varJ);
            //System.out.println("Swap mas made? "+swapWasMade);
        }//
    }

    private static String printOrder(ArrayList<Integer> order) {
        String s = "";
        for (Integer i : order) {
            s += BDD.variables().get(i).replace("_", "") + ", ";
        }
        s = s.substring(0, s.length() - 2);
        System.out.println(s);
        return s;
    }
    */
    public static void siftOrder(BDD bdd) {
        /*
        int minSize = BDD.T.size();
        System.out.println("SIZE " + minSize);
        ArrayList<String> variables = BDD.variables().list();
        ArrayList<Integer> bestVariableOrder = new ArrayList<Integer>(variables.size());
        for (int i = 0; i < variables.size(); i++) {
            bestVariableOrder.add(i);
        }

        ArrayList<Integer> currentVariableOrder = new ArrayList<Integer>(variables.size());
        for (int i = 0; i < variables.size(); i++) {
            currentVariableOrder.add(i);
        }

        //printOrder(currentVariableOrder);
        int loopGlobalIndex = 0;
        // Finding the best reordering
        System.out.println("TO BOTTOM");
        for (int i = 0; i < variables.size(); i++) {
            System.out.println("\nLOOP " + i);

            // To bottom
            int varI = i;
            for (int j = i; j < variables.size() - 1; j++) {
                //System.out.println("Swap "+j+"->"+(j+1));
                String var = BDD.variables().get(varI);
                swap(varI, j + 1);
                //System.out.println("Size var list: "+this.V.get(varI).size());
                Collections.swap(currentVariableOrder, j, j + 1);
                String order = printOrder(currentVariableOrder);
                //Printer.printBDD(bdd, "bdd_index:" + loopGlobalIndex + "_swapedVar:" + varI + "(" + var + ")_order:" + order + "_size:" + bdd.size());
                int currentSize = BDD.T.size();
                //System.out.println("CURRENT "+currentSize);
                if (minSize > currentSize) {
                    minSize = currentSize;
                    //System.out.println("MIN "+minSize);
                    for (int ci = 0; ci < currentVariableOrder.size(); ci++) {
                        bestVariableOrder.set(ci, currentVariableOrder.get(ci));
                    }
                    //return;
                }
                BDD.T.gc();
                loopGlobalIndex++;
            }
            /*
            System.out.println("Current order");
            printOrder(currentVariableOrder);
            System.out.println("size:"+bdd.size());
            System.out.println("");
            System.out.println("TO UP");
            // Bottom to up
            for(int j=variables.size()-1; j>0; j--){
            String var = BDD.variables().get(j);
            String varVarI = BDD.variables().get(varI);
            this.swap(j,varI);
            Collections.swap(currentVariableOrder, j, j-1);
            String order = printOrder(currentVariableOrder);
            System.out.println("swapedBackVar:"+varI+"("+varVarI+")");
            System.out.println("swapedVar:"+j+"("+var+")_order:"+order+"_size:"+bdd.size());
            Printer.printBDD(bdd, "upBdd_index:"+loopGlobalIndex+"_swapedVar:"+j+"("+var+")_order:"+order+"_size:"+bdd.size());
            int currentSize = this.size();
            System.out.println("CURRENT "+currentSize);
            if(minSize > currentSize){
            minSize = currentSize;
            System.out.println("MIN "+minSize);
            for(int ci=0; ci<currentVariableOrder.size(); ci++){
            bestVariableOrder.set(ci, currentVariableOrder.get(ci));
            }
            //return;
            }
            this.gc();
            loopGlobalIndex++;
            }
            //*/
        //}*/
    }
    
    
}
