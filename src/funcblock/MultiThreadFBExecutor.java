//*****************************************************
// Author: Dmitry Blyakhman, Yakov Starikov, 2000-2020
//*****************************************************
package funcblock;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MultiThreadFBExecutor extends FBExecutor {

    public static MultiSemaphore eventSemaphore = new MultiSemaphore();

    public static AtomicBoolean isStopSystem = new AtomicBoolean(false);

    private final ExecutorService executor;

    private final int samplingTimersMillis;

    public MultiThreadFBExecutor(int nThread) {
        executor = Executors.newFixedThreadPool(nThread);
        samplingTimersMillis = 100;
    }

    @Override
    public void run() {
        List<String> markedToDelete = new ArrayList<>();

        Thread timersCalculate = new Thread(new Runnable() {
            public void run() {
                while (true) {
                    calculate();
                    try {
                        Thread.sleep(samplingTimersMillis);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(MultiThreadFBExecutor.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
        timersCalculate.start();

        while (true) {
            try {
                eventSemaphore.waiting();
            } catch (InterruptedException ex) {
                Logger.getLogger(MultiThreadFBExecutor.class.getName()).log(Level.SEVERE, null, ex);
            }

            markedToDelete.clear();

            for (FuncBlock fb : funcBlocks.values()) {
                if (fb.isMarkedForDelete()) {
                    markedToDelete.add(fb.getName());
                    eventSemaphore.add(-fb.mailbox.size());
                    continue;
                }

                if (fb.catchExecuteLock()) {
                    executor.submit(fb);
                }
            }

            for (String name : markedToDelete) {
                funcBlocks.remove(name);
            }
        }
    }

    public void stopSystem() {
        isStopSystem.set(true);
        boolean isStop;
        do {
            isStop = true;
            for (FuncBlock fb : funcBlocks.values()) {
                if (fb.executeTaskLock.get()) {
                    isStop = false;
                }
            }
        } while (!isStop);
    }

    public void releaseSystem() {
        isStopSystem.set(false);
    }
}
