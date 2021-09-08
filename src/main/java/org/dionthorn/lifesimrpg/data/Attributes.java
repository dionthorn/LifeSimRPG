package org.dionthorn.lifesimrpg.data;

public class Attributes {

    public final static double MAX_ATTRIBUTE = 100.00; // constant attribute cap

    private double strength;
    private double constitution;
    private double dexterity;
    private double intelligence;
    private double wisdom;
    private double charisma;

    public Attributes() {
        Dice attributeDiceSet = new Dice(6, 3);
        strength = attributeDiceSet.roll();
        constitution = attributeDiceSet.roll();
        dexterity = attributeDiceSet.roll();
        intelligence = attributeDiceSet.roll();
        wisdom = attributeDiceSet.roll();
        charisma = attributeDiceSet.roll();
    }

    // logical

    public static int checkBonus(double statToCheck) {
        return (int) ((statToCheck - 10) / 2);
    }

    // Setters which also return the resulting value

    public double addStrength(double toAdd) {
        strength += toAdd;
        return strength;
    }

    public double addConstitution(double toAdd) {
        constitution += toAdd;
        return constitution;
    }

    public double addDexterity(double toAdd) {
        dexterity += toAdd;
        return dexterity;
    }


    public double addIntelligence(double toAdd) {
        intelligence += toAdd;
        return intelligence;
    }


    public double addWisdom(double toAdd) {
        wisdom += toAdd;
        return wisdom;
    }


    public double addCharisma(double toAdd) {
        charisma += toAdd;
        return charisma;
    }


    // pure getters

    public double getStrength() {
        return strength;
    }

    public double getConstitution() {
        return constitution;
    }

    public double getDexterity() {
        return dexterity;
    }

    public double getIntelligence() {
        return intelligence;
    }

    public double getWisdom() {
        return wisdom;
    }

    public double getCharisma() {
        return charisma;
    }

}
