package com.gangweedganggang.cs4240.ir;

import com.gangweedganggang.cs4240.flowgraph.ControlFlowGraph;
import com.gangweedganggang.cs4240.flowgraph.edges.ConditionalJumpEdge;
import com.gangweedganggang.cs4240.flowgraph.edges.FlowEdge;
import com.gangweedganggang.cs4240.flowgraph.edges.ImmediateEdge;
import com.gangweedganggang.cs4240.flowgraph.edges.UnconditionalJumpEdge;
import com.gangweedganggang.cs4240.ir.stmts.*;

import java.util.*;

public class IRControlFlowGraph extends ControlFlowGraph<IRBasicBlock> {
    public IRControlFlowGraph() {
        super(IRBasicBlock::new);
    }

    @Override
	public void verify() {
        super.verify();
    	if (getEntries().size() != 1)
    		throw new IllegalStateException("Wrong number of entries: " + getEntries());

    	int maxId = 0;
		Set<Integer> usedIds = new HashSet<>();
		for (IRBasicBlock b : vertices()) {
			if (!usedIds.add(b.getNumericId()))
				throw new IllegalStateException("Id collision: " + b);
			if (b.getNumericId() > maxId)
				maxId = b.getNumericId();
			if (getReverseEdges(b).size() == 0 && !getEntries().contains(b))
				throw new IllegalStateException("dead incoming: " + b);
			for (FlowEdge<IRBasicBlock> fe : getEdges(b)) {
				if (fe.src() != b)
					throw new RuntimeException(fe + " from " + b);
				IRBasicBlock dst = fe.dst();
				if (!containsVertex(dst) || !containsReverseVertex(dst))
					throw new RuntimeException(fe + "; dst invalid: " + containsVertex(dst) + " : " + containsReverseVertex(dst));
				boolean found = getReverseEdges(dst).contains(fe);
				if (!found)
					throw new RuntimeException("no reverse: " + fe);
			}
			if (b.isEmpty())
				throw new RuntimeException("empty block " + b);
			b.checkConsistency();
			long immEdgecount = getEdges(b).stream().filter(ImmediateEdge.class::isInstance).count();
			long condEdgeCount = getEdges(b).stream().filter(ConditionalJumpEdge.class::isInstance).count();
			long uncondEdgeCount = getEdges(b).stream().filter(UnconditionalJumpEdge.class::isInstance).count();
			if (immEdgecount >= 1 || condEdgeCount >= 1) {
				assert immEdgecount == 1;
				assert condEdgeCount == 1;
				assert getEdges(b).size() == 2;
			}
			if (uncondEdgeCount >= 1) {
				assert uncondEdgeCount == 1;
				assert getEdges(b).size() == 1;
			}
			long incomingImmCount = getReverseEdges(b).stream().filter(ImmediateEdge.class::isInstance).count();
			if (incomingImmCount >= 1) {
				assert incomingImmCount == 1;
			}
			IRStmt lastStmt = b.get(b.size() - 1);
			if (lastStmt instanceof GotoStmt) {
				IRBasicBlock dst = ((GotoStmt) lastStmt).getDst();
				assert getEdges(b).size() == 1 && getEdges(b).iterator().next().dst() == dst;
			} else if (lastStmt instanceof CondBranchStmt) {
				IRBasicBlock t = ((CondBranchStmt) lastStmt).getTrueBlock();
				IRBasicBlock f = ((CondBranchStmt) lastStmt).getFalseBlock();
				Optional<ConditionalJumpEdge> trueEdge = getEdges(b).stream().filter(ConditionalJumpEdge.class::isInstance).map(ConditionalJumpEdge.class::cast).findFirst();
				Optional<ImmediateEdge> falseEdge = getEdges(b).stream().filter(ImmediateEdge.class::isInstance).map(ImmediateEdge.class::cast).findFirst();
				assert trueEdge.isPresent() && trueEdge.get().dst() == t;
				assert falseEdge.isPresent() && falseEdge.get().dst() == f;
			} else if (lastStmt instanceof ReturnStmt || lastStmt instanceof MainReturnStmt) {
				assert getEdges(b).isEmpty();
			}

		}
		// if (maxId != size())
		// 	throw new IllegalStateException("Bad id numbering: " + size() + " vertices total, but max id is " + maxId);

		// check ordering -.-
		List<IRBasicBlock> order = verticesInOrder();
		for (ListIterator<IRBasicBlock> iterator = order.listIterator(); iterator.hasNext(); ) {
			IRBasicBlock b = iterator.next();
			for (FlowEdge<IRBasicBlock> e : getEdges(b)) {
				if (e instanceof ImmediateEdge) {
					assert order.get(iterator.nextIndex()).equals(e.dst());
				}
			}
		}
	}
}
