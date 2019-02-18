package com.tobiasfried.brewkeeper;

import java.util.Date;

public class Brew {

    private String name;
    private int stage;
    private Date date;

    public static final int STAGE_PRIMARY = 1;
    public static final int STAGE_SECONDARY = 2;

    public Brew(String name, int stage, Date date) {
        this.name = name;
        this.stage = stage;
        this.date = date;
    }

    public String getName() {
        return this.name;
    }

    public int getStage() {
        return this.stage;
    }

    public void setStage(int stage) {
        if (stage == STAGE_PRIMARY || stage == STAGE_SECONDARY) {
            this.stage = stage;
        }
    }

    public Date getDate() {
        return this.date;
    }

}
