package com.tobiasfried.brewkeeper.model;

import java.util.ArrayList;
import java.util.List;

import com.tobiasfried.brewkeeper.constants.*;

public class Recipe {

    public static final String COLLECTION = "recipes";

    // MEMBER FIELDS
    private String name;

    private List<Ingredient> teas = new ArrayList<>();

    private int primarySweetener = 0;
    private int primarySweetenerAmount = 75;

    private int secondarySweetener = 0;
    private int secondarySweetenerAmount = 0;

    private double water = 1.0;

    private List<Ingredient> ingredients = new ArrayList<>();

    private String notes;

    // CONSTRUCTORS

    /**
     * Constructor for programmatic use
     *
     * @param name                     Recipe name
     * @param teas                      Tea {@link Ingredient}
     * @param primarySweetener         {@link Ingredient}
     * @param primarySweetenerAmount   in grams
     * @param secondarySweetener       {@link Ingredient}
     * @param secondarySweetenerAmount in grams
     * @param ingredients              ArrayList<{@link Ingredient}>
     */
    public Recipe(String name, List<Ingredient> teas, int primarySweetener,
                  int primarySweetenerAmount, int secondarySweetener,
                  int secondarySweetenerAmount, double water, List<Ingredient> ingredients, String notes) {
        this.name = name;

        for(Ingredient i : teas) {
            if (i.getType() == IngredientType.TEA) {
                this.teas.add(i);
            } else throw new IllegalArgumentException("Invalid tea ingredient");
        }

        if (primarySweetener >= 0 && primarySweetener <= 5) {
            this.primarySweetener = primarySweetener;
        } else throw new IllegalArgumentException("Invalid sweetener ingredient");

        this.primarySweetenerAmount = primarySweetenerAmount;

        if (secondarySweetener >= 0 && secondarySweetener <= 5) {
            this.secondarySweetener = secondarySweetener;
        } else throw new IllegalArgumentException("Invalid sweetener ingredient");

        this.secondarySweetenerAmount = secondarySweetenerAmount;

        this.water = water;

        for (Ingredient i : ingredients) {
            if (i.getType() == IngredientType.FLAVOR) {
                this.ingredients.add(i);
            } else throw new IllegalArgumentException("Invalid flavor ingredient");
        }

        this.notes = notes;
    }

    /**
     * Required empty constructor for Firestore & Entry activity
     */
    public Recipe() {

    }

    // GETTERS

    public String getName() {
        return name;
    }

    public List<Ingredient> getTeas() {
        return teas;
    }

    public int getPrimarySweetener() {
        return primarySweetener;
    }

    public int getPrimarySweetenerAmount() {
        return primarySweetenerAmount;
    }

    public int getSecondarySweetener() {
        return secondarySweetener;
    }

    public int getSecondarySweetenerAmount() {
        return secondarySweetenerAmount;
    }

    public double getWater() {
        return water;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public String getNotes() {
        return notes;
    }

    // SETTERS

    public void setName(String name) {
        this.name = name;
    }

    public void setTeas(List<Ingredient> teas) {
        this.teas = teas;
    }

    public void addTea(Ingredient tea) {
        this.teas.add(tea);
    }

    public void removeTea(Ingredient tea) {
        this.teas.remove(tea);
    }

    public void setPrimarySweetener(int primarySweetener) {
        this.primarySweetener = primarySweetener;
    }

    public void setPrimarySweetenerAmount(int primarySweetenerAmount) {
        this.primarySweetenerAmount = primarySweetenerAmount;
    }

    public void setSecondarySweetener(int secondarySweetener) {
        this.secondarySweetener = secondarySweetener;
    }

    public void setSecondarySweetenerAmount(int secondarySweetenerAmount) {
        this.secondarySweetenerAmount = secondarySweetenerAmount;
    }

    public void setWater(double water) {
        this.water = water;
    }

    public void addIngredient(Ingredient ingredient) {
        ingredients.add(ingredient);
    }

    public void removeIngredient(Ingredient ingredient) {
        ingredients.remove(ingredient);
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
