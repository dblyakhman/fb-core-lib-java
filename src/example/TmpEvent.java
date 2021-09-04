//*****************************************************
// Author: Dmitry Blyakhman, Yakov Starikov, 2000-2020
//*****************************************************
package example;

import funcblock.FBEvent;

public class TmpEvent extends FBEvent {

    private int number;

    public TmpEvent(int number) {
        this.number = number;
    }

    public int getNumber() {
        return number;
    }
}
