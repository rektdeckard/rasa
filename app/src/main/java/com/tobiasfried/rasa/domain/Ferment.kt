package com.tobiasfried.rasa.domain

class Ferment {

    var first: Long? = null
    var second: Long? = null

    /**
     * Constructor for a Pair.
     *
     * @param first  the first object in the Pair
     * @param second the second object in the pair
     */
    constructor(first: Long?, second: Long?) {
        this.first = first
        this.second = second
    }

    /**
     * No-argument constructor for Firestore
     */
    constructor() {

    }
}
