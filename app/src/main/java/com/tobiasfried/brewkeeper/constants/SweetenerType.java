package com.tobiasfried.brewkeeper.constants;

public enum SweetenerType {

    WHITE_SUGAR(0),
    RAW_SUGAR(1),
    BROWN_SUGAR(2),
    AGAVE(3),
    HONEY(4),
    OTHER(5);

    private final int code;

    SweetenerType(int code) {
        this.code = code;
    }

    public int getCode() {
        return this.code;
    }
    public static SweetenerType get(int i) {
        for (SweetenerType type : SweetenerType.values()) {
            if (type.code == i) return type;
        }
        throw new IllegalArgumentException("That sweetener type does not exist.");
    }

}
