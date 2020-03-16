package com.gangweedganggang.cs4240.flowgraph;

import com.gangweedganggang.cs4240.flowgraph.edges.FlowEdge;
import com.gangweedganggang.cs4240.stdlib.util.TabbedStringWriter;

import java.util.Collection;
import java.util.Iterator;

public class CFGUtils {
    // /**
	//  * Renders the cfg as an image using graphviz.
	//  * This should be for debugging purposes only.
	//  * @param cfg cfg to dump.
	//  * @param filename output name without file extension.
	//  */
	// public static void easyDumpCFG(ControlFlowGraph cfg, String filename) {
	// 	IPropertyDictionary dict = PropertyHelper.createDictionary();
	// 	dict.put(new BooleanProperty(CFGExporterUtils.OPT_EDGES, true));
	// 	dict.put(new BooleanProperty(CFGExporterUtils.OPT_STMTS, true));
	// 	try {
	// 		Exporter.fromGraph(CFGExporterUtils.makeDotGraph(cfg, dict)).export(new File("cfg testing", filename + ".png"));
	// 	} catch (IOException e) {
	// 		e.printStackTrace();
	// 	}
	// }

	public static String printBlocks(Collection<BasicBlock> bbs) {
		TabbedStringWriter sw = new TabbedStringWriter();
		int insn = 1;
		for(BasicBlock bb : bbs) {
			blockToString(sw, bb.getGraph(), bb, insn);
			insn += bb.size();
		}
		return sw.toString();
	}

	public static String printBlock(BasicBlock b) {
		TabbedStringWriter sw = new TabbedStringWriter();
		blockToString(sw, b.getGraph(), b, 1);
		return sw.toString();
	}

	public static <T extends BasicBlock> void blockToString(TabbedStringWriter sw, ControlFlowGraph<T> cfg, T b, int insn) {
		// sw.print("===#Block " + b.getId() + "(size=" + (b.size()) + ")===");
		sw.print(String.format("===#Block %s(size=%d, flags=%s)===", b.getDisplayName(), b.size(), Integer.toBinaryString(b.getFlags())));
		sw.tab();

		Iterator<Stmt> it = b.iterator();
		if(!it.hasNext()) {
			sw.untab();
		} else {
			sw.print("\n");
		}
		while(it.hasNext()) {
			Stmt stmt = it.next();
//			sw.print(stmt.getId() + ". ");
			sw.print(insn++ + ". ");
			sw.print(stmt.toString());

			if(!it.hasNext()) {
				sw.untab();
			} else {
				sw.print("\n");
			}
		}

		sw.tab();
		sw.tab();

		if(cfg.containsVertex(b)) {
			for(FlowEdge<T> e : cfg.getEdges(b)) {
                sw.print("\n-> " + e.toString());
			}

			for(FlowEdge<T> p : cfg.getReverseEdges(b)) {
                sw.print("\n<- " + p.toString());
			}
		}

		sw.untab();
		sw.untab();

		sw.print("\n");
	}
}
