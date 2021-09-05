package org.dionthorn.lifesimrpg.records;

public record Attributes(
        double strength,
        double constitution,
        double dexterity,
        double intelligence,
        double wisdom,
        double charisma
) {
    public final static double MAX_ATTRIBUTE = 100.00; // constant attribute cap

}
