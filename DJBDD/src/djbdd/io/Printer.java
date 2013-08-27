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
public class Printer {

    /* Output file type */
    static String FILE_TYPE = "png";
    
    /** Should the graph show the vertex paths? */
    final boolean SHOW_NODE_PATHS = false;
    
    /** BDD tree to print */
    BDD bdd = null;
    
    /** Should we print debug messages? */
    static final boolean VERBOSE = false;
    
    /** Caché of edges of the graph */
    HashMap<String, Boolean> edgeCache = null;

    /**
     * Obtains the name of the vertex.
     * @param v Vertex that contain the name.
     * @param pathName Complete path from the root ot the tree.
     * @return The complete name of the vertex v.
     */
    protected String getVertexName(Vertex v, String pathName){
        String name = "";
        if (v.isLeaf()) {
            name = Boolean.toString(v.value());
            if (SHOW_NODE_PATHS) {
                name += " (" + pathName + ")";
            }
            return name;
        }

        if (v.variable() > -1) {
            name = bdd.variables().get(v.variable());
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
            name = "" + v.variable();
            return name;
        }
        return name;
    }

    /**
     * Constructor.
     * @param bdd BDD that will be used in the image generation.
     */
    public Printer(BDD bdd) {
        this.bdd = bdd;
    }
    
    public Printer() {
        this.bdd = null;
    }    

    /**
     * Creates the tree in the GraphViz graph.
     * @param graph Graph that will contain the BDD.
     * @param v Root vertex of the subtree.
     * @param pathName Complete tree path.
     */
    protected void createTree(GraphViz graph, Vertex v, String pathName) {
        if (v.index > 1) {
            Vertex low = v.low();
            Vertex high = v.high();
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

    /**
     * Creates the global tree in the GraphViz graph.
     * That is, the tree that contains all the BDDs.
     * @param graph Graph that will contain the BDD.
     * @param pathName Complete tree path.
     */
    protected void createMultiTree(GraphViz graph, String pathName){
        ArrayList<Vertex> roots = new ArrayList<Vertex>();
        for(Vertex v : BDD.T.getVertices()){
            boolean isRoot = true;
            for(Vertex w : BDD.T.getVertices()){
                if(v.index != w.index && v.isChildOf(w)){
                    isRoot = false;
                    break;
                }
            }
            if(isRoot)
                roots.add(v);
        }
        
        for(Vertex r : roots){
            pathName = "";
            this.createTree(graph, r, pathName);
        }
    }
    
    /**
     * Prints the BDD as an image in a path.
     * @param path Path that will be the image of the BDD.
     */
    public void print(String path) {
        GraphViz gv = new GraphViz();
        gv.addln(gv.start_graph());

        String pathName = "R";
        this.edgeCache = new HashMap<String, Boolean>();
                    
            
        if(this.bdd!=null)
           createTree(gv, bdd.root(), pathName);
        else
            createMultiTree(gv, pathName);

        gv.addln(gv.end_graph());
        if(VERBOSE){
            System.out.println("Dot graph:");
            System.out.println(gv.getDotSource());
        }

        String type = FILE_TYPE;
        if (!path.contains("\\" + FILE_TYPE)) {
            path += "." + FILE_TYPE;
        }
        File out = new File(path);   // Linux
        if(VERBOSE){
            System.out.println("Saving in "+out.getAbsolutePath());
        }
        gv.writeGraphToFile(gv.getGraph(gv.getDotSource(), type), out);
    }

    /**
     * Prints a BDD as an image in a path.
     * @param bdd BDD that will be printed as image.
     * @param path Path that will be the image of the BDD.
     */
    public static void printBDD(BDD bdd, String path){
        Printer printer = new Printer(bdd);
        printer.print(path);
    }
    
    /**
     * Prints a TableT as an image in a path.
     * @param bdd BDD that will be printed as image.
     * @param path Path that will be the image of the BDD.
     */
    public static void printTableT(String path){
        BDD.gc();
        Printer printer = new Printer();
        printer.print(path);
    }    
}
