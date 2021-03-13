package minesweeper.util;

import java.util.HashSet; //needed to convert HashSet to MySet
import java.util.Iterator;

public class MySet<E> implements Iterable<E> {

    private int size;
    private Object[] entries;
    public static final String TOMBSTONE = "tombstone";
    private int entriesUsed;

    public MySet(int capacity) {
        this.size = 0;
        this.entries = new Object[capacity];
        this.entriesUsed = 0;
    }

    public MySet() {
        this(16);
    }

    public MySet(HashSet<E> hSet) {
        this(!hSet.isEmpty() ? hSet.size() * 2 : 6);

        for (E e : hSet) {
            add(e);
        }
    }

    /**
     * Adds the specified element to this set if it is not already in this set.
     *
     * @param e element to be added to this set
     * @return true if this set did not already contain the specified element
     */
    public boolean add(E e) {
        reHash();

        int index = indexOf(e);
        if (entries[index] == null) {
            entries[index] = e;
            size++;
            entriesUsed++;
            return true;
        } else {
            return false;
        }
    }

    /**
     * Adds all of the elements on a given set to this set.
     *
     * @param eSet a set which elements will be added to this set.
     */
    public void addAll(MySet<E> eSet) {
        for (E e : eSet) {
            add(e);
        }
    }

    /**
     * Removes the specified element from this list if it is present. Places a
     * tombstone value in the position of the removed element.
     *
     * @param e element to be removed from this set
     * @return true if this set contained the specified element
     */
    public boolean remove(E e) {
        int index = indexOf(e);
        if (entries[index] == null) {
            return false;
        } else {
            entries[index] = TOMBSTONE;
            size--;
            return true;
        }
    }

    /**
     * Returns true if this set contains the specified element.
     *
     * @param e element which presence on this list is tested
     * @return true if this set contains the specified element
     */
    public boolean contains(E e) {
        int index = indexOf(e);
        return entries[index] != null;
    }

    /**
     * Returns the index of the specified element in this set. Returns an index
     * pointing to a null value if the element is not in this set.
     *
     * @param e element which index is determined
     * @return the index of the element in this set
     */
    private int indexOf(E e) {
        int index = e.hashCode() % entries.length;

        while (true) {
            if (entries[index] == null) {
                return index;
            } else if (entries[index].equals(e)) {
                return index;
            } else {
                index = (index + 1) % entries.length;
            }
        }
    }

    /**
     * Returns the number of elements in this set.
     *
     * @return the number of elements in this set
     */
    public int size() {
        return size;
    }

    /**
     * Returns true if this set contains no elements.
     *
     * @return true if this set is empty
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Calculates new positions to the elements on this set. If there are more
     * elements than 75 % of the set's capacity, the set space is doubled.
     * Tombstone values are deleted.
     */
    private void reHash() {
        if (entriesUsed > entries.length * 0.75) {

            Object[] oldEntries = entries;
            int newLength = oldEntries.length * 2;

            if (size < oldEntries.length * 0.5) {
                //just removing a lot of tombstones, no need of more space
                newLength = oldEntries.length;
            }
            size = 0; //add will calculate again
            entriesUsed = 0;
            entries = new Object[newLength];

            for (int i = 0; i < oldEntries.length; i++) {
                if (oldEntries[i] != null && oldEntries[i] != TOMBSTONE) {
                    add((E) oldEntries[i]);
                }
            }
        }
    }

    /**
     * Returns an iterator over the elements on this set. Iterator supports
     * remove. See MySetIterator class.
     *
     * @return an iterator over the elements on this set
     */
    @Override
    public Iterator<E> iterator() {
        return new MySetIterator(entries, this);
    }

    /**
     * For testing purposes only.
     *
     * @return a String representation of the set contents
     */
    public String toString() {
        StringBuilder sb = new StringBuilder("(");

        for (int i = 0; i < entries.length; i++) {
            if (i != 0) {
                sb.append("|");
            }
            sb.append(entries[i] != null ? entries[i].toString() : "null");
        }

        sb.append("," + size + ", " + entriesUsed + ")");

        return sb.toString();
    }
}
