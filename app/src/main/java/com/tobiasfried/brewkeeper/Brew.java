package com.tobiasfried.brewkeeper;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Brew {

    private String name;
    private Stage stage;
    private Date date;

    public Brew(String name, Stage stage, Date date) {
        this.name = name;
        this.stage = stage;
        this.date = date;
    }

    public String getName() {
        return this.name;
    }

    public Stage getStage() {
        return this.stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public Date getDate() {
        return this.date;
    }

}
