package com.tobiasfried.brewkeeper.model;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAmount;
import java.util.List;

import com.tobiasfried.brewkeeper.constants.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class Brew {

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
     * @param teas                      Tea {@link Ingredient}
     * @param primarySweetener         Sweetener {@link Ingredient}
     * @param primarySweetenerAmount   in grams
     * @param secondarySweetener       Sweetener {@link Ingredient}
     * @param secondarySweetenerAmount in grams
     * @param water                    in Liters
     * @param ingredients              ArrayList<{@link Ingredient}>
     * @param primaryStartDate         UNIX Timestamp
     * @param secondaryStartDate       UNIX Timestamp
     * @param endDate                  UNIX Timestamp
     * @param stage                    Stage {@link Stage}
     * @param isRunning                boolean
     */
    public Brew(@NonNull String name, @NonNull List<Ingredient> teas,
                int primarySweetener, int primarySweetenerAmount,
                int secondarySweetener, int secondarySweetenerAmount,
                double water, @Nullable List<Ingredient> ingredients, @Nullable String notes,
                long primaryStartDate, long secondaryStartDate, long endDate,
                @Nullable Stage stage, boolean isRunning) {
        this.recipe = new Recipe(name, teas, primarySweetener, primarySweetenerAmount,
                secondarySweetener, secondarySweetenerAmount, water, ingredients, notes);
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
    public Brew(@NonNull Recipe recipe,
                long primaryStartDate, long secondaryStartDate, long endDate,
                @Nullable Stage stage, boolean isRunning) {
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
        recipe = new Recipe();
        primaryStartDate = Instant.now().toEpochMilli();
        secondaryStartDate = ZonedDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()).plusDays(10).toEpochSecond() * 1000;
        endDate = ZonedDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()).plusDays(12).toEpochSecond() * 1000;
        stage = Stage.PRIMARY;
        isRunning = false;
    }

    // GETTERS

    public @NonNull Recipe getRecipe() {
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

    public @Nullable Stage getStage() {
        return stage;
    }

    public boolean isRunning() {
        return isRunning;
    }


    // SETTERS

    public void setRecipe(@NonNull Recipe recipe) {
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

    public void setStage(@Nullable Stage stage) {
        this.stage = stage;
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }

}
