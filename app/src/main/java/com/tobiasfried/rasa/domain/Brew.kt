package com.tobiasfried.rasa.domain

import com.google.firebase.firestore.ServerTimestamp
import com.tobiasfried.rasa.constants.*


class Brew {


    // MEMBER FIELDS
    //    private void setEndDate(long endDate) {
    //        switch (stage) {
    //            case PRIMARY:
    //            case PAUSED:
    //                if (primaryFerment != null) {
    //                    primaryFerment.second = endDate;
    //                }
    //            case SECONDARY:
    //            case COMPLETE:
    //                if (secondaryFerment != null) {
    //                    secondaryFerment.second = endDate;
    //                }
    //            default:
    //                throw new NullPointerException("No appropriate stage found.");
    //        }
    //    }

    // SETTERS & GETTERS

    var recipe: Recipe? = null
    var stage: Stage? = null
    var primaryFerment: Ferment? = null
    var secondaryFerment: Ferment? = null

    @ServerTimestamp
    private val serverTimestamp: Long = 0

    val startDate: Long
        get() {
            when (stage) {
                Stage.PRIMARY, Stage.PAUSED -> {
                    if (primaryFerment!!.first != null) {
                        return primaryFerment!!.first!!
                    }
                    if (secondaryFerment!!.first != null) {
                        return secondaryFerment!!.first!!
                    }
                    throw NullPointerException("No appropriate stage found.")
                }
                Stage.SECONDARY, Stage.COMPLETE -> {
                    if (secondaryFerment!!.first != null) {
                        return secondaryFerment!!.first!!
                    }
                    throw NullPointerException("No appropriate stage found.")
                }
                else -> throw NullPointerException("No appropriate stage found.")
            }
        }

    //    private void setStartDate(long startDate) {
    //        switch (stage) {
    //            case PRIMARY:
    //            case PAUSED:
    //                if (primaryFerment != null) {
    //                    primaryFerment.first = startDate;
    //                }
    //            case SECONDARY:
    //            case COMPLETE:
    //                if (secondaryFerment != null) {
    //                    secondaryFerment.first = startDate;
    //                }
    //            default:
    //                throw new NullPointerException("No appropriate stage found.");
    //        }
    //    }

    val endDate: Long
        get() {
            when (stage) {
                Stage.PRIMARY, Stage.PAUSED -> {
                    if (primaryFerment!!.second != null) {
                        return primaryFerment!!.second!!
                    }
                    if (secondaryFerment != null && secondaryFerment!!.second != null) {
                        return secondaryFerment!!.second!!
                    }
                    throw NullPointerException("No appropriate stage found.")
                }
                Stage.SECONDARY, Stage.COMPLETE -> {
                    if (secondaryFerment != null && secondaryFerment!!.second != null) {
                        return secondaryFerment!!.second!!
                    }
                    throw NullPointerException("No appropriate stage found.")
                }
                else -> throw NullPointerException("No appropriate stage found.")
            }
        }

    // CONSTRUCTORS

    /**
     * Constructor to start from recipe
     *
     * @param recipe from recipe
     */
    constructor(recipe: Recipe) {
        this.recipe = recipe
    }

    /**
     * Empty constructor for editor activity
     */
    constructor() {
        this.recipe = Recipe()
    }

    // METHODS

    fun advanceStage(): Boolean {
        when (stage) {
            Stage.PRIMARY -> {
                stage = Stage.PAUSED
                // TODO use method to update 1F end and 2F start simultaneously
                primaryFerment!!.second = System.currentTimeMillis()
                secondaryFerment!!.first = System.currentTimeMillis()
            }
            Stage.PAUSED -> if (secondaryFerment != null) {
                stage = Stage.SECONDARY
                primaryFerment!!.second = System.currentTimeMillis()
                secondaryFerment!!.first = System.currentTimeMillis()
            } else {
                return false
            }
            Stage.SECONDARY -> {
                stage = Stage.COMPLETE
                // TODO also here
                secondaryFerment!!.second = System.currentTimeMillis()
            }
            Stage.COMPLETE -> {
                secondaryFerment!!.second = System.currentTimeMillis()
                return false
            }
            else -> {
                secondaryFerment!!.second = System.currentTimeMillis()
                return false
            }
        }
        return true
    }

    companion object {

        const val CURRENT = "brews"
        const val HISTORY = "history"
    }
}
