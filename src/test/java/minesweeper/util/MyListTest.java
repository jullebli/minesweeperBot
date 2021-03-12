
package minesweeper.util;

import minesweeper.util.MyList;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertTrue;

public class MyListTest {
    private MyList<String> list;
    
    public MyListTest() {
        list = new MyList<>();
    }
    
    @Before
    public void setUp() {
        list.add("one");
    }
    
    @Test
    public void getWorks() {
        assertEquals("one", list.get(0));
    }
    
    @Test
    public void addIncreasesSizeByOne() {
        int sizeBefore = list.size();
        list.add("two");
        assertEquals(sizeBefore + 1, list.size());
    }
    
    @Test
    public void sizeIsOne() {
        assertEquals(1, list.size());
    }
    
    @Test
    public void addAllAddsManyToSize() {
        MyList list2 = new MyList();
        list2.add("two");
        list2.add("three");
        list2.add("four");
        
        list.addAll(list2);
        assertEquals(4, list.size());
    }
    
    @Test
    public void removeDecrementsSizeByOne() {
        list.remove(0);
        assertEquals(0, list.size());
    }
    
    @Test
    public void entryChangesIndexAfterRemove() {
        list.add("two");
        list.remove(0);
        assertEquals("two", list.get(0));
    }
    
    @Test
    public void IntegerTypeList() {
        MyList<Integer> list3 = new MyList<>();
        list3.add(1);
        assertEquals(1, (int)list3.get(0));
    }
}
