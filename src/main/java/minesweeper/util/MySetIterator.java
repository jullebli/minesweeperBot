package minesweeper.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class MySetIterator<E> implements Iterator<E> {

    private int index;
    private Object[] entries;
    private MySet<E> set;
    private boolean removed;
    private boolean nextCalled;

    public MySetIterator(Object[] entries, MySet<E> set) {
        this.entries = entries;
        this.index = 0;
        this.set = set;
        this.removed = false;
        this.nextCalled = false;
    }

    /**
     * Returns true if this iteration has more elements.
     *
     * @return true if there are more elements in this iteration.
     */
    @Override
    public boolean hasNext() {
        while (index < entries.length && (entries[index] == null
                || entries[index] == MySet.TOMBSTONE)) {
            index++;
        }
        return index < entries.length;
    }

    /**
     * Returns the next element in this iteration. Throws NoSuchElementExceptio
     * if this iteration has no more elements.
     *
     * @return the next element in this iteration
     */
    @Override
    public E next() {
        nextCalled = true;
        removed = false;
        if (!hasNext()) {
            throw new NoSuchElementException();
        } else {
            E entry = (E) entries[index];
            index++;
            return entry;
        }
    }

    /**
     * Removes from the set the element that was last given by this iterator.
     * This method can be called once per every next() call. If it is called
     * consecutively and/or before a call to next() then an
     * IllegalStateException is thrown.
     */
    @Override
    public void remove() {
        if (removed || !nextCalled) {
            throw new IllegalStateException();
        }
        set.remove((E) entries[index - 1]);
        removed = true;
    }
}
