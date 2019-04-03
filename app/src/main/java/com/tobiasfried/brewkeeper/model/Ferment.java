package com.tobiasfried.brewkeeper.model;

import com.google.firebase.firestore.Exclude;
import com.tobiasfried.brewkeeper.constants.Stage;

public class Ferment{

    private Stage stage;
    private long startDate;
    private long endDate;

    /**
     * Required empty constructor for Firestore
     */
    public Ferment() {

    }

    /**
     * Constructor for programmatic use
     * @param stage         {@link Stage}
     * @param startDate     long UNIX timestamp
     * @param endDate       long UNIX timestamp
     */
    public Ferment(Stage stage, long startDate, long endDate) {
        this.stage = stage;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @Exclude
    public void pause() {
        this.stage = Stage.PAUSED;
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public long getEndDate() {
        return endDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }
}
