/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package djbdd.io;

import djbdd.BDD;
import djbdd.Vertex;
import graphvizjava.*;
import java.util.*;
import java.io.File;

/**
 *
 * @author diegoj
 */
public class BDDPrinter {

    /* Output file type */
    static String FILE_TYPE = "png";
    final boolean SHOW_NODE_PATHS = false;
    /** BDD tree to print */
    BDD bdd = null;
    HashMap<String, Boolean> edgeCache = null;

    protected String getVertexName(Vertex v, String pathName){
        String name = "";
        if (v.isLeaf()) {
            name = Boolean.toString(v.value());
            if (SHOW_NODE_PATHS) {
                //name += " (" + pathName + ")";
            }
            return name;
        }

        if (v.variable > -1) {
            name = bdd.variables().get(v.variable);
            if(name.endsWith("_")){
                name = name.substring(0, name.length()-1);
            }
            if (SHOW_NODE_PATHS) {
                name += " (" + pathName + ")";
            }else{
                name += " (" + v.index + ")";
            }
            return name;
        }

        if (v.index != -1) {
            name = "" + v.variable;
            return name;
        }
        return name;
    }

    public BDDPrinter(BDD bdd) {
        this.bdd = bdd;
    }

    protected void createTree(GraphViz graph, Vertex v, String pathName) {
        if (v.index > 1) {
            Vertex low = bdd.T.get(v.low());
            Vertex high = bdd.T.get(v.high());
            this.createTree(graph, low, pathName + "L");
            this.createTree(graph, high, pathName + "H");
            String lowEdgeKey = v.index + "-" + v.low();
            String vName = getVertexName(v, pathName);
            if (!edgeCache.containsKey(lowEdgeKey)) {
                edgeCache.put(lowEdgeKey, true);
                String lowName = getVertexName(low, pathName + "L");
                graph.addln("\"" + vName + "\" -> \"" + lowName + "\" [dir=\"forward\" arrowtype=\"normal\" style=\"dashed\"];");
            }
            String highEdgeKey = v.index + "-" + v.high();
            if (!edgeCache.containsKey(highEdgeKey)) {
                edgeCache.put(highEdgeKey, true);
                String highName = getVertexName(high, pathName + "H");
                graph.addln("\"" + vName + "\" -> \"" + highName + "\" [dir=\"forward\" arrowtype=\"normal\" style=\"normal\"];");
            }
        }

    }

    public void print(String path) {

        GraphViz gv = new GraphViz();
        gv.addln(gv.start_graph());

        String pathName = "R";
        this.edgeCache = new HashMap<String, Boolean>();
        createTree(gv, bdd.root(), pathName);

        gv.addln(gv.end_graph());
        System.out.println(gv.getDotSource());

        String type = FILE_TYPE;
        if (!path.contains("\\" + FILE_TYPE)) {
            path += "." + FILE_TYPE;
        }
        File out = new File(path);   // Linux
        System.out.println("Saving in "+out.getAbsolutePath());
        gv.writeGraphToFile(gv.getGraph(gv.getDotSource(), type), out);
    }
    
    public static void printBDD(BDD bdd, String path){
        BDDPrinter printer = new BDDPrinter(bdd);
        printer.print(path);
    }
}
