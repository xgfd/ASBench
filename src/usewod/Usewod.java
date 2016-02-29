package usewod;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.sparql.algebra.Algebra;
import org.apache.jena.sparql.algebra.Op;
import org.apache.jena.sparql.sse.SSE;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class Usewod {

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        String inputFolder = args[0];
        System.out.print(inputFolder);
        try {
            Files.walk(Paths.get(inputFolder)).filter(Files::isRegularFile)
                    .filter((Path file) -> file.getFileName().toString().startsWith("access"))
                    .peek(System.out::println)
                    .forEach(Usewod::parseFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void parseFile(Path file) {
        try (Stream<String> lines = Files.lines(file)) {
            lines.map(line -> {
                try {
                    return URLDecoder.decode(line, StandardCharsets.UTF_8.name());
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    return "";
                }
            }).map(Usewod::parseToOp).forEach((Op op)-> {
                SSE.write(op);
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

    static Op parseToOp(String q) {
        Query query = QueryFactory.create(q) ;
        Op op = Algebra.compile(query);
        return op;
    }
}


