package minesweeper.util;

import java.util.HashSet;
import java.util.Iterator;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

public class MySetTest {

    private MySet<String> set = new MySet(3);

    @Before
    public void setUp() {
        set.add("one");
        set.add("two");
    }

    @Test
    public void addIncreasesSizeByOne() {
        int sizeBefore = set.size();
        set.add("three");
        assertEquals(sizeBefore + 1, set.size());
    }

    @Test
    public void sizeIsTwo() {
        assertEquals(2, set.size());
    }

    @Test
    public void containsWorks() {
        assertTrue(set.contains("one"));
    }

    @Test
    public void containsWorks2() {
        assertTrue(set.contains("two"));
    }

    @Test
    public void containsWorks3() {
        //NullPointerException
        assertFalse(set.contains("three"));
    }

    /*
    @Test
    public void addAllAddsManyToSize() {
        MySet set2 = new MySet();
        set2.add("two");
        set2.add("three");
        set2.add("four");
        
        set.addAll(set2);
        assertEquals(4, set.size());
    }
     */
    @Test
    public void removeDecrementsSizeByOne() {
        set.remove("two");
        assertEquals(1, set.size());
    }

    @Test
    public void removeWorks() {
        set.remove("one");
        assertFalse(set.contains("one"));
    }

    @Test
    public void isEmptyWorks() {
        set.remove("two");
        set.remove("one");
        assertTrue(set.isEmpty());
    }

    @Test
    public void isEmptyWorks2() {
        set.remove("one");
        assertFalse(set.isEmpty());
    }

    /*
    @Test
    public void IntegerTypeSet() {
        MySet<Integer> set3 = new MySet<>();
        set3.add(1);
        assertEquals(1, (int)set3.get(0));
    }
     */
    @Test
    public void iteratorHasNext() {
        Iterator it = set.iterator();
        assertTrue(it.hasNext());
    }

    @Test
    public void iteratorContainsRightAmountOfValues() {
        HashSet<String> hSet = new HashSet();

        for (String value : set) {
            hSet.add(value);
        }
        assertEquals(set.size(), hSet.size());
    }

    @Test
    public void iteratorRemoveWorks() {
        Iterator it = set.iterator();
        it.next();
        it.remove();
        assertEquals(1, set.size());
    }

    @Test
    public void iteratorRemoveUntilEmpty() {
        set.add("three");
        set.add("Four");
        Iterator it = set.iterator();
        System.out.println(set.toString());
        while (it.hasNext()) {
            System.out.println(set.toString());
            it.next();
            System.out.println("after next:" + set.toString());
            it.remove();
        }
        assertEquals(0, set.size());
    }
}
