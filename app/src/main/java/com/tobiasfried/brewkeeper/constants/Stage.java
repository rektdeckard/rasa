package com.tobiasfried.brewkeeper.constants;

public enum Stage {

    PAUSED(-1),
    COMPLETE(0),
    PRIMARY(1),
    SECONDARY(2),
    UPCOMING(9);

    private int code;

    Stage(int code) {
        this.code = code;
    }

    public int getCode() {
        return this.code;
    }
}
