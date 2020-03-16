package com.gangweedganggang.cs4240.stdlib.collections.list;

import java.util.*;
import java.util.function.Consumer;

/**
 * An ArrayList but with callbacks for when an element is removed or added.
 * This is really a pretty big kludge since ArrayList's internals aren't fixed,
 * and subclassing ArrayList is generally a bad idea anyways.
 *
 * Yes, this *does* acount for the Iterator's remove, add, etc.
 *
 * @param <E> element type
 */
public class WrappingNotifiedList<E> implements NotifiedList<E> {
	private final List<Consumer<E>> onAdded;
	private final List<Consumer<E>> onRemoved;
	private final List<E> backingList;

	public WrappingNotifiedList(List<E> backingList) {
		this.backingList = backingList;
		this.onAdded = new ArrayList<>();
		this.onRemoved = new ArrayList<>();
	}

	public WrappingNotifiedList() {
		this(new ArrayList<>());
	}

	@Override
	public void subscribeAdded(Consumer<E> callback) {
		onAdded.add(callback);
	}

	@Override
	public void unsubscribeAdded(Consumer<E> callback) {
		onAdded.remove(callback);
	}

	@Override
	public void subscribeRemoved(Consumer<E> callback) {
		onRemoved.add(callback);
	}

	@Override
	public void unsubscribeRemoved(Consumer<E> callback) {
		onRemoved.remove(callback);
	}

	private void onAdded(E elem) {
		onAdded.forEach(f -> f.accept(elem));
	}

	private void onRemoved(E elem) {
		onRemoved.forEach(f -> f.accept(elem));
	}

	// List methods
	@Override
	public boolean add(E elem) {
		boolean ret = backingList.add(elem);
		onAdded(elem);
		return ret;
	}

	@Override
	public void add(int index, E elem) {
		backingList.add(index, elem);
		onAdded(elem);
	}

	@Override
	public boolean remove(Object o) {
		boolean ret = backingList.remove(o);
		if (ret)
			onRemoved((E) o);
		return ret;
	}

	@Override
	public E remove(int index) {
		E oldElem = backingList.remove(index);
		onRemoved.forEach(f -> f.accept(oldElem));
		return oldElem;
	}

	@Override
	public E set(int index, E elem) {
		onAdded(elem);
		E oldElem = backingList.set(index, elem);
		onRemoved(oldElem);
		return oldElem;
	}

	// Overridden collective updates
	@Override
	public boolean addAll(Collection<? extends E> c) {
		for (E elem : c)
			add(elem);
		return c.size() != 0;
	}

	@Override
	public boolean addAll(int index, Collection<? extends E> c) {
		for (E elem : c)
			add(index++, elem);
		return c.size() != 0;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		boolean ret = false;
		for (Object o : c)
			ret = remove(o) || ret; // keep in mind that must be after the || due to short-circuiting
		return ret;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		boolean ret = false;
		Iterator<E> it = iterator();
		while (it.hasNext()) {
			E elem = it.next();
			if (!c.contains(elem)) {
				it.remove();
				ret = true;
			}
		}
		return ret;
	}

	@Override
	public void clear() {
		Iterator<E> it = iterator();
		while (it.hasNext()) {
			E s = it.next();
			it.remove();
		}
	}

	@Override
	public E get(int index) {
		return backingList.get(index);
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
		return backingList.contains(o);
	}

	@Override
	public Iterator<E> iterator() {
		Iterator<E> backingIter = backingList.iterator();
		return new Iterator<E>() {
			E curElem;

			@Override
			public boolean hasNext() {
				return backingIter.hasNext();
			}

			@Override
			public E next() {
				return curElem = backingIter.next();
			}

			@Override
			public void remove() {
				backingIter.remove();
				onRemoved(curElem);
			}

			@Override
			public void forEachRemaining(Consumer<? super E> action) {
				backingIter.forEachRemaining(action);
			}
		};
	}

	@Override
	public Object[] toArray() {
		return backingList.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return backingList.toArray(a);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return backingList.contains(c);
	}

	@Override
	public int indexOf(Object o) {
		return backingList.indexOf(o);
	}

	@Override
	public int lastIndexOf(Object o) {
		return backingList.lastIndexOf(o);
	}

	class ListIterWrapper implements ListIterator<E> {
		ListIterator<E> backingIter;

		ListIterWrapper(ListIterator<E> backingIter) {
			this.backingIter = backingIter;
		}

		E curElem;

		@Override
		public boolean hasNext() {
			return backingIter.hasNext();
		}

		@Override
		public E next() {
			return curElem = backingIter.next();
		}

		@Override
		public boolean hasPrevious() {
			return backingIter.hasPrevious();
		}

		@Override
		public E previous() {
			return curElem = backingIter.previous();
		}

		@Override
		public int nextIndex() {
			return backingIter.nextIndex();
		}

		@Override
		public int previousIndex() {
			return backingIter.previousIndex();
		}

		@Override
		public void remove() {
			onRemoved(curElem);
			backingIter.remove();
		}

		@Override
		public void set(E e) {
			onRemoved(curElem);
			onAdded(e);
			backingIter.set(e);
		}

		@Override
		public void add(E e) {
			onAdded(e);
			backingIter.add(e);
		}
	}

	@Override
	public ListIterator<E> listIterator() {
		return new ListIterWrapper(backingList.listIterator());
	}

	@Override
	public ListIterator<E> listIterator(int index) {
		return new ListIterWrapper(backingList.listIterator(index));
	}

	// Blocked functions
	@Override
	public List<E> subList(int fromIndex, int toIndex) {
		// the sublist implementation directly modifies elementData. No bueno.
		throw new UnsupportedOperationException();
	}
}
