package query;

import org.apache.jena.query.Query;
import org.apache.jena.sparql.algebra.Algebra;
import org.apache.jena.sparql.algebra.Op;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.expr.ExprAggregator;
import org.apache.jena.sparql.expr.ExprVar;
import org.apache.jena.sparql.expr.aggregate.AggCountVarDistinct;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by xgfd on 11/04/2016.
 */
public class QueryDressing {
    public static void countDistinctAll(Query q) {
        List<Var> vars = q.getProjectVars();
        countDistinct(q, vars);
    }

    public static void countDistinct(Query q, Collection<Var> vars) {
        Op op = Algebra.compile(q);
//        List<AggCountVarDistinct> distCountVars =
        List<ExprAggregator> aggs = vars.stream()
                .map(v -> new ExprAggregator(v, new AggCountVarDistinct(new ExprVar(v))))
                .collect(Collectors.toList());

        q.getProject().clear();
        q.setQueryResultStar(false);
        aggs.forEach(expr -> q.addResultVar(expr.getVar().getName() + "_Count", expr));
//        q.addProjectVars(distCountVars);
    }
}
