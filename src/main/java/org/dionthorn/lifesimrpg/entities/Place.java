package org.dionthorn.lifesimrpg.entities;

import org.dionthorn.lifesimrpg.FileOpUtil;
import java.net.URI;
import java.util.ArrayList;

/**
 * Place will manage information about a location such as PLACE_TYPE flags for deciding context UI,
 * the characters reference will manage which Characters are considered 'at' this Place,
 * the connections reference will manage which Places are considered 'connected' to this Place,
 * name will reference this Place name, and type will reference this Place type.
 */
public class Place extends AbstractEntity {

    /**
     * Enum to flag place type for context
     */
    public enum PLACE_TYPE {
        RESIDENTIAL_ZONE,
        HOUSE,
        NORMAL,
        STORE,
        FOOD,
        SCHOOL,
        REALTOR
    }

    // Variables
    private final ArrayList<AbstractCharacter> characters = new ArrayList<>();
    private final ArrayList<Place> connections = new ArrayList<>();
    private final String name;
    private PLACE_TYPE type;

    /**
     * Constructor used to generate Residence child objects
     * @param name String representing this Place name
     * @param type PLACE_TYPE representing this Place type
     */
    public Place(String name, PLACE_TYPE type) {
        super();
        this.name = name;
        this.type = type;
    }

    // generate Place object from fileName should be qualified by Map for JRT or not

    /**
     * Default constructor loads Place data from {fileName}.place
     * where {fileName} is of the form: /Maps/{mapName}/{placeName}
     * @param fileName String representing the target fileName, should match the Place name
     */
    public Place(String fileName) {
        super();

        // Load data from {fileName}.place
        this.name = fileName.split("/")[fileName.split("/").length - 1];
        String[] fileLines;
        fileLines = FileOpUtil.getFileLines(URI.create(fileName + ".place"));
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
                this.type = Enum.valueOf(PLACE_TYPE.class, line);
            } else if(AI) {
                // process AI data, currently very simple just pulls a number and generates, might
                // want this to be multiline where you can pull .ai file data to generate specified AI
                // that's far down the line after some form of goal system
                int toGenerate = Integer.parseInt(line.replaceAll(" ",""));
                for(int i=0; i<toGenerate; i++) {
                    AbstractCharacter temp = new AICharacter();
                    this.characters.add(temp);
                    temp.setCurrentLocation(this);
                }
            }
        }

    }

    // Logical

    public void addConnection(Place place) {
        if(!this.connections.contains(place)) {
            this.connections.add(place);
        }
    }

    // Pure getters

    public String getName() {
        return this.name;
    }

    public ArrayList<AbstractCharacter> getCharacters() {
        return this.characters;
    }

    public ArrayList<Place> getConnections() {
        return this.connections;
    }

    public PLACE_TYPE getType() {
        return this.type;
    }

}
