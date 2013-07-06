/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package djbdd;

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
    
    class Node {

        public BDD bdd = null;
        public Vertex v = null;
        public boolean visited = false;
        public Node low = null;
        public Node high = null;
        String name = "";
        String label = "";

        public Node(Vertex v, BDD bdd) {
            this.v = v;
            this.bdd = bdd;
        }

        /**
         * Obtains the node name: if BDD is reduced it gets the id, else returns the node path-from-root name
         */
        public String setName(String pathName) {
            if (v.isLeaf()) {
                this.name = Boolean.toString(v.value());
                if(SHOW_NODE_PATHS)
                    this.name += " (" + pathName + ")";
                return this.name;
            }
            
            if (v.variable > -1) {
                this.name = bdd.variables.get(v.variable);
                if(SHOW_NODE_PATHS)
                    this.name += " (" + pathName + ")";
                this.name = this.name.replaceAll("_", "");
                return this.name;
            }
            
            if (v.index != -1) {
                this.name = "" + v.variable;
                return this.name;
            }
            this.name = pathName;
            return this.name;
        }

        /**
         * Obtains the node label that is the variable name that the node represents
         */
        private String setLabel() {
            // Obtains the node variable name
            // If it's not a leaf, get the variable name associated
            if (!v.isLeaf()) {
                //System.out.println(v.index+" "+bdd.variables.get(v.index - 1));
                //System.out.flush();
                this.label = bdd.variables.get(v.index - 1);
                return this.label;
            }
            // If it's a leaf, get 1/0 from True/False, resp.
            if (v.value()) {
                this.label = "1";
            }
            this.label = "0";
            return this.label;
        }
        
        public String toString(){
            return "{"+this.v.toString()+"| l="+this.low+" h="+this.high+"}";
        }
    }

    /**  Generates the graph */
    private void _create_graph(GraphViz graph, Node n, String pathName) {
        // Traverse all nodes and creates a dot graph"""
        //System.out.println(n);
        //System.out.println(n.v);
        //System.out.println(n.v.index);
        if (n.v.index > 1) {
            this._create_graph(graph, n.low, pathName + "L");
            this._create_graph(graph, n.high, pathName + "H");
            if (!n.visited) {
                n.visited = true;
                n.setName(pathName);
                graph.addln("\""+n.name + "\" -> \"" + n.low.name + "\" [dir=\"forward\" arrowtype=\"normal\" style=\"dashed\"];");
                graph.addln("\""+n.name + "\" -> \"" + n.high.name + "\" [dir=\"forward\" arrowtype=\"normal\" style=\"normal\"];");
            }
        } else if (n.v.isLeaf()) {
                if (!n.visited) {
                    n.visited = true;
                    n.setName(pathName);
                    n.setLabel();
                }
            }
        }
    
    /**
     * Prints the BDD.
     */
    public void print(String path) {

       HashMap<Integer,Node> nodes = new HashMap<Integer,Node>();
       ArrayList<Vertex> vertices = bdd.T.getVertices();
       for (Vertex v : vertices) {
            Node n = new Node(v, bdd);
            nodes.put(v.index, n);
        }
        Node root = nodes.get(bdd.root.index);

        for (int i = 0; i < vertices.size(); i++) {
            Vertex v = vertices.get(i);
            int index = v.index;
            if (v.low != Vertex.NULL_INDEX) {
                nodes.get(index).low = nodes.get(v.low);
            }
            if (v.high != Vertex.NULL_INDEX) {
                nodes.get(index).high = nodes.get(v.high);
            }
        }
        
        for (Integer nodeKey : nodes.keySet()){
            Node n = nodes.get(nodeKey);
            if(n.low!=null && n.low!=null && n.low.v.index != n.v.low)
                System.out.println("El nodo "+n.v.index+" está petao");
            if(n.high!=null && n.high!=null && n.high.v.index != n.v.high)
                System.out.println("El nodo "+n.v.index+" está petao");
        }

        GraphViz gv = new GraphViz();
        //GraphViz gv = null;
        gv.addln(gv.start_graph());

        String pathName = "R";
        this._create_graph(gv, root, pathName);

        //gv.addln("A -> B;");
        //gv.addln("A -> C;");
        gv.addln(gv.end_graph());
        System.out.println(gv.getDotSource());
        
        String type = FILE_TYPE;
//      String type = "gif";
//      String type = "dot";
//      String type = "fig";    // open with xfig
//      String type = "pdf";
//      String type = "ps";
//      String type = "svg";    // open with inkscape
//      String type = "png";
//      String type = "plain";
        if(!path.contains("\\"+ FILE_TYPE))
            path += "."+FILE_TYPE;
        File out = new File(path);   // Linux
        gv.writeGraphToFile(gv.getGraph(gv.getDotSource(), type), out);
    }
    
    
    public BDDPrinter(BDD bdd){
        this.bdd = bdd;
    }
}
