package com.tobiasfried.rasa.domain

import com.tobiasfried.rasa.constants.*

class Ingredient {

    // MEMBER FIELDS
    // GETTERS

    // SETTERS

    var name: String? = null
    var type: IngredientType? = null
    var teaType: TeaType? = null
        get() = if (this.type == IngredientType.TEA) field else null
    var amount = 10
    private var timesUsed = 0


    // CONSTRUCTORS
    /**
     * Constructor for programmatic use
     * @param name Ingredient name
     * @param type [IngredientType]
     * @param teaType [TeaType]
     */
    constructor(name: String, type: IngredientType, teaType: TeaType, amount: Int) {
        this.name = name
        this.type = type
        this.teaType = teaType
        this.amount = amount
    }

    /**
     * Empty constructor for Firestore & Entry activity
     */
    constructor() {}

    // METHODS
    fun incrementUsed() {
        timesUsed++
    }

    fun getTimesUsed(): Int {
        return this.timesUsed
    }

    fun setTimesUsed(timesUsed: Int) {
        if (timesUsed >= 0) {
            this.timesUsed = timesUsed
        }
    }

    companion object {

        val LOG_TAG = Ingredient::class.java.simpleName
        val COLLECTION = "ingredients"
    }

}
