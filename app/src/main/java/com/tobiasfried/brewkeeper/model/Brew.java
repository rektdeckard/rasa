package com.tobiasfried.brewkeeper.model;

import java.time.LocalDate;
import java.util.List;

import com.tobiasfried.brewkeeper.constants.*;

public class Brew {

    private static final String LOG_TAG = Brew.class.getSimpleName();
    public static final String COLLECTION = "brews";

    // MEMBER FIELDS
    private Recipe recipe;

    private long primaryStartDate;
    private long secondaryStartDate;
    private long endDate;

    private Stage stage;
    private boolean isRunning;


    // CONSTRUCTORS

    /**
     * Constructor for programmatic use
     *
     * @param name                     Brew name
     * @param tea                      Tea {@link Ingredient}
     * @param teaAmount                in grams
     * @param primarySweetener         Sweetener {@link Ingredient}
     * @param primarySweetenerAmount   in grams
     * @param secondarySweetener       Sweetener {@link Ingredient}
     * @param secondarySweetenerAmount in grams
     * @param ingredients              ArrayList<{@link Ingredient}>
     * @param primaryStartDate         UNIX Timestamp
     * @param secondaryStartDate       UNIX Timestamp
     * @param endDate                  UNIX Timestamp
     * @param stage                    Stage {@link Stage}
     * @param isRunning                boolean
     */
    public Brew(String name, Ingredient tea, int teaAmount,
                int primarySweetener, int primarySweetenerAmount,
                int secondarySweetener, int secondarySweetenerAmount,
                List<Ingredient> ingredients,
                long primaryStartDate, long secondaryStartDate, long endDate,
                Stage stage, boolean isRunning) {
        this.recipe = new Recipe(name, tea, teaAmount, primarySweetener, primarySweetenerAmount,
                secondarySweetener, secondarySweetenerAmount, ingredients);
        this.primaryStartDate = primaryStartDate;
        this.secondaryStartDate = secondaryStartDate;
        this.endDate = endDate;
        this.stage = stage;
        this.isRunning = isRunning;
    }

    /**
     * Constructor to start from recipe
     *
     * @param recipe from recipe
     * @param primaryStartDate         {@link LocalDate}
     * @param secondaryStartDate       {@link LocalDate}
     * @param endDate                  {@link LocalDate}
     * @param stage                    Stage {@link Stage}
     * @param isRunning                boolean
     */
    public Brew(Recipe recipe,
                long primaryStartDate, long secondaryStartDate, long endDate,
                Stage stage, boolean isRunning) {
        this.recipe = recipe;
        this.primaryStartDate = primaryStartDate;
        this.secondaryStartDate = secondaryStartDate;
        this.endDate = endDate;
        this.stage = stage;
        this.isRunning = isRunning;
    }

    /**
     * Empty constructor for editor activity
     */
    public Brew() {
        this.recipe = new Recipe();
        this.stage = Stage.PRIMARY;
        this.isRunning = false;
    }

    // GETTERS

    public Recipe getRecipe() {
        return recipe;
    }

    public long getPrimaryStartDate() {
        return primaryStartDate;
    }

    public long getSecondaryStartDate() {
        return secondaryStartDate;
    }

    public long getEndDate() {
        return this.endDate;
    }

    public Stage getStage() {
        return stage;
    }

    public boolean isRunning() {
        return isRunning;
    }


    // SETTERS

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

    public void setPrimaryStartDate(long primaryStartDate) {
        this.primaryStartDate = primaryStartDate;
    }

    public void setSecondaryStartDate(long secondaryStartDate) {
        this.secondaryStartDate = secondaryStartDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }

}
