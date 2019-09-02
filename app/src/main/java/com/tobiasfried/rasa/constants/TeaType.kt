package com.tobiasfried.rasa.constants

enum class TeaType private constructor(val code: Int) {

    WHITE(0),
    GREEN(1),
    OOLONG(2),
    BLACK(3),
    PUERH(4),
    HERBAL(5),
    OTHER(6);


    companion object {

        operator fun get(i: Int): TeaType {
            for (type in TeaType.values()) {
                if (type.code == i) return type
            }
            throw IllegalArgumentException("That tea type does not exist.")
        }
    }

}
