package com.example.przemek.gymdiary.Models;

import java.io.Serializable;

public class Set implements Serializable {

    private int repeats;
    private double weight;

    public Set() {
    }

    public Set(int repeats, double weight) {
        this.repeats = repeats;
        this.weight = weight;
    }

    public int getRepeats() {
        return repeats;
    }

    public void setRepeats(int repeats) {
        this.repeats = repeats;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    @Override
    public String toString() {
        return String.valueOf(getRepeats()) + " " + String.valueOf(getRepeats());
    }
}
