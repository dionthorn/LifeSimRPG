package org.dionthorn.lifesimrpg;

import org.dionthorn.lifesimrpg.entities.*;
import org.dionthorn.lifesimrpg.entities.Character;
import java.net.URI;
import java.time.LocalDate;

/**
 * GameState will manage the currentMap, the player, and the currentDate references,
 * Thus this manages the 'state' of the game objects
 *      currentMap - Hold references to all the Place objects, and each place object
 *                   holds the references to all Character objects amongst them.
 *                   currentMap.getAllCharacters() displays the relationship.
 *      player - Character reference of the player
 *      currentDate - LocalDate to manage game time
 */
public class GameState {

    // Game Constants
    public static final LocalDate DAY_ONE = LocalDate.of(1990,1,1); // Start on 1/1/1990
    public static final LocalDate AGE_CAP = LocalDate.of(1972,1,1); // Birthday age cap date

    // Game Variables
    private LocalDate currentDate = LocalDate.of(1990,1,1).minusDays(1);
    private final Character player;
    private final Map currentMap; // if we add more maps won't be final

    /**
     * GameState constructor will clear the AbstractEntity.entities master reference,
     * in case this is starting a new game after having already played.
     * It will also generate the Map object, will add ability to change name later on
     * Sets up the default player state as well as AI state, loads job data, and generates homes
     * @param firstName String representing the players chosen first name
     * @param lastName String representing the players chosen last name
     * @param birthday LocalDate representing the players chosen birthday
     */
    public GameState(String firstName, String lastName, LocalDate birthday) {
        // New Game is just a fresh GameState
        AbstractEntity.entities.clear();

        // load map
        currentMap = new Map("Vanillaton");

        // generate homes for all AI in the ResidentialZone
        currentMap.getPlaces().stream().filter(p -> p.getType() == Place.PLACE_TYPE.RESIDENTIAL_ZONE).forEach(p -> p.getCharacters().forEach(c -> {
            Residence temp = new Residence(String.format("%s Home", c.getLastName()), 30, p);
            c.setHome(temp);
            p.getConnections().add(temp);
        }));

        // add new player default age 18 born 1/1/1972 for starting date 1/1/1990
        player = new Character(birthday, firstName, lastName);

        // give player a default cheap house
        Residence defaultHome = null;
        Place resZone;
        for(Place check: currentMap.getPlaces()) {
            if(check.getName().equals("ResidentialZone")) {
                resZone = check;
                defaultHome = new Residence(String.format(
                        "%s Home (Your Home)",
                        player.getLastName()),
                        25,
                        resZone
                );
                resZone.getConnections().add(defaultHome);
                currentMap.getPlaces().add(defaultHome);
                break;
            }
        }
        player.setHome(defaultHome);
        player.setCurrentLocation(defaultHome);

        // add the new Residence objects to the map
        for(AbstractEntity e: AbstractEntity.entities) {
            if(e instanceof Residence) {
                currentMap.getPlaces().add((Residence)e);
            }
        }

        // load job info
        String[] jobs;
        if(FileOpUtils.JRT) {
            jobs = FileOpUtils.getFileNamesFromDirectory(
                    URI.create(FileOpUtils.jrtBaseURI + "Jobs")
            );
        } else {
            jobs = FileOpUtils.getFileNamesFromDirectory(
                    URI.create(String.valueOf(getClass().getResource("/Jobs")))
            );
        }
        for(String fileName: jobs) {
            new Job(fileName);
        }
    }

    // getters and setters

    public Character getPlayer() {
        return player;
    }

    public LocalDate getCurrentDate() {
        return currentDate;
    }

    public Map getCurrentMap() {
        return currentMap;
    }

    public void setCurrentDate(LocalDate currentDate) {
        this.currentDate = currentDate;
    }

}
