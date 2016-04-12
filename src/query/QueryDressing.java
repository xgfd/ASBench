package query;

import org.apache.jena.query.Query;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.expr.Expr;
import org.apache.jena.sparql.expr.ExprAggregator;
import org.apache.jena.sparql.expr.ExprVar;
import org.apache.jena.sparql.expr.aggregate.Aggregator;
import org.apache.jena.sparql.expr.aggregate.AggregatorFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
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
        addAgg(q, vars, Agg.CountExpr, true);
    }

    /**
     * Extend a query with COUNT on all variables, i.e. COUNT([DISTINCT] *)
     *
     * @param q        Extend the given query with aggregator
     * @param distinct Aggregate on distinct values
     */
    public static void countStar(Query q, boolean distinct) {
        List<ExprAggregator> aggs = new ArrayList();
        aggs.add(new ExprAggregator(Var.alloc("count"), AggregatorFactory.createCount(distinct)));
        _addAgg(q, aggs, Agg.Count);
    }

    /**
     * Extend a query with aggregation on each variable in {@value vars}, i.e. AGG([DISTINCT] {@value var}) AS {@value var}_{@value type}
     *
     * @param q        Extend the given query with aggregator
     * @param vars     List of variables to be aggregated
     * @param type     Type of aggregator
     * @param distinct Aggregate on distinct values
     */
    public static void addAgg(Query q, Collection<Var> vars, Agg type, boolean distinct) {
        List<ExprAggregator> aggs = vars.stream()
                .map(v -> new ExprAggregator(v, varToAgg(type, v, distinct)))
                .collect(Collectors.toList());

        _addAgg(q, aggs, type);
    }

    private static void _addAgg(Query q, List<ExprAggregator> aggs, Agg type) {
        if (q.isQueryResultStar()) {
            q.setQueryResultStar(false);
            q.getProject().clear();
        }

        aggs.forEach(expr -> q.addResultVar(expr.getVar().getName() + "_" + type, expr));
    }

    private enum Agg {Avg, Count, CountExpr, Max, Min, Sample, Sum}

    private static Aggregator varToAgg(Agg type, Var v, boolean distinct) {
        try {
            Method method = AggregatorFactory.class.getDeclaredMethod("create" + type, boolean.class, Expr.class);

            return (Aggregator) method.invoke(AggregatorFactory.class, distinct, new ExprVar(v));

        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return AggregatorFactory.createAggNull();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return AggregatorFactory.createAggNull();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            return AggregatorFactory.createAggNull();
        }
    }
}
