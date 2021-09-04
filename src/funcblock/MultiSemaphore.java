//*****************************************************
// Author: Dmitry Blyakhman, Yakov Starikov, 2000-2020
//*****************************************************
package funcblock;

import java.util.concurrent.atomic.AtomicInteger;

public class MultiSemaphore {

    private AtomicInteger events;

    public MultiSemaphore() {
        events = new AtomicInteger(0);
    }

    public synchronized void take() {
        events.incrementAndGet();
        notify();
    }

    public synchronized void release() {
        //if (events.get() > 0) {
        events.decrementAndGet();
        notify();
        //}
    }

    public synchronized void waiting() throws InterruptedException {
        while (events.get() <= 0) {
            wait();
        }
    }

    public synchronized void add(int delta) {
        events.addAndGet(delta);
//        if (events.get() < 0) {
//            events.set(0);
//        }
    }

    public synchronized int get() {
        return events.get();
    }
}
