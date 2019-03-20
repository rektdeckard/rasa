package com.tobiasfried.brewkeeper.model;

import java.util.ArrayList;
import java.util.List;

import com.tobiasfried.brewkeeper.constants.*;

public class Recipe {

    private static final String LOG_TAG = Recipe.class.getSimpleName();
    public static final String COLLECTION = "recipes";

    // MEMBER FIELDS
    private String name;

    private Ingredient tea;
    private int teaAmount;

    private int primarySweetener;
    private int primarySweetenerAmount;

    private int secondarySweetener;
    private int secondarySweetenerAmount;

    private List<Ingredient> ingredients;

    // CONSTRUCTORS

    /**
     * Constructor for programmatic use
     *
     * @param name                     Recipe name
     * @param tea                      Tea {@link Ingredient}
     * @param teaAmount                in grams
     * @param primarySweetener         {@link Ingredient}
     * @param primarySweetenerAmount   in grams
     * @param secondarySweetener       {@link Ingredient}
     * @param secondarySweetenerAmount in grams
     * @param ingredients              ArrayList<{@link Ingredient}>
     */
    public Recipe(String name, Ingredient tea, int teaAmount, int primarySweetener,
                  int primarySweetenerAmount, int secondarySweetener,
                  int secondarySweetenerAmount, List<Ingredient> ingredients) {
        this.name = name;

        if (tea.getType() == IngredientType.TEA) {
            this.tea = tea;
        } else throw new IllegalArgumentException("Invalid tea ingredient");

        this.teaAmount = teaAmount;

        if (primarySweetener >= 0 && primarySweetener <= 5) {
            this.primarySweetener = primarySweetener;
        } else throw new IllegalArgumentException("Invalid sweetener ingredient");

        this.primarySweetenerAmount = primarySweetenerAmount;

        if (secondarySweetener >= 0 && secondarySweetener <= 5) {
            this.secondarySweetener = secondarySweetener;
        } else throw new IllegalArgumentException("Invalid sweetener ingredient");

        this.secondarySweetenerAmount = secondarySweetenerAmount;

        this.ingredients = new ArrayList<>();
        for (Ingredient ingredient : ingredients) {
            if (ingredient.getType() == IngredientType.FLAVOR) {
                this.ingredients.add(ingredient);
            } else throw new IllegalArgumentException("Invalid flavor ingredient");
        }
    }

    /**
     * Required empty constructor for Firestore & Entry activity
     */
    public Recipe() {
        teaAmount = 50;
        primarySweetener = 0;
        primarySweetenerAmount = 100;
        secondarySweetener = 0;
        secondarySweetenerAmount = 50;
        ingredients = new ArrayList<>();
    }

    // GETTERS

    public String getName() {
        return name;
    }

    public Ingredient getTea() {
        return tea;
    }

    public int getTeaAmount() {
        return teaAmount;
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

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    // SETTERS

    public void setName(String name) {
        this.name = name;
    }

    public void setTea(Ingredient tea) {
        this.tea = tea;
    }

    public void setTeaAmount(int teaAmount) {
        this.teaAmount = teaAmount;
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

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }
}
