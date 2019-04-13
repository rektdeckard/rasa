package com.tobiasfried.brewkeeper.model;

public class Ferment {

    public Long first;
    public Long second;

    /**
     * Constructor for a Pair.
     *
     * @param first  the first object in the Pair
     * @param second the second object in the pair
     */
    public Ferment(Long first, Long second) {
        this.first = first;
        this.second = second;
    }

    /**
     * No-argument constructor for Firestore
     */
    public Ferment() {

    }
}
