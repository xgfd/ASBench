import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;

/**
 * Created by xgfd on 11/04/2016.
 */
public class test {
    public static void main(String[] args) {
        Query q = QueryFactory.create("SELECT (count(distinct ?s) as ?count) where{?s <http://ko.dbpedia.org/property/aa> ?o. ?s a ?class.} order by DESC(?count)");
        q.getProject();
        return;
    }
}
