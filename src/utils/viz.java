package utils;

import org.apache.jena.graph.Triple;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.shared.PrefixMapping;
import org.apache.jena.sparql.algebra.Algebra;
import org.apache.jena.sparql.algebra.Op;
import org.apache.jena.sparql.algebra.OpWalker;
import sparql.TripleCollector;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by xgfd on 07/04/2016.
 */
public class viz {

    public static void main(String[] args) {
        boolean isRDF = args.length > 1 && args[1].contains("--t");

        String dot = isRDF ? parseRDF(args[0]) : parseQuery(args[0]);
        System.out.println(dot);
    }

    public String toGViz(String input, boolean isRDF) {
        String dot = isRDF ? parseRDF(input) : parseQuery(input);
        return dot;
    }

    private static String getQuery(String q) {
        if (isPath(q)) {
            //read query from file
            try (Stream<String> queryStr = Files.lines(Paths.get(q))) {
                q = queryStr.map(s -> s + " ").reduce("", String::concat);
            } catch (IOException e) {
                e.printStackTrace();
                q = "";
            }
        } else {
            //assuming q is a query and do nothing
        }
        return q;
    }

    private static String parseQuery(String s) {
        String q = getQuery(s);

        try {
            Query query = QueryFactory.create(q);
            Op op = Algebra.compile(query);
            TripleCollector tc = new TripleCollector();
            OpWalker.walk(op, tc);
            Set<Triple> triples = tc.getTriples();
            return _toGViz(triples, query.getPrefixMapping());
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private static Model readRDF(String s) {
        Model model = ModelFactory.createDefaultModel();

        if (isPath(s)) {
            System.out.println("Parsing file " + s);
            model.read(s);
        } else {
            try {
                System.out.println("Parsing RDF string " + s);
                RDFDataMgr.read(model, new ByteArrayInputStream(s.getBytes("UTF-8")), Lang.TTL);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return model;
    }

    private static String parseRDF(String s) {
        Model model = readRDF(s);
        StmtIterator iter = model.listStatements();
        Set<Triple> triples = iter.toSet().stream().map(stmt -> stmt.asTriple()).collect(Collectors.toSet());
        return _toGViz(triples, model);
    }

    private static String _toGViz(Collection<Triple> triples, PrefixMapping m) {
        GraphViz gv = new GraphViz();
        triples.stream().forEach(t -> gv.addTriple(t, m));
        return gv.toString();
    }

    private static boolean isPath(String s) {
        String regex = "^[. /]?.*\\.(nt|ttl|nq|trig|rdf|owl|jsonld|trdf|rt|rj|trix)(.gz)?";
        return Pattern.matches(regex, s);
    }
}
