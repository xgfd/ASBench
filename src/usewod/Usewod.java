package usewod;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.sparql.algebra.Algebra;
import org.apache.jena.sparql.algebra.Op;
import org.apache.jena.sparql.algebra.OpWalker;
import sparql.FeatureCounter;
import sparql.PatternCounter;

import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Stream;

public class Usewod {
    private static long totalQ = 0;
    private static HashMap<String, HashMap<Integer, Long>> histogram = new HashMap<>();
    private static FileOutputStream out;
    private static Path dbsse;

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        String inputFolder = args[0];
        dbsse = Paths.get(inputFolder + File.separator + "dbpedia.sse");
        try {
            out = new FileOutputStream(dbsse.toFile());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            Files.walk(Paths.get(inputFolder)).filter(Files::isRegularFile)
                    .filter((Path file) -> file.getFileName().toString().startsWith("access"))
                    .peek(System.out::println)
                    .forEach(Usewod::parseFile);
            System.out.println("Number of valid queries: " + totalQ);
            System.out.println("Query histogram: " + histogram);
            Files.write(dbsse, Arrays.asList("#Number of valid queries: " + totalQ, "#Query histogram: " + histogram), Charset.forName("UTF-8"), StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void parseFile(Path file) {
        Queue<String> lastLine = new ArrayDeque<>();
        try (Stream<String> lines = Files.lines(file)) {
            lines.map(line -> {
                try {
                    return URLDecoder.decode(line, StandardCharsets.UTF_8.name());
                } catch (UnsupportedEncodingException e) {
                    System.out.println(e.getMessage());
                    return null;
                } catch (IllegalArgumentException e) {
                    //System.out.println(e.getMessage());
                    return null;
                }
            })
                    .filter((String q) -> q != null)
                    .filter((String q) -> {
                        boolean noDup = !lastLine.contains(q);
                        if (noDup) {
                            if (lastLine.size() >= 100) {
                                lastLine.remove();
                            }
                            lastLine.add(q);
                        }
                        return noDup;
                    })
                    .map(Usewod::parseToOp)
                    .filter((List pair) -> pair != null)
                    .filter((List pair) -> {
                        Op op = (Op) pair.get(1);
                        PatternCounter pc = new PatternCounter();
                        OpWalker.walk(op, pc);
                        int tCount = pc.getCount();
                        return tCount != 0;
                    })
                    .forEach((List pair) -> {
                        String q = (String) pair.get(0);
                        Op op = (Op) pair.get(1);

                        totalQ++;
                        PatternCounter pc = new PatternCounter();
                        FeatureCounter fc = new FeatureCounter();
                        OpWalker.walk(op, pc);
                        OpWalker.walk(op, fc);

                        int tCount = pc.getCount();
                        HashMap<String, Integer> fCount = fc.getCounts();
                        Set<String> features = fCount.keySet();

                        increaseHist("triple", tCount);

                        for (String f : features) {
                            int category = fCount.get(f);
                            increaseHist(f, category);
                        }

                        if ((!fCount.containsKey("extend") && fCount.size() > 1) || (fCount.containsKey("extend") && fCount.size() > 2)) {

                            System.out.println(op.toString());
                            System.out.println("######");
                            System.out.println(toComment(q));
                            try {
                                Files.write(dbsse, Arrays.asList(op.toString(), "######", toComment(q)), Charset.forName("UTF-8"), StandardOpenOption.APPEND);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
//            int count = 0;
//            for (String line : (Iterable<String>) lines::iterator) {
//
//                System.out.println(line);
//                count++;
//                if (count > 10) {
//                    break;
//                }
//            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void increaseHist(String f, int category) {
        if (!histogram.containsKey(f)) {
            histogram.put(f, new HashMap<Integer, Long>());
        }
        HashMap<Integer, Long> fHist = histogram.get(f);
        fHist.put(category, fHist.getOrDefault(category, 0L) + 1);
    }

    static List parseToOp(String q) {
        try {
            Query query = QueryFactory.create(q);
            Op op = Algebra.compile(query);
//            System.out.println(toComment(q));

            return Arrays.asList(q, op);
        } catch (Exception e) {
//            System.out.println(toComment(q));
            //e.printStackTrace();
            return null;
        }
    }

    static String toComment(String q) {
        q = q.replace("\n", "\n#");
        q = "#" + q;
        return q;
    }
}
