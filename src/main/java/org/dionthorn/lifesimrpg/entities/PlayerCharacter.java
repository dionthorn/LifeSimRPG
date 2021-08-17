package org.dionthorn.lifesimrpg.entities;

import java.time.LocalDate;

/**
 * Will manage methods distinct from the AICharacter
 */
public class PlayerCharacter extends AbstractCharacter {

    /**
     * Player constructor used to generate the starting Player object
     * Will start with the default Unemployed job and 0 dailyPayRate
     * @param birthday the Characters LocalDate reference to their birthday date
     * @param firstName the String representing the Characters first name
     * @param lastName the String representing the Characters last name
     */
    public PlayerCharacter(LocalDate birthday, String firstName, String lastName) {
        super();
        this.birthday = birthday;
        this.firstName = firstName;
        this.lastName = lastName;
        this.job = new Job("Unemployed", 0);
    }

}
