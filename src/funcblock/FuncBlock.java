package funcblock;

import exception.MailboxFullException;
import static funcblock.MultiThreadFBExecutor.eventSemaphore;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class FuncBlock implements Runnable {

    protected final String name;

    protected final FBExecutor executor;

    public final ArrayBlockingQueue<FBEvent> mailbox;

    protected AtomicBoolean markedForDelete;

    public AtomicBoolean executeLock;
    public AtomicBoolean executeTaskLock;

    public FuncBlock(FBExecutor executor, String name, int mailboxSize) {
        this.name = name;
        this.executor = executor;
        this.mailbox = new ArrayBlockingQueue(mailboxSize);
        this.markedForDelete = new AtomicBoolean(false);
        this.executeLock = new AtomicBoolean(false);
        this.executeTaskLock = new AtomicBoolean(false);
    }

    public FuncBlock(FBExecutor executor, String name) {
        this.name = name;
        this.executor = executor;
        this.mailbox = new ArrayBlockingQueue(1000);
        this.markedForDelete = new AtomicBoolean(false);
        this.executeLock = new AtomicBoolean(false);
        this.executeTaskLock = new AtomicBoolean(false);
    }

    @Override
    public void run() {
        try {
            while (!mailbox.isEmpty() && !isMarkedForDelete()) {
                while (MultiThreadFBExecutor.isStopSystem.get()) {
                    Thread.sleep(10);
                }
                FBEvent event = mailbox.take();
                try {
                    executeTaskLock.set(true);
                    task(event);
                } finally {
                    executeTaskLock.set(false);
                    eventSemaphore.release();
                }
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(FuncBlock.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            executeLock.set(false);
        }
    }

    public abstract void task(FBEvent event);

    public String getName() {
        return name;
    }

    public void offer(FBEvent event) {
        if (!this.isMarkedForDelete()) {
            if (!mailbox.offer(event)) {
                try {
                    throw new MailboxFullException();
                } catch (MailboxFullException ex) {
                    Logger.getLogger(FuncBlock.class.getName()).log(Level.SEVERE, name + ":" + ex, ex);
                }
            } else {
                eventSemaphore.take();
            }
        }
    }

    public void put(FBEvent event) {
        if (mailbox.remainingCapacity() == 0) {
            try {
                throw new MailboxFullException();
            } catch (MailboxFullException ex) {
                Logger.getLogger(FuncBlock.class.getName()).log(Level.SEVERE, name + ":" + ex, ex);
            }
        }

        try {
            if (!this.isMarkedForDelete()) {
                mailbox.put(event);
                eventSemaphore.take();
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(FuncBlock.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean isEmpty() {
        return mailbox.isEmpty();
    }

    public boolean isMarkedForDelete() {
        return markedForDelete.get();
    }

    public void markForDelete() {
        this.markedForDelete.compareAndSet(false, true);
    }

    public FBTimer startTimer(FBTimerType type, FBEvent callbackEvent, int expires) {
        FBTimer timer = executor.startTimer(getName(), type, callbackEvent, expires);
        return timer;
    }

    public void deleteTimer(FBTimer timer) {
        executor.deleteTimer(timer);
    }

    public boolean catchExecuteLock() {
        return executeLock.compareAndSet(false, true);
    }

}
