package com.tobiasfried.brewkeeper.model;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import com.tobiasfried.brewkeeper.constants.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class Brew {

    public static final String CURRENT = "brews";
    public static final String HISTORY = "history";


    // MEMBER FIELDS
    private Recipe recipe;
    private List<Ferment> ferments = new ArrayList<>();

    // CONSTRUCTORS

    /**
     * Constructor for programmatic use
     *
     * @param name                      Brew name
     * @param teas                      Tea {@link Ingredient}
     * @param primarySweetener          Sweetener {@link Ingredient}
     * @param primarySweetenerAmount    in grams
     * @param secondarySweetener        Sweetener {@link Ingredient}
     * @param secondarySweetenerAmount  in grams
     * @param water                     in Liters
     * @param ingredients               ArrayList<{@link Ingredient}>
     * @param ferment                   ArrayDeque<{@link Ferment}>
     */
    public Brew(@NonNull String name, @NonNull List<Ingredient> teas,
                int primarySweetener, int primarySweetenerAmount,
                int secondarySweetener, int secondarySweetenerAmount,
                double water, @Nullable List<Ingredient> ingredients, @Nullable String notes,
                Ferment ferment) {
        this.recipe = new Recipe(name, teas, primarySweetener, primarySweetenerAmount,
                secondarySweetener, secondarySweetenerAmount, water, ingredients, notes);
        this.ferments.add(ferment);
    }

    /**
     * Constructor to start from recipe
     *
     * @param recipe                    from recipe
     * @param ferment                   ArrayDeque<{@link Ferment}>
     */
    public Brew(@NonNull Recipe recipe,
                Ferment ferment) {
        this.recipe = recipe;
        this.ferments.add(ferment);
    }

    /**
     * Empty constructor for editor activity
     */
    public Brew() {
        this.recipe = new Recipe();
    }

    // METHODS

    public boolean pauseStage() {
        if (!ferments.isEmpty()) {
//            ferments.peek().pause();
            ferments.get(0).pause();
            return true;
        } else {
            return false;
        }
    }

    public boolean advanceStage() {
        // return ferments.size() > 1 && ferments.add(ferments.poll());
        return ferments.size() > 1 && ferments.add(ferments.remove(0));
    }

    // SETTERS & GETTERS

    public @NonNull Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(@NonNull Recipe recipe) {
        this.recipe = recipe;
    }

    public Stage getStage() {
//        return ferments.isEmpty() ? null : ferments.peek().getStage();
        return ferments.isEmpty() ? null : ferments.get(0).getStage();
    }

    public long getStartDate() {
        if (!ferments.isEmpty()) {
//            return ferments.peek().getStartDate();
            return ferments.get(0).getStartDate();
        } else {
            throw new NullPointerException("Ferment does not contain a startDate");
        }
    }

    public void setStartDate(long startDate) {
        if (!ferments.isEmpty()) {
//            ferments.peek().setStartDate(startDate);
            ferments.get(0).setStartDate(startDate);
        }
    }

    public long getEndDate() {
        if (!ferments.isEmpty()) {
//            return ferments.peek().getEndDate();
            return ferments.get(0).getEndDate();
        } else {
            throw new NullPointerException("Ferment does not contain a endDate");
        }
    }

    public void setEndDate(long endDate) {
        if (!ferments.isEmpty()) {
//            ferments.peek().setEndDate(endDate);
            ferments.get(0).setEndDate(endDate);
        }
    }

    public void addFerment(Ferment ferment) {
        ferments.add(ferment);
    }

    public List<Ferment> getFerments() {
        return ferments;
    }

    public void setFerments(List<Ferment> ferments) {
        this.ferments = ferments;
    }
}
