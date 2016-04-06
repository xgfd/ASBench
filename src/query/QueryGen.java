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
import utils.GraphViz;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by xgfd on 11/03/2016.
 */
public class QueryGen {
    private static List<String> priories;
    private static List<String> ignore;
    private static final Node a = NodeFactory.createURI("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
    private static final String xsd = "http://www.w3.org/2001/XMLSchema#";
    private static Resource lastNode;
    private static Model model;

    private static final double SELECT_P = 0.7;
    private static final double SELECT_NUM_P = 0.3;
    private static final double GROW_STEM = 0.7;
    private static final int STEM_LENGTH = 6;

    public static void main(String[] args) {
        String[] inputFileNames = args;

        Model model = loadRDF(inputFileNames);
        model.setNsPrefix("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");

        //read file into stream, try-with-resources
        try (Stream<String> prioryStream = Files.lines(Paths.get("./np.txt")); Stream<String> ignoreStream = Files.lines(Paths.get("./ignore.txt"))) {
            priories = prioryStream.collect(Collectors.toList());
            ignore = ignoreStream.collect(Collectors.toList());

            Set<Resource> roots = getRoots(model);

            roots.stream()
                    .forEach((res) -> {
                        System.out.println("Generating query from " + res);
                        varId = 0;
                        Query query = randomWalk(model, res);
                        System.out.println(query);
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Set<Resource> getRoots(Model model) {
        int cap = 20;

        Set<Resource> resSet = new HashSet<>();

//        try (Stream<String> seeds = Files.lines(Paths.get("./seeds.txt"))) {
//            resSet = seeds.map(uri -> model.getResource(model.expandPrefix("dbo:" + uri))).collect(Collectors.toSet());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        ResIterator resIterator = model.listSubjects();
        double size = model.listSubjects().toSet().size();
        double thresh = cap / size;

        Random ran = new Random();

        while (resIterator.hasNext()) {
            Resource sub = resIterator.nextResource();
            if (ran.nextDouble() < thresh) {
                resSet.add(sub);
            }
        }

        return resSet;
    }

    private static Model loadRDF(String[] inputFileNames) {
        // create an empty model
        model = ModelFactory.createDefaultModel();
        // read the RDF file
        System.out.println("Loading RDF...");
        for (int i = 0; i < inputFileNames.length; i++) {
            model.read(inputFileNames[i]);
        }
        System.out.println("Loading finished");
        return model;
    }

    private static Query randomWalk(Model model, Resource node) {

//        Resource node = model.getResource(root);
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

        while (iter.hasNext() && stem.size() < STEM_LENGTH && leaves.size() < 10) {
            Statement statement = iter.nextStatement();
            Resource p = statement.getPredicate();

            if (ignore.contains(p.getURI())) {
                continue;
            }

            if (priories.contains(p.toString()) && ran.nextDouble() < SELECT_NUM_P) {
                // property whose range is a numerical value
                leaves.add(statement);
            } else {
                // property whose range is not a numerical value
                RDFNode obj = statement.getObject();
                if (obj.isURIResource() && obj != lastNode && ran.nextDouble() < SELECT_P) {

                    Resource objNode = (Resource) obj;

                    // 50% chance to extend the current chain
                    boolean growStem = ran.nextDouble() < GROW_STEM;

                    if (growStem) {
                        lastNode = objNode;
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
        Random ran = new Random();
        StmtIterator iter = node.listProperties();
        while (iter.hasNext()) {
            Statement statement = iter.nextStatement();
            Resource p = statement.getPredicate();
            if (priories.contains(p.toString()) && ran.nextDouble() < SELECT_NUM_P) {
                // property whose range is a numerical value
                list.add(statement);
            }
        }
    }

    private static Query toQuery(Set<Statement> numP, Set<Statement> nonNumP) {
        Random ran = new Random();
        Set<String> variables = new HashSet<>();

        Op op;
        BasicPattern pat = new BasicPattern();                 // Make a pattern

        nonNumP.stream()
                .map((s) -> {
//                    boolean sub = false;
//                    boolean obj = false;

//                    String sUri = s.getSubject().getURI();
//                    String oUri = ((Resource) s.getObject()).getURI();
//
//                    while (!sub && !obj) {
//                        if (variables.contains(sUri)) {
//                            sub = true;
//                        }
//
//                        if (variables.contains(oUri)) {
//                            obj = true;
//                        }
//                    }
//
//                    if (variables.contains(sUri)) {
//                        sub = true;
//                        if (ran.nextBoolean()) {
//                            obj = true;
//                        }
//                    } else {
//                        if (ran.nextBoolean()) {
//                            sub = true;
//                        }
//
//                        if (ran.nextBoolean()) {
//                            obj = true;
//                        }
//                    }
//
//
//                    if (sub) {
//                        variables.add(sUri);
//                    }
//
//                    if (obj) {
//                        variables.add(oUri);
//                    }

                    return tripleToQuery(s, true, true);
                })
                // Add our pattern match
                .forEach(pat::add);

        numP.stream()
                .map((s) -> {
//                    boolean sub = false;
//                    boolean obj = true;
//                    if (variables.contains(s.getSubject())) {
//                        sub = true;
//                    }
                    return tripleToQuery(s, true, true);
                })
                .forEach(pat::add);

        List<Triple> triples = pat.getList();

        Set<Triple> toAdd = new HashSet<>();
        Set<Triple> toRemove = new HashSet<>();

        triples.stream().forEach(t -> {
            Node sub = t.getSubject();
            Node obj = t.getObject();

            if (sub.isConcrete()) {
                Node inst = Var.alloc("instOf" + sub.getLocalName());
                Triple t1 = Triple.create(inst, a, sub);
                Triple t2 = Triple.create(inst, t.getPredicate(), obj);
                toAdd.add(t1);
                toAdd.add(t2);
                toRemove.add(t);
            }

            if (obj.isConcrete()) {
                Node inst = Var.alloc("instOf" + obj.getLocalName());
                Triple t1 = Triple.create(inst, a, obj);
                Triple t2 = Triple.create(sub, t.getPredicate(), inst);
                toAdd.add(t1);
                toAdd.add(t2);
                toRemove.add(t);
            }
        });

        triples.removeAll(toRemove);
        triples.addAll(toAdd);

        System.out.println(toGraphviz(triples).toString());

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

    private static int varId = 0;

    private static Node resToNode(Resource r, boolean toVar) {
        Node n;
        if (toVar) {
            String name = r.getLocalName();

            if (r.getURI().contains(xsd)) {
                name += varId++;
            }

            n = Var.alloc(name);
        } else {
            n = NodeFactory.createURI(r.getURI());
        }
        return n;
    }

    private static String toGraphviz(Collection<Triple> triples) {

        //to graphviz
        GraphViz gv = new GraphViz();
        Iterator<Triple> iter = triples.iterator();
        while (iter.hasNext()) {
            Triple t = iter.next();
            String v1, v2;

            if (t.getSubject().isVariable()) {
                v1 = "?" + t.getSubject().getName();
            } else {
                v1 = "dbo:" + t.getSubject().getLocalName();
            }

            if (t.getObject().isVariable()) {
                v2 = "?" + t.getObject().getName();
            } else {
                v2 = "dbo:" + t.getObject().getLocalName();
            }

            gv.addEdge(v1, model.qnameFor(t.getPredicate().getURI()), v2);
        }

        return gv.toString();
    }
}
