package org.dionthorn.lifesimrpg.entities;

import java.util.ArrayList;

/**
 * AbstractEntity is the base 'game object' where all game objects inherit from this class
 * We generate a unique ID for each Entity loaded by the Application
 * We store the entity in a static ArrayList for quick reference of all game objects
 */
public abstract class AbstractEntity {

    // Used as the master entity reference list, should be clear only on starting a new game.
    public static final ArrayList<AbstractEntity> entities = new ArrayList<>();

    // These keeps track of the UID generation.
    private static int GEN_COUNT = 0;
    protected final int UID = GEN_COUNT++;

    /**
     * Default AbstractEntity Constructor will assign the entity a unique id then increment the GEN_COUNT that's it.
     */
    protected AbstractEntity() {
        // UID assignment takes place upon construction as defined by UID declaration at the class level.
        // It is then ++ after assignment, giving us a Unique Identification number for every entity.
        entities.add(this);
    }

    /**
     * Returns the integer Unique ID of the entity, no two entities will have the same UID.
     * @return the integer unique id of the entity
     */
    public int getUID() {
        return UID;
    }

}
