package utils;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.shared.PrefixMapping;

import java.util.*;

/**
 * Created by xgfd on 15/03/2016.
 */
public class GraphViz {
    private Set<List<String>> edges = new HashSet<>();

    public GraphViz() {
    }

    public void addTriple(Triple t, PrefixMapping model) {
        String s = t.getSubject().toString(model);
        String p = t.getPredicate().toString(model);
        String o = t.getObject().toString(model);

        addEdge(s, p, o);
    }

    private String getQName(Node n, PrefixMapping m) {
        String name;
        if (n.isVariable()) {
            name = n.toString();
        } else {
            name = n.toString(m);
        }
        return name;
    }

    public void addEdge(String v1, String e, String v2) {

        List<String> edge = new ArrayList<>();
        edge.add(v1);
        edge.add(e);
        edge.add(v2);

        edges.add(edge);
    }

    @Override
    public String toString() {
        String graph = "digraph{\n";

        Iterator<List<String>> eiter = edges.iterator();

        while (eiter.hasNext()) {
            List<String> e = eiter.next();
            String v1 = e.get(0), v2 = e.get(2);
            String label = e.get(1);
//          "$v1"->"$v2"[label="${label}"]
            String edge = "\"" + v1 + "\"->\"" + v2 + "\"[label=\"" + label + "\"];\n";
            graph += edge;
        }

        graph += "}";

        return graph;
    }
}
