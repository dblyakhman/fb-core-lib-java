//*****************************************************
// Author: Dmitry Blyakhman, Yakov Starikov, 2000-2020
//*****************************************************
package app;

import example.FastFb;
import static example.FbEvents.EVENT_START;
import example.FbMsg;
import example.SlowFb;
import exception.FuncBlockException;
import funcblock.MultiThreadFBExecutor;

public class Main {

    public static void main(String[] args) throws InterruptedException, FuncBlockException {

        MultiThreadFBExecutor executor = new MultiThreadFBExecutor(2);

        SlowFb slowFb = new SlowFb(executor, "slowFuncBlock");
        FastFb fastFb = new FastFb(executor, "fastFuncBlock");
        executor.add(slowFb);
        executor.add(fastFb);

        executor.start();

        fastFb.put(new FbMsg(EVENT_START));

        Thread.sleep(20000);
        executor.stopSystem();
        System.out.println("system is stopped...");
        Thread.sleep(10000);
        executor.releaseSystem();
        System.out.println("system is released...");

        while (true) {
            Thread.sleep(5000);
        }
    }
}
