package org.dionthorn.lifesimrpg.entities;

public class Residence extends Place {

    private int rent; // per day rent cost
    private int daysInPeriod = 0;
    private int daysLivedIn = 0;
    private int monthsUnpaid = 0;
    private int totalUnpaid = 0;

    public Residence(String name, int rent, Place residentialZone) {
        super(name, PLACE_TYPE.HOUSE);
        this.rent = rent;
        this.addConnection(residentialZone);
    }

    // methods

    public void onNextDay() {
        daysLivedIn++;
        daysInPeriod++;
    }

    public int getRentPeriodCost() {
        int days = daysInPeriod;
        this.daysInPeriod = 0;
        return days * rent;
    }

    // getters and setters

    public int getDaysLivedIn() {
        return daysLivedIn;
    }

    public int getDaysInPeriod() {
        return daysInPeriod;
    }

    public int getRent() {
        return rent;
    }

    public void setRent(int rent) {
        this.rent = rent;
    }

    public int getMonthsUnpaid() {
        return monthsUnpaid;
    }

    public void setMonthsUnpaid(int monthsUnpaid) {
        this.monthsUnpaid = monthsUnpaid;
    }

    public int getTotalUnpaid() {
        return totalUnpaid;
    }

    public void setTotalUnpaid(int totalUnpaid) {
        this.totalUnpaid = totalUnpaid;
    }

}
