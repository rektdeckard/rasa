package com.tobiasfried.brewkeeper.constants;

public enum Stage {

    PRIMARY(1),
    SECONDARY(2);

    private int code;

    Stage(int code) {
        this.code = code;
    }

    public int getCode() {
        return this.code;
    }
}
