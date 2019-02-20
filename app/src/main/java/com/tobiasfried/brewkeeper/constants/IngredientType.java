package com.tobiasfried.brewkeeper.constants;

public enum IngredientType {

    TEA(0),
    SWEETENER(1),
    FLAVOR(2);

    private final int code;

    IngredientType(int code) {
        this.code = code;
    }

    public int getCode() {
        return this.code;
    }

    public static IngredientType get(int i) {
        for (IngredientType type : IngredientType.values()) {
            if (type.code == i) return type;
        }
        throw new IllegalArgumentException("That ingredient type does not exist.");
    }

}
