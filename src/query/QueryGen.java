package query;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.FileManager;

import java.io.InputStream;

/**
 * Created by xgfd on 11/03/2016.
 */
public class QueryGen {
    public static void main(String[] args) {
        String inputFileName = args[0],
                rootURI = "<http://dbpedia.org/ontology/Person>";

        if (args.length > 1) {
            rootURI=args[1];
        }

    }

    private static Model loadRDF(String inputFileName) {
        // create an empty model
        Model model = ModelFactory.createDefaultModel();

        // use the FileManager to find the input file
        InputStream in = FileManager.get().open(inputFileName);
        if (in == null) {
            throw new IllegalArgumentException(
                    "File: " + inputFileName + " not found");
        }

// read the RDF file
        model.read(in, null);

        return model;
    }

    private static randomWalk(Model model, String root) {


    }
}
