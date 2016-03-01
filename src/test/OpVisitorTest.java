package test;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.sparql.algebra.Algebra;
import org.apache.jena.sparql.algebra.Op;
import org.apache.jena.sparql.algebra.OpWalker;
import org.apache.jena.sparql.sse.SSE;
import sparql.FeatureCounter;
import sparql.PatternCounter;

import java.util.HashMap;

/**
 * Created by xgfd on 01/03/2016.
 */
public class OpVisitorTest {

    public static void main(String[] args) {
        String q = "prefix dbp:<http://dbpedia.org/ontology/> prefix xsd:<http://www.w3.org/2001/XMLSchema#> prefix rdfs:<http://www.w3.org/2000/01/rdf-schema#> select ?athleteGroupEN (count(?athlete) as ?count) (avg(?age) as ?ageAvg) where { filter(?age >= 20 && ?age <= 100) .  { select distinct ?athleteGroupEN ?athlete (?deathYear - ?birthYear as ?age) where { ?subOfAthlete rdfs:subClassOf dbp:Athlete .  ?subOfAthlete rdfs:label ?athleteGroup filter(lang(?athleteGroup) = \"en\") .  bind(str(?athleteGroup) as ?athleteGroupEN) ?athlete a ?subOfAthlete .  ?athlete dbp:birthDate ?birth filter(datatype(?birth) = xsd:date) .  ?athlete dbp:deathDate ?death filter(datatype(?death) = xsd:date) .  bind (strdt(replace(?birth,\"^(\\\\d+)-.*\",\"$1\"),xsd:integer) as ?birthYear) .  bind (strdt(replace(?death,\"^(\\\\d+)-.*\",\"$1\"),xsd:integer) as ?deathYear) .  } } } group by ?athleteGroupEN having (count(?athleteGroup) >= 25) order by ?ageAvg";

        Query query = QueryFactory.create(q);
        Op op = Algebra.compile(query);
        SSE.write(op);

        PatternCounter pc = new PatternCounter();
        FeatureCounter fc = new FeatureCounter();
        OpWalker.walk(op, pc);
        OpWalker.walk(op, fc);

        int tCount = pc.getCount();
        HashMap<String, Integer> fCount = fc.getCounts();
        assert tCount == 5;
        assert fCount.get("AVG") == 1;
        assert fCount.get("project") == 2;
        assert fCount.get("COUNT") == 2;
        assert fCount.get("group") == 1;

        System.out.println(tCount);
        System.out.println(fCount);
    }

}
