package org.dionthorn.lifesimrpg.entities;

import org.dionthorn.lifesimrpg.NameDataUtil;

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
        int coinFlip = NameDataUtil.rand.nextInt(2); // 50%ish
        if(coinFlip > 0) {
            int cap = getCurrentLocation().getConnections().size();
            Place choice = getCurrentLocation().getConnections().get(NameDataUtil.rand.nextInt(cap));
            moveTo(choice);
        }
    }

}
