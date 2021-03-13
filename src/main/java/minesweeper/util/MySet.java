package minesweeper.util;

public class MySet {

    private int size;
    private String[] entries;
    private String tombstone;
    private int entriesUsed;

    public MySet(int capacity) {
        this.size = 0;
        this.entries = new String[capacity];
        this.tombstone = "tombstone";
        this.entriesUsed = 0;
    }

    public MySet() {
        this(16);
    }

    public boolean add(String e) {
        if (entriesUsed > entries.length * 0.75) {
            reHash();
        }

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
/*
    public void addAll(String[] e) {
        for (int i = 0; i < e.length; i++) {
            add(e[i]);
        }
    }
*/

    public boolean remove(String e) {
        int index = indexOf(e);
        if (entries[index] == null) {
            return false;
        } else {
            entries[index] = tombstone;
            size--;
            return true;
        }
    }

    public boolean contains(String e) {
        int index = indexOf(e);
        return entries[index] != null;
    }

    private int indexOf(String e) {
        int index = e.hashCode() % entries.length;
        int firstTombstoneIndex = -1;

        while (true) {
            if (entries[index] == null) {
                return index;
            } else if (entries[index].equals(e)) {
                if (firstTombstoneIndex != -1) {
                    entries[firstTombstoneIndex] = e;
                    entries[index] = tombstone;
                    return firstTombstoneIndex;
                } else {
                    return index;
                }
            } else {
                if (entries[index] == tombstone && firstTombstoneIndex == -1) {
                    firstTombstoneIndex = index;
                }
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
        String[] oldEntries = entries;
        size = 0; //add will calculate again
        entriesUsed = 0;
        entries = new String[oldEntries.length * 2];

        for (int i = 0; i < oldEntries.length; i++) {
            if (oldEntries[i] != null && oldEntries[i] != tombstone) {
                add(oldEntries[i]);
            }
        }
    }
}
