package sparql;

import org.apache.jena.sparql.algebra.OpVisitor;
import org.apache.jena.sparql.algebra.op.*;
import org.apache.jena.sparql.expr.ExprAggregator;

import java.util.HashMap;
import java.util.List;

/**
 * Created by xgfd on 01/03/2016.
 */
public class FeatureCounter implements OpVisitor {
    private HashMap<String, Integer> counts = new HashMap<String, Integer>();

    private void increaseCount(String name) {
        counts.put(name, counts.getOrDefault(name, 0) + 1);
    }

    public HashMap<String, Integer> getCounts() {
        return counts;
    }

    @Override
    public void visit(OpBGP opBGP) {

    }

    @Override
    public void visit(OpQuadPattern opQuadPattern) {

    }

    @Override
    public void visit(OpQuadBlock opQuadBlock) {

    }

    @Override
    public void visit(OpTriple opTriple) {

    }

    @Override
    public void visit(OpQuad opQuad) {

    }

    @Override
    public void visit(OpPath opPath) {

    }

    @Override
    public void visit(OpTable opTable) {

    }

    @Override
    public void visit(OpNull opNull) {

    }

    @Override
    public void visit(OpProcedure opProcedure) {

    }

    @Override
    public void visit(OpPropFunc opPropFunc) {

    }

    @Override
    public void visit(OpFilter opFilter) {

    }

    @Override
    public void visit(OpGraph opGraph) {

    }

    @Override
    public void visit(OpService opService) {

    }

    @Override
    public void visit(OpDatasetNames opDatasetNames) {

    }

    @Override
    public void visit(OpLabel opLabel) {

    }

    @Override
    public void visit(OpAssign opAssign) {

    }

    @Override
    public void visit(OpExtend opExtend) {
        String name = opExtend.getName();
        increaseCount(name);
    }

    @Override
    public void visit(OpJoin opJoin) {

    }

    @Override
    public void visit(OpLeftJoin opLeftJoin) {

    }

    @Override
    public void visit(OpUnion opUnion) {

    }

    @Override
    public void visit(OpDiff opDiff) {

    }

    @Override
    public void visit(OpMinus opMinus) {

    }

    @Override
    public void visit(OpConditional opConditional) {

    }

    @Override
    public void visit(OpSequence opSequence) {

    }

    @Override
    public void visit(OpDisjunction opDisjunction) {

    }

    @Override
    public void visit(OpExt opExt) {

    }

    @Override
    public void visit(OpList opList) {

    }

    @Override
    public void visit(OpOrder opOrder) {

    }

    @Override
    public void visit(OpProject opProject) {
        String name = opProject.getName();
        increaseCount(name);
    }

    @Override
    public void visit(OpReduced opReduced) {

    }

    @Override
    public void visit(OpDistinct opDistinct) {

    }

    @Override
    public void visit(OpSlice opSlice) {

    }

    @Override
    public void visit(OpGroup opGroup) {

        List<ExprAggregator> aggregators = opGroup.getAggregators();
        for (ExprAggregator agg : aggregators) {
            String name = agg.getAggregator().getName();
            increaseCount(name);
        }
        increaseCount(opGroup.getName());
    }

    @Override
    public void visit(OpTopN opTopN) {

    }
}
