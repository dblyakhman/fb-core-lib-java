//*****************************************************
// Author: Dmitry Blyakhman, Yakov Starikov, 2000-2020
//*****************************************************
package funcblock;

import java.util.concurrent.atomic.AtomicBoolean;

public class FBTimer {

    private final String name;

    private final FBTimerType type;

    private final int expires;

    private final FBEvent callbackEvent;

    private long startTime;

    private AtomicBoolean markedForDelete;

    FBTimer(String name, FBTimerType type, FBEvent callbackEvent, int expires) {
        this.name = name;
        this.type = type;
        this.expires = expires;
        this.callbackEvent = callbackEvent;
        this.startTime = System.currentTimeMillis();
        this.markedForDelete = new AtomicBoolean(false);
    }

    public String getName() {
        return name;
    }

    public FBTimerType getType() {
        return type;
    }

    public int getExpires() {
        return expires;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public FBEvent getCallbackEvent() {
        return callbackEvent;
    }

    public boolean isMarkedForDelete() {
        return markedForDelete.get();
    }

    public void setMarkedForDelete(boolean markedForDelete) {
        this.markedForDelete.set(markedForDelete);
    }
}
