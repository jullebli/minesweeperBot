package minesweeper.util;

import java.util.HashSet; //needed to convert HashSet to MySet
import java.util.Iterator;

public class MySet<E> implements Iterable<E> {

    private int size;
    private Object[] entries;
    public final static String tombstone = "tombstone";
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
        this(hSet.size() != 0 ? hSet.size() * 2 : 6);
        
        for (E e : hSet) {
            add(e);
        }
    }

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

    public void addAll(MySet<E> eSet) {
        for (E e : eSet) {
            add(e);
        }
    }

    public boolean remove(E e) {
        int index = indexOf(e);
        if (entries[index] == null) {
            return false;
        } else {
            entries[index] = tombstone;
            size--;
            return true;
        }
    }

    public boolean contains(E e) {
        int index = indexOf(e);
        return entries[index] != null;
    }

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

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void reHash() {
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
                if (oldEntries[i] != null && oldEntries[i] != tombstone) {
                    add((E) oldEntries[i]);
                }
            }
        }
    }

    @Override
    public Iterator<E> iterator() {
        return new MySetIterator(entries, this);
    }

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
