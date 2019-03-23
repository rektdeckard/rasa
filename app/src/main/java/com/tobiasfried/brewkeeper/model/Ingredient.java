package com.tobiasfried.brewkeeper.model;

import com.tobiasfried.brewkeeper.constants.*;

public class Ingredient {

    public static final String LOG_TAG = Ingredient.class.getSimpleName();
    public static final String COLLECTION = "ingredients";

    // MEMBER FIELDS
    private String name;
    private IngredientType type;
    private TeaType teaType;
    private int amount = 10;


    // CONSTRUCTORS
    /**
     * Constructor for programmatic use
     * @param name Ingredient name
     * @param type {@link IngredientType}
     * @param teaType {@link TeaType}
     */
    public Ingredient(String name, IngredientType type, TeaType teaType, int amount) {
        this.name = name;
        this.type = type;
        this.teaType = teaType;
        this.amount = amount;
    }

    /**
     * Empty constructor for Firestore & Entry activity
     */
    public Ingredient() {
    }

    // GETTERS

    public String getName() {
        return name;
    }

    public IngredientType getType() {
        return type;
    }

    public TeaType getTeaType() {
        return this.type == IngredientType.TEA ? teaType : null;
    }

    public int getAmount() {
        return this.amount;
    }


    // SETTERS

    public void setName(String name) {
        this.name = name;
    }

    public void setType(IngredientType type) {
        this.type = type;
    }

    public void setTeaType(TeaType teaType) {
        this.teaType = teaType;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

}
