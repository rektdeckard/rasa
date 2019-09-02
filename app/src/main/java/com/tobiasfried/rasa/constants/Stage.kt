package com.tobiasfried.rasa.constants

enum class Stage private constructor(val code: Int) {

    PAUSED(-1),
    COMPLETE(0),
    PRIMARY(1),
    SECONDARY(2),
    UPCOMING(9)
}
