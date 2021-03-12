
package minesweeper.util;

public class MyList<E> {
    private int size;
    private E[] entries;
    
    public MyList() {
        size = 0;
        entries = (E[])new Object[8];
    }
    
    public void add(E e) {
        if (size == entries.length) {
            
            E[] help = (E[])new Object[size * 2];
            for (int i = 0; i < size; i++) {
                help[i] = entries[i];
            }
            entries = help;
        }
        
        entries[size] = e;
        size++;
    }
    
    public void addAll(MyList list) {       
        for (int i = 0; i < list.size(); i++) {
            add((E)list.get(i));
        }
    }
    
    public void remove(int index) {
        for (int i = index; i < size - 1; i++) {
            entries[i] = entries[i + 1];
        }
        size--;
    }
    
    public E get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        return entries[index];
    }
    
    public boolean isEmpty() {
        return size == 0;
    }
    
    public int size() {
        return size;
    }
}
