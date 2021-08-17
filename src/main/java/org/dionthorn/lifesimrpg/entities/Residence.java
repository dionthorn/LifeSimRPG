package org.dionthorn.lifesimrpg.entities;

/**
 * Residence will manage data related to 'House' Type places that Characters live in
 * and will manage its own rentPerDay system
 */
public class Residence extends Place {

    // Variables
    private int rentPerDay;
    private int daysInPeriod = 0;
    private int daysLivedIn = 0;
    private int monthsUnpaid = 0;
    private int totalUnpaid = 0;

    /**
     * Will generate a Residence and keep track of rent, days lived in/rent period time tracking,
     * gets added to the residentialZone Place provided and sets the rentPerDay
     * @param name String representing the Residence name usually "{ownerName} Home"
     * @param rentPerDay int representing the daily rent cost per day in $
     * @param residentialZone Place representing the connected RESIDENTIAL_ZONE type Place this Residence is in
     */
    public Residence(String name, int rentPerDay, Place residentialZone) {
        super(name, PLACE_TYPE.HOUSE);
        this.rentPerDay = rentPerDay;
        this.addConnection(residentialZone);
    }

    // Logical

    /**
     * Will increase daysLivedIn and daysInPeriod by 1
     * Used each day to track rent costs
     */
    public void onNextDay() {
        this.daysLivedIn++;
        this.daysInPeriod++;
    }

    // Qualified getters

    /**
     * Will return an int representing the rentPerDay * daysInPeriod then resets daysInPeriod to 0
     * @return int representing the rentPerDay * daysInPeriod then resets daysInPeriod to 0
     */
    public int getRentPeriodCost() {
        int days = this.daysInPeriod;
        this.daysInPeriod = 0;
        return days * this.rentPerDay;
    }

    // Pure getters and setters

    /**
     * Will return an int representing the current daysLivedIn this Residence
     * @return int representing the current daysLivedIn this Residence
     */
    public int getDaysLivedIn() {
        return this.daysLivedIn;
    }

    /**
     * Will return an int representing the current daysInPeriod
     * @return int representing the current daysInPeriod
     */
    public int getDaysInPeriod() {
        return this.daysInPeriod;
    }

    /**
     * Will return an int representing the current rentPerDay
     * @return int representing the current rentPerDay
     */
    public int getRentPerDay() {
        return this.rentPerDay;
    }

    /**
     * Will return an int representing the current monthsUnpaid rent
     * @return int representing the current monthsUnpaid rent
     */
    public int getMonthsUnpaid() {
        return this.monthsUnpaid;
    }

    /**
     * Will return an int representing the current totalUnpaid rent
     * @return int representing the current totalUnpaid rent
     */
    public int getTotalUnpaid() {
        return this.totalUnpaid;
    }

    /**
     * Will set an int representing the new value for this Residence monthsUnpaid value
     * @param monthsUnpaid int representing the new value for this Residence monthsUnpaid value
     */
    public void setMonthsUnpaid(int monthsUnpaid) {
        this.monthsUnpaid = monthsUnpaid;
    }

    /**
     * Will set an int representing the new value for this Residence rentPerDay value
     * @param rentPerDay int representing the new value for this Residence rentPerDay value
     */
    public void setRentPerDay(int rentPerDay) {
        this.rentPerDay = rentPerDay;
    }

    /**
     * Will set an int representing the new value for this Residence totalUnpaid value
     * @param totalUnpaid int representing the new value for this Residence totalUnpaid value
     */
    public void setTotalUnpaid(int totalUnpaid) {
        this.totalUnpaid = totalUnpaid;
    }

}
