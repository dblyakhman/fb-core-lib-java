package funcblock;

import exception.FuncBlockException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class FBExecutor extends Thread {

    public final Map<String, FuncBlock> funcBlocks;

    public final List<FBTimer> timers;

    protected final Thread thread;

    public FBExecutor() {
        funcBlocks = new ConcurrentHashMap<>();
        timers = new CopyOnWriteArrayList<>();
        thread = new Thread(this);
    }

    public void add(FuncBlock fb) throws FuncBlockException {
        if (funcBlocks.containsKey(fb.getName())) {
            //throw new FuncBlockException();
            return;
        }

        funcBlocks.put(fb.getName(), fb);
    }

    public FuncBlock get(String name) {
        FuncBlock fb;
        fb = funcBlocks.get(name);

        if (fb != null && fb.isMarkedForDelete()) {
            fb = null;
        }

        return fb;
    }

    public void markForDelete(String name) {
        FuncBlock f = get(name);

        if (f != null) {
            f.markForDelete();
        }
    }

    public void offer(String name, FBEvent event) {
        FuncBlock f = get(name);

        if (f != null) {
            f.offer(event);
        }
    }

    public void put(String name, FBEvent event) {
        FuncBlock f = get(name);

        if (f != null) {
            f.put(event);
        }
    }

    public FBTimer startTimer(String name, FBTimerType type, FBEvent callbackEvent, int expires) {
        FuncBlock f = get(name);

        if (f != null) {
            FBTimer timer = new FBTimer(name, type, callbackEvent, expires);
            timers.add(timer);
            return timer;
        }

        return null;
    }

    public void deleteTimer(FBTimer timer) {
        if (timers.contains(timer)) {
            timer.setMarkedForDelete(true);
        }
    }

    public void calculate() {
        List<FBTimer> markedFromDelete = new ArrayList<>();

        for (FBTimer t : timers) {
            if (t.isMarkedForDelete()) {
                markedFromDelete.add(t);
                continue;
            }
            if ((System.currentTimeMillis() - t.getStartTime()) > t.getExpires()) {
                FuncBlock f = get(t.getName());
                if (f != null) {
                    f.put(t.getCallbackEvent());
                } else {
                    markedFromDelete.add(t);
                    continue;
                }

                t.setStartTime(System.currentTimeMillis());
                if (t.getType() == FBTimerType.ONE_TIME) {
                    markedFromDelete.add(t);
                }
            }
        }

        timers.removeAll(markedFromDelete);
    }

    @Override
    public abstract void run();
}
