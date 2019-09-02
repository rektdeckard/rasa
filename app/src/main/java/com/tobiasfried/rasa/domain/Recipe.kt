package com.tobiasfried.rasa.domain

import java.util.ArrayList

import com.tobiasfried.rasa.constants.*

class Recipe {

    // MEMBER FIELDS
    // GETTERS

    // SETTERS

    var name: String? = null

    private var teas: MutableList<Ingredient> = ArrayList()

    var primarySweetener = 0
    var primarySweetenerAmount = 75

    var secondarySweetener = 0
    var secondarySweetenerAmount = 0

    var water = 1.0

    private var ingredients: MutableList<Ingredient> = ArrayList()

    var notes: String? = null

    // CONSTRUCTORS

    /**
     * Constructor for programmatic use
     *
     * @param name                     Recipe name
     * @param teas                      Tea [Ingredient]
     * @param primarySweetener         [Ingredient]
     * @param primarySweetenerAmount   in grams
     * @param secondarySweetener       [Ingredient]
     * @param secondarySweetenerAmount in grams
     * @param ingredients              ArrayList<[Ingredient]>
     */
    constructor(name: String, teas: List<Ingredient>, primarySweetener: Int,
                primarySweetenerAmount: Int, secondarySweetener: Int,
                secondarySweetenerAmount: Int, water: Double, ingredients: List<Ingredient>, notes: String) {
        this.name = name

        for (i in teas) {
            if (i.type == IngredientType.TEA) {
                this.teas.add(i)
            } else
                throw IllegalArgumentException("Invalid tea ingredient")
        }

        if (primarySweetener >= 0 && primarySweetener <= 5) {
            this.primarySweetener = primarySweetener
        } else
            throw IllegalArgumentException("Invalid sweetener ingredient")

        this.primarySweetenerAmount = primarySweetenerAmount

        if (secondarySweetener >= 0 && secondarySweetener <= 5) {
            this.secondarySweetener = secondarySweetener
        } else
            throw IllegalArgumentException("Invalid sweetener ingredient")

        this.secondarySweetenerAmount = secondarySweetenerAmount

        this.water = water

        for (i in ingredients) {
            if (i.type == IngredientType.FLAVOR) {
                this.ingredients.add(i)
            } else
                throw IllegalArgumentException("Invalid flavor ingredient")
        }

        this.notes = notes
    }

    /**
     * Required empty constructor for Firestore & Entry activity
     */
    constructor() {

    }

    fun getTeas(): List<Ingredient> {
        return teas
    }

    fun getIngredients(): List<Ingredient> {
        return ingredients
    }

    fun setTeas(teas: MutableList<Ingredient>) {
        this.teas = teas
    }

    fun addTea(tea: Ingredient) {
        this.teas.add(tea)
    }

    fun removeTea(tea: Ingredient) {
        this.teas.remove(tea)
    }

    fun addIngredient(ingredient: Ingredient) {
        ingredients.add(ingredient)
    }

    fun removeIngredient(ingredient: Ingredient) {
        ingredients.remove(ingredient)
    }

    fun setIngredients(ingredients: MutableList<Ingredient>) {
        this.ingredients = ingredients
    }

    companion object {

        val COLLECTION = "recipes"
    }
}
