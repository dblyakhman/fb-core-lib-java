//*****************************************************
// Author: Dmitry Blyakhman, Yakov Starikov, 2000-2020
//*****************************************************
package example;

import funcblock.FBEvent;
import funcblock.FBExecutor;
import funcblock.FuncBlock;

public class TmpFb extends FuncBlock {

    public TmpFb(FBExecutor executor, String name) {
        super(executor, name);
    }

    @Override
    public void task(FBEvent event) {
        System.out.println(this.getName() + " func block is created... event number = " + ((TmpEvent) event).getNumber());
        executor.put("slowFuncBlock", new FBEvent());
        this.put(new FBEvent()); //send event for self, next time not processed if fb deleted
        this.markForDelete();
        System.out.println(this.getName() + " func block is deleted...");
    }
}
