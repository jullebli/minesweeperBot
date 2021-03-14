package minesweeper.util;

//Because getPossibleMoves (Bots) needs to return ArrayList so MyList must
//be converted to ArrayList
import java.util.ArrayList;

public class MyList<E> {

    private int size;
    private E[] entries;

    public MyList() {
        size = 0;
        entries = (E[]) new Object[8];
    }

    /**
     * Adds a specified element to the end of this list.
     *
     * @param e element to be added to this list
     */
    public void add(E e) {
        if (size == entries.length) {

            E[] help = (E[]) new Object[size * 2];
            for (int i = 0; i < size; i++) {
                help[i] = entries[i];
            }
            entries = help;
        }

        entries[size] = e;
        size++;
    }

    /**
     * Adds all of the elements on a given list to this list.
     *
     * @param list a list which elements will be added to this list
     */
    public void addAll(MyList list) {
        for (int i = 0; i < list.size(); i++) {
            add((E) list.get(i));
        }
    }

    /**
     * Removes the element at the specified position (index) in this list.
     *
     * @param index the index of the element to be removed
     */
    public void remove(int index) {
        for (int i = index; i < size - 1; i++) {
            entries[i] = entries[i + 1];
        }
        size--;
    }

    /**
     * Returns the element at the specified position (index) in this list.
     *
     * @param index index of the element to return
     * @return the element at the specified position in this list
     */
    public E get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        return entries[index];
    }

    /**
     * Returns true if this list contains no elements.
     *
     * @return true if this list is empty
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Returns the number of elements in this list.
     *
     * @return the number of elements in this list
     */
    public int size() {
        return size;
    }

    /**
     * Returns an ArrayList containing all of the elements in this list. The
     * elements will be same type and in the same order as in this list.
     *
     * @return an ArrayList containing the elements on this list.
     */
    public ArrayList<E> toArrayList() {
        ArrayList<E> arrayList = new ArrayList<>(size);

        for (int i = 0; i < size; i++) {
            arrayList.add(entries[i]);
        }

        return arrayList;
    }
}
