//*****************************************************
// Author: Dmitry Blyakhman, Yakov Starikov, 2000-2020
//*****************************************************
package example;

import exception.FuncBlockException;
import funcblock.FBEvent;
import funcblock.FBExecutor;
import funcblock.FBTimerType;
import funcblock.FuncBlock;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FastFb extends FuncBlock {

    static final int STATE_IDLE = 0;
    static final int STATE_START = 1;
    static final int STATE_WORK = 2;

    private int state;

    public FastFb(FBExecutor executor, String name) {
        super(executor, name);
        state = STATE_IDLE;
    }

    @Override
    public void task(FBEvent event) {
        FbMsg msg = null;

        if (event instanceof FbMsg) {
            msg = (FbMsg) event;
        }

        switch (state) {
            case STATE_IDLE:
                switch (msg.getFbEvent()) {
                    case EVENT_START:
                        System.out.println("fastFuncBlock is started...");
                        executor.startTimer(this.getName(), FBTimerType.ONE_TIME, new FbData(FbEvents.EVENT_DATA, "fastFuncBlock timer is expires..."), 5000);
                        state = STATE_START;
                        break;
                }
                break;
            case STATE_START:
                switch (msg.getFbEvent()) {
                    case EVENT_DATA:
                        System.out.println(((FbData) msg).getText());
                        TmpFb tmpFb = new TmpFb(executor, genFbName());

                        try {
                            executor.add(tmpFb);
                        } catch (FuncBlockException ex) {
                            Logger.getLogger(FastFb.class.getName()).log(Level.SEVERE, null, ex);
                        }

                        tmpFb.put(new TmpEvent(1000));
                        state = STATE_WORK;
                        break;
                }
                break;
            case STATE_WORK:
                FuncBlock fb = executor.get("slowFuncBlock");
                if (fb != null) {
                    fb.put(event);
                }
                break;
        }
    }

    public static String genFbName() {
        String tagStr = "";
        char asciiTable[] = {'a', 'b', 'c', 'd', 'e', 'f',
            '1', '2', '3', '4', '5', '6', '7', '8', '9', '0'};

        for (int i = 0; i < 8; i++) {
            tagStr += asciiTable[(int) (Math.random() * 16)];
        }
        return tagStr;
    }
}
