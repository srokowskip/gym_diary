package com.example.przemek.gymdiary.Enums;

public enum MusculeGroup {

    Chest("Klatka piersiowa"),
    Legs("Nogi"),
    Arms("Ramiona"),
    Back("Plecy");

    String musculeGroup;

    MusculeGroup(String musculeGroup) {

        this.musculeGroup = musculeGroup;

    }

    public String getMusculeGroup() {
        return musculeGroup;
    }

    @Override
    public String toString() {
        return this.musculeGroup;
    }
}
