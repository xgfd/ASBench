package query;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.Query;
import org.apache.jena.rdf.model.*;
import org.apache.jena.sparql.algebra.Op;
import org.apache.jena.sparql.algebra.OpAsQuery;
import org.apache.jena.sparql.algebra.op.OpBGP;
import org.apache.jena.sparql.core.BasicPattern;
import org.apache.jena.sparql.core.Var;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by xgfd on 11/03/2016.
 */
public class QueryGen {
    private static List<String> numericProperties;
    private static List<String> ignore;
    private static Resource lastNode;

    public static void main(String[] args) {
        String inputFileName = args[0],
                rootURI = "<http://dbpedia.org/ontology/Person>";

        if (args.length > 1) {
            rootURI = args[1];
        }

        Model model = loadRDF(inputFileName);

        //read file into stream, try-with-resources
        try (Stream<String> stream = Files.lines(Paths.get("./properties.txt")); Stream<String> ignoreStream = Files.lines(Paths.get("./ignore.txt"))) {
            numericProperties = stream.collect(Collectors.toList());
            ignore = ignoreStream.collect(Collectors.toList());

            Set<Resource> roots = getRoots(model, 5);

            roots.stream()
                    .forEach((res) -> {
                        System.out.println("Generating query from " + res);
                        Query query = randomWalk(model, res.getURI());
                        System.out.println(query);
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Set<Resource> getRoots(Model model, int cap) {
        ResIterator resIterator = model.listSubjects();
        Set<Resource> resSet = resIterator.toSet();
        float thresh = cap / resSet.size();

        Random ran = new Random();

        while (resIterator.hasNext()) {
            if (ran.nextDouble() > thresh) {
                resSet.remove(resIterator.nextResource());
            }
        }

        return resSet;
    }

    private static Model loadRDF(String inputFileName) {
        // create an empty model
        Model model = ModelFactory.createDefaultModel();
        // read the RDF file
        System.out.println("Loading RDF...");
        model.read(inputFileName);
        System.out.println("Loading finished");
        return model;
    }

    private static Query randomWalk(Model model, String root) {

        Resource node = model.getResource(root);
        Set<Statement> numP = new HashSet();
        Set<Statement> nonNumP = new HashSet();

        System.out.println("Selecting statements...");
        walkNode(node, numP, nonNumP);

        System.out.println(numP);
        System.out.println(nonNumP);

        System.out.println("Converting to query...");
        Query query = toQuery(numP, nonNumP);
        return query;
    }

    private static void walkNode(Resource node, Set<Statement> leaves, Set<Statement> stem) {
        Random ran = new Random();
        StmtIterator iter = node.listProperties();

        while (iter.hasNext() && stem.size() < 6 && leaves.size() < 10) {
            Statement statement = iter.nextStatement();
            Resource p = statement.getPredicate();

            if (ignore.contains(p.getURI())) {
                continue;
            }

            if (numericProperties.contains(p.toString())) {
                // property whose range is a numerical value
                leaves.add(statement);
            } else {
                // property whose range is not a numerical value
                RDFNode obj = statement.getObject();
                if (obj.isURIResource() && obj != lastNode) {

                    if (ran.nextDouble() > 0.7) {
                        //discard 30% properties
                        continue;
                    }

                    Resource objNode = (Resource) obj;

                    // 50% chance to extend the current chain
                    boolean growStem = ran.nextBoolean();

                    if (growStem) {
                        //grow a stem node to form a chain
                        stem.add(statement);
                        //continue walk a stem node
                        walkNode(objNode, leaves, stem);
                    } else {
                        //grow a leaf node
                        leaves.add(statement);
                        //add all numerical properties of an leaf node
                        addNumProperties(objNode, leaves);
                    }
                }
            }
        }
    }

    private static void addNumProperties(Resource node, Set<Statement> list) {
        StmtIterator iter = node.listProperties();
        while (iter.hasNext()) {
            Statement statement = iter.nextStatement();
            Resource p = statement.getPredicate();
            if (numericProperties.contains(p.toString())) {
                // property whose range is a numerical value
                list.add(statement);
            }
        }
    }

    private static Query toQuery(Set<Statement> numP, Set<Statement> nonNumP) {
        Random ran = new Random();
        Set<Resource> variables = new HashSet<>();

        Op op;
        BasicPattern pat = new BasicPattern();                 // Make a pattern

        final boolean[] isLastNodeVar = {false};

        nonNumP.stream()
                .map((s) -> {
                    boolean sub = false;
                    boolean obj = false;

                    if (!isLastNodeVar[0]) {
                        sub = true;
                        if (ran.nextBoolean()) {
                            obj = true;
                            isLastNodeVar[0] = true;
                        }
                    } else {
                        if (ran.nextBoolean()) {
                            sub = true;
                        }

                        if (ran.nextBoolean()) {
                            obj = true;
                            isLastNodeVar[0] = true;
                        }
                    }

                    if (variables.contains(s.getSubject())) {
                        sub = true;
                    }

                    if (variables.contains(s.getObject())) {
                        obj = true;
                    }

                    if (sub) {
                        variables.add(s.getSubject());
                    }

                    if (obj) {
                        variables.add((Resource) s.getObject());
                    }

                    return tripleToQuery(s, sub, obj);
                })
                // Add our pattern match
                .forEach(pat::add);

        numP.stream()
                .map((s) -> {
                    boolean sub = false;
                    boolean obj = false;
                    if (variables.contains(s.getSubject())) {
                        sub = true;
                    }
                    return tripleToQuery(s, sub, obj);
                })
                .forEach(pat::add);

        op = new OpBGP(pat);                                   // Make a BGP from this pattern
        Query q = OpAsQuery.asQuery(op);                       // Convert to a query
        q.setQuerySelectType();
        return q;
    }

    private static Triple tripleToQuery(Statement s, boolean sub, boolean obj) {
        Node subject = resToNode(s.getSubject(), sub), object = resToNode((Resource) s.getObject(), obj);
        Triple pattern = Triple.create(subject, resToNode(s.getPredicate(), false), object);
        return pattern;
    }

    private static Node resToNode(Resource r, boolean toVar) {
        Node n;
        if (toVar) {
            String name = r.getLocalName();
            n = Var.alloc(name);
        } else {
            n = NodeFactory.createURI(r.getURI());
        }
        return n;
    }
}
