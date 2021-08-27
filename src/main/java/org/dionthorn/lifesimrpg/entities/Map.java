package org.dionthorn.lifesimrpg.entities;

import org.dionthorn.lifesimrpg.FileOpUtil;
import java.net.URI;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Map will manage data in /Maps/{mapName}/{mapName}.map
 *
 * Map will also keep reference of all associated Place objects, set their connections,
 * And any Course objects required for this map
 */
public class Map extends AbstractEntity {

    // Map variables for tracking Place and Course objects as well as the name for reference
    private final String name;
    private final ArrayList<Place> places = new ArrayList<>();
    private final ArrayList<Course> courses = new ArrayList<>();

    /**
     * Constructor takes a String representing the mapName to target
     * ex: /Maps/{mapName}/{mapName}.map
     * @param mapName String representing the mapName to target
     */
    public Map(String mapName) {
        super();

        // load map data from resources ex input: Vanillaton
        this.name = mapName;
        String[] fileLines = FileOpUtil.getFileLines(URI.create(FileOpUtil.MAP_PATH + "/" + mapName + ".map"));
        Place target = null;

        // loop through all fileLines
        for(String line: fileLines) {
            // $ indicates we want to connect the following lines to this Place
            if(line.contains("$")) {
                String targetName = line.replaceAll("\\$", "");

                // test if this Place already exists
                boolean isNew = true;
                for(Place place: this.places) {
                    if(place.getName().equals(targetName)) {
                        isNew = false;
                        target = place;
                    }
                }

                // if this Place is new then add it to the places reference
                if(isNew) {
                    target = new Place(FileOpUtil.MAP_PATH + targetName);
                    this.places.add(target);
                }
            } else {
                // if no $ then we connect this Place to the last $ marked Place
                if(line.startsWith(":END")) {
                    // Marks the End of connection data this is an artifact we might add more map level metadata later
                    break;
                } else if(!line.startsWith(":")) {
                    boolean isNew = true;
                    Place notNew = null;

                    // check if Place is new or not
                    for(Place place: this.places) {
                        if(place.getName().equals(line)) {
                            isNew = false;
                            notNew = place;
                            break;
                        }
                    }

                    // if new add connection and to places reference
                    if(isNew && target != null) {
                        Place newPlace = new Place(FileOpUtil.MAP_PATH + line);
                        this.places.add(newPlace);
                        target.addConnection(newPlace);
                        newPlace.addConnection(target);
                    } else if(target != null) {
                        // if not new then just add connection
                        target.addConnection(notNew);
                        notNew.addConnection(target);
                    }
                }
            }
        }

        // load courses
        for(String fileName: FileOpUtil.getFileNamesFromDirectory(FileOpUtil.MAP_COURSES_PATH)) {
            this.courses.add(new Course(fileName));
        }
    }

    // Logical

    /**
     * Will return an ArrayList representing all characters in every Place reference this Map has
     * @return ArrayList representing all characters in every Place reference this Map has
     */
    public ArrayList<AbstractCharacter> getAllCharacters() {
        return this.places.stream().flatMap(
                p -> p.getCharacters().stream()
        ).collect(Collectors.toCollection(ArrayList::new));
    }

    // Pure getters

    /**
     * Will return an ArrayList representing all Course objects referenced by this Map
     * @return ArrayList representing all Course objects referenced by this Map
     */
    public ArrayList<Course> getCourses() {
        return this.courses;
    }

    /**
     * Will return a String representing this Map name
     * @return String representing this Map name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Will return an ArrayList representing all Place references this Map has
     * @return ArrayList representing all Place references this Map has
     */
    public ArrayList<Place> getPlaces() {
        return this.places;
    }


}
