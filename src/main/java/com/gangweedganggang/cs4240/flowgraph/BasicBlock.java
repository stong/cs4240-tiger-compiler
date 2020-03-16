package com.gangweedganggang.cs4240.flowgraph;

import com.gangweedganggang.cs4240.stdlib.collections.graph.FastGraphVertex;
import com.gangweedganggang.cs4240.stdlib.collections.list.NotifiedIndexedList;

import java.util.*;

import static com.gangweedganggang.cs4240.stdlib.util.StringHelper.createBlockName;

public class BasicBlock<T extends Stmt> implements FastGraphVertex, Iterable<T> {
	/**
	 * Specifies that this block should not be merged in later passes.
	 */
	public static final int FLAG_NO_MERGE = 0x1;

	/**
	 * Two blocks A, B, must have A.id == B.id IFF A == B
	 * Very important!
	 */
	private int id;
	public final ControlFlowGraph cfg;
	private final NotifiedIndexedList<T> statements;
	private int flags = 0;

	// for debugging purposes. the number of times the label was changed
	private int relabelCount = 0;

	public BasicBlock(ControlFlowGraph cfg) {
		this.cfg = cfg;
		this.id = cfg.makeBlockId();
		statements = new NotifiedIndexedList<>();
		statements.subscribeAdded((s) -> {
		    if (s != null)
		        s.setBlock(this);
        });
        statements.subscribeRemoved((s) -> {
            if (s != null && s.getBlock() == this)
                s.setBlock(null);
        });
	}

	public boolean isFlagSet(int flag) {
		return (flags & flag) == flag;
	}

	public void setFlag(int flag, boolean b) {
		if(b) {
			flags |= flag;
		} else {
			flags ^= flag;
		}
	}

	public void setFlags(int flags) {
		this.flags = flags;
	}

	public int getFlags() {
		return flags;
	}

	public ControlFlowGraph getGraph() {
		return cfg;
	}

	public void transfer(BasicBlock dst) {
		Iterator<T> it = statements.iterator();
		while(it.hasNext()) {
			T s = it.next();
			it.remove();
			dst.add(s);
			assert (s.getBlock() == dst);
		}
	}

	/**
	 * Transfers statements up to index `to`, exclusively, to block `dst`.
	 */
	public void transferUpto(BasicBlock dst, int to) {
		// FIXME: faster
		for(int i=to - 1; i >= 0; i--) {
			T s = remove(0);
			dst.add(s);
			assert (s.getBlock() == dst);
		}
	}

	@Override
	public String getDisplayName() {
		return createBlockName(id);
	}

	/**
	 * If you call me you better know what you are doing.
	 * If you use me in any collections, they must be entirely rebuilt from scratch
	 * ESPECIALLY indexed or hash-based ones.
	 * This includes collections of edges too.
	 * @param i newId
	 */
	public void setId(int i) {
		relabelCount++;
		id = i;
	}

	@Override
	public int getNumericId() {
		return id;
	}

	@Override
	public String toString() {
		return String.format("Block #%s", createBlockName(id)/* (%s), label != null ? label.hashCode() : "dummy"*/);
	}

	// This implementation of equals doesn't really do anything, it's just for sanity-checking purposes.
	// NOTE: we can't change equals or hashCode because the id can change from ControlFlowGraph#relabel.
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		BasicBlock bb = (BasicBlock) o;

		if (id == bb.id) {
			assert (relabelCount == bb.relabelCount);
			assert (this == bb);
		}
		return id == bb.id;
	}

	public void checkConsistency() {
		for (T stmt : statements)
			if (stmt.getBlock() != this)
				throw new IllegalStateException("Orphaned child " + stmt);
	}

	// List functions
	public boolean add(T stmt) {
		return statements.add(stmt);
	}

	public void add(int index, T stmt) {
		statements.add(index, stmt);
	}

	public boolean remove(Object o) {
		return statements.remove(o);
	}

	public boolean containsAll(Collection<?> c) {
		return statements.containsAll(c);
	}

	public boolean addAll(Collection<? extends T> c) {
		return statements.addAll(c);
	}

	public boolean addAll(int index, Collection<? extends T> c) {
		return statements.addAll(index, c);
	}

	public boolean removeAll(Collection<?> c) {
		return statements.removeAll(c);
	}

	public boolean retainAll(Collection<?> c) {
		return statements.retainAll(c);
	}

	public T remove(int index) {
		return statements.remove(index);
	}

	public boolean contains(Object o) {
		return statements.contains(o);
	}

	public boolean isEmpty() {
		return statements.isEmpty();
	}

	public int indexOf(Object o) {
		return statements.indexOf(o);
	}

	public int lastIndexOf(Object o) {
		return statements.lastIndexOf(o);
	}

	public T get(int index) {
		return statements.get(index);
	}

	public T set(int index, T stmt) {
		return statements.set(index, stmt);
	}

	public int size() {
		return statements.size();
	}

	public void clear() {
		statements.clear();
	}

	public Iterator<T> iterator() {
		return statements.iterator();
	}

	public ListIterator<T> listIterator() {
		return statements.listIterator();
	}

	public ListIterator<T> listIterator(int index) {
		return statements.listIterator(index);
	}

	public List<T> subList(int fromIndex, int toIndex) {
		throw new UnsupportedOperationException();
	}

	public Object[] toArray() {
		return statements.toArray();
	}

	public <T> T[] toArray(T[] a) {
		return statements.toArray(a);
	}
	// End list functions
}
