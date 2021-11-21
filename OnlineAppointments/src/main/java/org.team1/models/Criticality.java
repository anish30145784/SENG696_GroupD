package org.team1.models;

public enum Criticality {
    HIGH(2),MEDIUM(1),LOW(0);

    private int value;

    Criticality(int value) { this.value = value; }

    Criticality() {

    }
    public int getValue() { return value; }

    public static Criticality parse(int id) {
        Criticality criticality = null; // Default
        for (Criticality item : Criticality.values()) {
            if (item.getValue()==id) {
                criticality = item;
                break;
            }
        }
        return criticality;
    }
}
