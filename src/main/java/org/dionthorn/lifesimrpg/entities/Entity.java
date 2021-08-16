package org.dionthorn.lifesimrpg.entities;

import java.util.ArrayList;

public abstract class Entity {

    public static int GEN_COUNT = 0;
    protected final int UID = GEN_COUNT++;
    public static final ArrayList<Entity> entities = new ArrayList<>();

    /**
     * Default Entity Constructor will assign the entity a unique id then increment the GEN_COUNT that's it.
     */
    protected Entity() {
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
