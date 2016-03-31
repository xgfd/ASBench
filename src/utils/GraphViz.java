package utils;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.rdf.model.Model;

import java.util.*;

/**
 * Created by xgfd on 15/03/2016.
 */
public class GraphViz {
    //    private int id = 0;
//    private Map<String, Integer> nodes = new HashMap<>();
    private Set<List<String>> edges = new HashSet<>();

    public GraphViz() {
    }

    public void addTriple(Triple t, Model model) {
        Node s = t.getSubject();
        Node p = t.getPredicate();
        Node o = t.getObject();


    }

    public void addEdge(String v1, String e, String v2) {

//        if (!nodes.containsKey(v1)) {
//            int id1 = id++;
//            nodes.put(v1, id1);
//        }
//
//        if (!nodes.containsKey(v2)) {
//            int id2 = id++;
//            nodes.put(v2, id2);
//        }

        List<String> edge = new ArrayList<>();
        edge.add(v1);
        edge.add(e);
        edge.add(v2);

        edges.add(edge);
    }

    @Override
    public String toString() {
        String graph = "digraph{\n";

//        Iterator<Map.Entry<String, Integer>> niter = nodes.entrySet().iterator();
//
//        while (niter.hasNext()) {
//            Map.Entry<String, Integer> n = niter.next();
////            1[label="name"];
//            String nStr = n.getValue() + "[label=\"" + n.getKey() + "\"];\n";
//            graph += nStr;
//        }

        Iterator<List<String>> eiter = edges.iterator();

        while (eiter.hasNext()) {
            List<String> e = eiter.next();
//            1--2[label="name"];
            String v1 = e.get(0), v2 = e.get(2);
            String label = e.get(1);
            String eStr = "\"" + v1 + "\"->\"" + v2 + "\"[label=\"" + label + "\"];\n";
            graph += eStr;
        }

        graph += "}";

        return graph;
    }
}
