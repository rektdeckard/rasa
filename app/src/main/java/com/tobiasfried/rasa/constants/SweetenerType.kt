package com.tobiasfried.rasa.constants

enum class SweetenerType private constructor(val code: Int) {

    WHITE_SUGAR(0),
    RAW_SUGAR(1),
    BROWN_SUGAR(2),
    AGAVE(3),
    HONEY(4),
    OTHER(5);


    companion object {

        operator fun get(i: Int): SweetenerType {
            for (type in SweetenerType.values()) {
                if (type.code == i) return type
            }
            throw IllegalArgumentException("That sweetener type does not exist.")
        }
    }

}
