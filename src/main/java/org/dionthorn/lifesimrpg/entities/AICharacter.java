package org.dionthorn.lifesimrpg.entities;

import org.dionthorn.lifesimrpg.NameDataUtil;

/**
 * AICharacter will manage AI distinct methods
 */
public class AICharacter extends AbstractCharacter {

    /**
     * AI constructor will use random information to generate a AbstractCharacter object
     */
    public AICharacter() {
        super();
        this.birthday = NameDataUtil.getRandomDate();
        this.firstName = NameDataUtil.getRandomFirstName();
        this.lastName = NameDataUtil.getRandomLastName();
        this.job = new Job("Salary Person", 70); // temporary
    }

    /**
     * Will randomly decide a Place to move this AbstractCharacter to
     * from among the currentLocation connections reference
     */
    public void moveRandom() {
        int coinFlip = NameDataUtil.rand.nextInt(2); // 50%ish returns 0 or 1
        if(coinFlip > 0) {
            int cap = this.currentLocation.getConnections().size();
            Place choice = this.currentLocation.getConnections().get(NameDataUtil.rand.nextInt(cap));
            this.moveTo(choice);
        }
    }

}
