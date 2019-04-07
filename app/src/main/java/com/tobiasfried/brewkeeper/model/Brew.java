package com.tobiasfried.brewkeeper.model;

import androidx.annotation.NonNull;
import com.google.firebase.firestore.ServerTimestamp;
import com.tobiasfried.brewkeeper.constants.*;


public class Brew {

    public static final String CURRENT = "brews";
    public static final String HISTORY = "history";


    // MEMBER FIELDS
    private Recipe recipe;
    private Stage stage;
    private Ferment primaryFerment;
    private Ferment secondaryFerment;

    @ServerTimestamp
    private long serverTimestamp;

    // CONSTRUCTORS

    /**
     * Constructor to start from recipe
     *
     * @param recipe from recipe
     */
    public Brew(@NonNull Recipe recipe) {
        this.recipe = recipe;
    }

    /**
     * Empty constructor for editor activity
     */
    public Brew() {
        this.recipe = new Recipe();
    }

    // METHODS

    public boolean advanceStage() {
        switch (stage) {
            case PRIMARY:
                stage = Stage.PAUSED;
                // TODO use method to update 1F end and 2F start simultaneously
                primaryFerment.second = System.currentTimeMillis();
                secondaryFerment.first = System.currentTimeMillis();
                break;
            case PAUSED:
                if (secondaryFerment != null) {
                    stage = Stage.SECONDARY;
                    primaryFerment.second = System.currentTimeMillis();
                    secondaryFerment.first = System.currentTimeMillis();
                } else {
                    return false;
                }
                break;
            case SECONDARY:
                stage = Stage.COMPLETE;
                // TODO also here
                secondaryFerment.second = System.currentTimeMillis();
                break;
            case COMPLETE:
            default:
                secondaryFerment.second = System.currentTimeMillis();
                return false;

        }
        return true;
    }

    public long getStartDate() {
        switch (stage) {
            case PRIMARY:
            case PAUSED:
                if (primaryFerment.first != null) {
                    return primaryFerment.first;
                }
            case SECONDARY:
            case COMPLETE:
                if (secondaryFerment.first != null) {
                    return secondaryFerment.first;
                }
            default:
                throw new NullPointerException("No appropriate stage found.");
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

    public long getEndDate() {
        switch (stage) {
            case PRIMARY:
            case PAUSED:
                if (primaryFerment.second != null) {
                    return primaryFerment.second;
                }
            case SECONDARY:
            case COMPLETE:
                if (secondaryFerment != null && secondaryFerment.second != null) {
                    return secondaryFerment.second;
                }
            default:
                throw new NullPointerException("No appropriate stage found.");
        }
    }

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

    public @NonNull
    Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(@NonNull Recipe recipe) {
        this.recipe = recipe;
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public Ferment getPrimaryFerment() {
        return primaryFerment;
    }

    public void setPrimaryFerment(Ferment primaryFerment) {
        this.primaryFerment = primaryFerment;
    }

    public Ferment getSecondaryFerment() {
        return secondaryFerment;
    }

    public void setSecondaryFerment(Ferment secondaryFerment) {
        this.secondaryFerment = secondaryFerment;
    }
}
