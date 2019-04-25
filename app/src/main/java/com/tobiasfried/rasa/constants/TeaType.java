package com.tobiasfried.rasa.constants;

public enum TeaType {

    WHITE(0),
    GREEN(1),
    OOLONG(2),
    BLACK(3),
    PUERH(4),
    HERBAL(5),
    OTHER(6);

    private final int code;

    TeaType(int code) {
        this.code = code;
    }

    public int getCode() {
        return this.code;
    }

    public static TeaType get(int i) {
        for (TeaType type : TeaType.values()) {
            if (type.code == i) return type;
        }
        throw new IllegalArgumentException("That tea type does not exist.");
    }

}
