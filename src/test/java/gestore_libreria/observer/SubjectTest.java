package gestore_libreria.observer;

import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;

import java.util.Observable;
import java.util.Observer;

import static org.junit.Assert.*;
public class SubjectTest {

    private Subject subject;
    private TestObserver observer1;
    private TestObserver observer2;

    private static class ConcreteTestSubject extends Subject {}

    private static class TestObserver implements BookObserver {

        private int updateCount = 0;

        @Override
        public void update() {
            updateCount++;
        }

        public int getUpdateCount() {
            return updateCount;
        }
    }

    @Before
    public void setUp() {
        subject = new ConcreteTestSubject();
        observer1 = new TestObserver();
        observer2 = new TestObserver();
    }

    @Test
    public void testAttach() {
        subject.attach(observer1);
        subject.attach(observer2);

        subject.notifyObservers();
        assertEquals(1, observer1.getUpdateCount());
        assertEquals(1, observer2.getUpdateCount());
    }

    @Test
    public void testAttachDuplicate() {
        subject.attach(observer1);
        subject.attach(observer1);

        subject.notifyObservers();

        assertEquals(1, observer1.getUpdateCount());
    }

    @Test
    public void testDetach() {
        subject.attach(observer1);
        subject.attach(observer2);

        subject.detach(observer1);
        subject.notifyObservers();
        assertEquals(0, observer1.getUpdateCount());
        assertEquals(1, observer2.getUpdateCount());
    }
}