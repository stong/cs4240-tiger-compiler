package com.gangweedganggang.cs4240.stdlib.collections.list;

import com.gangweedganggang.cs4240.stdlib.collections.map.NullPermeableHashMap;

import java.util.*;

/**
 * List with index cache for fast index lookup.
 * @param <T> Element type
 */
public class IndexedList<T> implements List<T> {
	private final NullPermeableHashMap<T, List<Integer>> indexMap;
	private final List<T> backingList;
	protected final NotifiedCollection<T> notifier;
	private boolean dirty;

	public IndexedList() {
		NotifiedList<T> notifiedList = new WrappingNotifiedList<>();
		backingList = notifiedList;
		notifier = notifiedList;
		notifier.subscribeAdded((t) -> dirty = true);
		notifier.subscribeRemoved((t) -> dirty = true);
		indexMap = new NullPermeableHashMap<>(ArrayList::new);
		dirty = false;
	}
	
	public IndexedList(Collection<T> other) {
		this();
		addAll(other);
		recacheIndices();
	}
	
	private void recacheIndices() {
		if (!dirty)
			return;
		indexMap.clear();
		for (int i = 0; i < backingList.size(); i++)
			indexMap.getNonNull(backingList.get(i)).add(i);
		dirty = false;
	}
	
	@Override
	public int size() {
		return backingList.size();
	}
	
	@Override
	public boolean isEmpty() {
		return backingList.isEmpty();
	}
	
	@Override
	public boolean contains(Object o) {
		recacheIndices();
		return indexMap.containsKey(o);
	}
	
	@Override
	public Iterator<T> iterator() {
		return backingList.iterator();
	}
	
	@Override
	public Object[] toArray() {
		return backingList.toArray();
	}
	
	@Override
	public <T1> T1[] toArray(T1[] a) {
		return backingList.toArray(a);
	}
	
	@Override
	public boolean add(T t) {
		boolean ret = backingList.add(t);
		dirty |= ret;
		return ret;
	}
	
	@Override
	public boolean remove(Object o) {
		boolean ret = backingList.remove(o);
		dirty |= ret;
		return ret;
	}
	
	@Override
	public boolean containsAll(Collection<?> c) {
		return backingList.contains(c);
	}
	
	@Override
	public boolean addAll(Collection<? extends T> c) {
		boolean ret = backingList.addAll(c);
		dirty |= ret;
		return ret;
	}
	
	@Override
	public boolean addAll(int index, Collection<? extends T> c) {
		boolean ret = backingList.addAll(index, c);
		dirty |= ret;
		return ret;
	}
	
	@Override
	public boolean removeAll(Collection<?> c) {
		boolean ret = backingList.removeAll(c);
		dirty |= ret;
		return ret;
	}
	
	@Override
	public boolean retainAll(Collection<?> c) {
		boolean ret = backingList.retainAll(c);
		dirty |= ret;
		return ret;
	}
	
	@Override
	public void clear() {
		backingList.clear();
		indexMap.clear();
		dirty = false;
	}
	
	@Override
	public T get(int index) {
		return backingList.get(index);
	}
	
	@Override
	public T set(int index, T element) {
		T prev = backingList.set(index, element);
		dirty |= prev != element;
		return prev;
	}
	
	@Override
	public void add(int index, T element) {
		backingList.add(index, element);
		dirty = true;
	}
	
	@Override
	public T remove(int index) {
		T ret = backingList.remove(index);
		dirty = true;
		return ret;
	}
	
	@Override
	public int indexOf(Object o) {
		recacheIndices();
		List<Integer> indices = indexMap.get(o);
		return indices == null ? -1 : indices.get(0);
	}
	
	@Override
	public int lastIndexOf(Object o) {
		recacheIndices();
		List<Integer> indices = indexMap.get(o);
		return indices == null ? -1 : indices.get(indices.size() - 1);
	}
	
	@Override
	public ListIterator<T> listIterator() {
		return backingList.listIterator();
	}
	
	@Override
	public ListIterator<T> listIterator(int index) {
		return backingList.listIterator(index);
	}
	
	@Override
	public IndexedList<T> subList(int fromIndex, int toIndex) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public String toString() {
		return backingList.toString();
	}
}
