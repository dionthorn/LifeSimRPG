package org.dionthorn.lifesimrpg.entities;

import org.dionthorn.lifesimrpg.FileOpUtils;

import java.net.URI;
import java.util.ArrayList;

public class Place extends Entity {

    public enum PLACE_TYPE {
        RESIDENTIAL_ZONE,
        HOUSE,
        NORMAL,
        STORE,
        FOOD,
        SCHOOL,
        REALTOR
    }

    private final ArrayList<Character> characters = new ArrayList<>();
    private final ArrayList<Place> connections = new ArrayList<>();

    private final String name;
    private PLACE_TYPE type;

    // generate Place object directly without metadata used for Residence
    public Place(String name, PLACE_TYPE type) {
        super();
        this.name = name;
        this.type = type;
    }

    // generate Place object from fileName should be qualified by Map for JRT or not
    public Place(String fileName) {
        super();
        this.name = fileName.split("/")[fileName.split("/").length - 1];
        // load place data and character data from resources
        String[] fileLines;
        fileLines = FileOpUtils.getFileLines(URI.create(fileName + ".place"));
        boolean TY = false; // small two state machine
        boolean AI = false;
        for(String line: fileLines) {
            if(line.contains(":TYPE:")) {
                TY = true;
                AI = false;
            } else if(line.contains(":AI:")) {
                AI = true;
                TY = false;
            } else if(TY) {
                // process TYPE data
                type = Enum.valueOf(PLACE_TYPE.class, line);
            } else if(AI) {
                // process AI data
                int toGenerate = Integer.parseInt(line.replaceAll(" ",""));
                for(int i=0; i<toGenerate; i++) {
                    Character temp = new Character();
                    characters.add(temp);
                    temp.setCurrentLocation(this);
                }
            }
        }

    }

    // getters and setters

    public String getName() {
        return name;
    }

    public ArrayList<Character> getCharacters() {
        return characters;
    }

    public ArrayList<Place> getConnections() {
        return connections;
    }

    public void addConnection(Place place) {
        if(!connections.contains(place)) {
            connections.add(place);
        }
    }

    public PLACE_TYPE getType() {
        return type;
    }

}
