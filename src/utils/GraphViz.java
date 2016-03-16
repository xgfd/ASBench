package utils;

import java.util.*;

/**
 * Created by xgfd on 15/03/2016.
 */
public class GraphViz {
    private int id = 0;
    private Map<String, Integer> nodes = new HashMap<>();
    private Set edges = new HashSet<>();

    public GraphViz() {
    }

    public void addEdge(String v1, String e, String v2) {

        if (!nodes.containsKey(v1)) {
            int id1 = id++;
            nodes.put(v1, id1);
        }

        if (!nodes.containsKey(v2)) {
            int id2 = id++;
            nodes.put(v2, id2);
        }

        List edge = new ArrayList<>();
        edge.add(nodes.get(v1));
        edge.add(e);
        edge.add(nodes.get(v2));

        edges.add(edge);
    }

    @Override
    public String toString() {
        String graph = "graph{\n";

        Iterator<Map.Entry<String, Integer>> niter = nodes.entrySet().iterator();

        while (niter.hasNext()) {
            Map.Entry<String, Integer> n = niter.next();
//            1[label="(1)"];
            String nStr = n.getValue() + "[label=\"(" + n.getKey() + ")\"];\n";
            graph += nStr;
        }

        Iterator<List> eiter = edges.iterator();

        while (eiter.hasNext()) {
            List e = eiter.next();
//            1--1[label=""];
            Integer v1 = (Integer) e.get(0), v2 = (Integer) e.get(2);
            String label = (String) e.get(1);
            String eStr = v1 + "--" + v2 + "[label=\"" + label + "\"];\n";
            graph += eStr;
        }

        graph+="}";

        return graph;
    }
}
