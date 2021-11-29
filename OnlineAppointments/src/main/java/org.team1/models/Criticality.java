package org.team1.models;

public enum Criticality {
    REGULAR(0), URGENT(1);

    private int value;

    Criticality(int value) {
        this.value = value;
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

    public static Criticality parse(Criticality id) {

        return id;
    }

    public int getValue() {
        return value;
    }
}
