package org.team1.models;

public enum Criticality {
    REGULAR, URGENT;

    private int value;

    Criticality(int value) {
        this.value = value;
    }

    Criticality() {

    }

    public static Criticality parse(int id) {
        Criticality criticality = null; // Default
        for (Criticality item : Criticality.values()) {
            if (item.getValue() == id) {
                criticality = item;
                break;
            }
        }
        return criticality;
    }

    public int getValue() {
        return value;
    }
}
