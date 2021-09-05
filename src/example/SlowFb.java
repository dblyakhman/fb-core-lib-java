package example;

import funcblock.FBEvent;
import funcblock.FBExecutor;
import funcblock.FuncBlock;

public class SlowFb extends FuncBlock {

    private int i;
    private long timeStamp;

    public SlowFb(FBExecutor executor, String name) {
        super(executor, name);
        i = 0;
        timeStamp = 0;
    }

    @Override
    public void task(FBEvent event) {

        executor.put("fastFuncBlock", event);

        if (System.currentTimeMillis() - timeStamp > 1000) {
            timeStamp = System.currentTimeMillis();
            System.out.println("SlowEvents count per sec: " + i);
            i = 0;
        }
        i++;
    }
}
