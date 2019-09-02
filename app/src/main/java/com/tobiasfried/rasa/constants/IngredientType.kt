package com.tobiasfried.rasa.constants

enum class IngredientType private constructor(val code: Int) {

    TEA(0),
    SWEETENER(1),
    FLAVOR(2);


    companion object {

        operator fun get(i: Int): IngredientType {
            for (type in IngredientType.values()) {
                if (type.code == i) return type
            }
            throw IllegalArgumentException("That ingredient type does not exist.")
        }
    }

}
