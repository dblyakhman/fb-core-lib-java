//*****************************************************
// Author: Dmitry Blyakhman, Yakov Starikov, 2000-2020
//*****************************************************
package example;

import funcblock.FBEvent;

public class FbMsg extends FBEvent {

    private FbEvents fbEvent;

    public FbMsg(FbEvents event) {
        this.fbEvent = event;
    }

    public FbMsg() {
        this.fbEvent = FbEvents.EVENT_EMPTY;
    }

    public FbEvents getFbEvent() {
        return fbEvent;
    }
}
