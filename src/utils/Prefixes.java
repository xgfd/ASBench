package utils;

import org.apache.jena.shared.PrefixMapping;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

/**
 * Created by xgfd on 08/04/2016.
 */
public class Prefixes {
    public static PrefixMapping getPrefixes(String file) {
        PrefixMapping pm = PrefixMapping.Factory.create();

        try (Stream<String> prefixes = Files.lines(Paths.get(Prefixes.class.getResource(file).toURI()))) {
            prefixes.forEach(l -> lineToPrefix(l, pm));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return pm;
    }


    /**
     * Convert a line of prefix into a prefix mapping.
     *
     * @param l  Prefix in the form "prefix URL".
     * @param pm A PrefixMapping to which prefixes are added.
     */
    private static void lineToPrefix(String l, PrefixMapping pm) {
        String[] pf = l.split(" ");
        pm.setNsPrefix(pf[0], pf[1]);
    }
}
