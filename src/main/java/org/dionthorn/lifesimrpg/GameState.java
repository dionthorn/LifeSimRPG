package org.dionthorn.lifesimrpg;

import org.dionthorn.lifesimrpg.entities.*;
import java.time.LocalDate;

/**
 * GameState will manage the currentMap, the player, and the currentDate references,
 * Thus this manages the 'state' of the game objects
 *      currentMap - Hold references to all the Place objects, and each place object
 *                   holds the references to all AbstractCharacter objects amongst them.
 *                   currentMap.getAllCharacters() displays the relationship.
 *      player - AbstractCharacter reference of the player
 *      currentDate - LocalDate to manage game time
 */
public class GameState {

    // Game Constants
    public static final LocalDate DAY_ONE = LocalDate.of(1990,1,1); // Start on 1/1/1990
    public static final LocalDate AGE_CAP = LocalDate.of(1972,1,1); // Birthday age cap date

    // Game Variables
    private LocalDate currentDate = DAY_ONE.minusDays(1);
    private final PlayerCharacter player;
    private final Map currentMap; // the Map manages Place objects

    /**
     * GameState constructor will clear the AbstractEntity entities master reference,
     * in case this is starting a new game after having already played.
     * It will also generate the Map object, will add ability to change name later on
     * Sets up the default player state as well as AI state, loads job data, and generates homes
     * @param firstName String representing the players chosen first name
     * @param lastName String representing the players chosen last name
     * @param birthday LocalDate representing the players chosen birthday
     */
    public GameState(String firstName, String lastName, LocalDate birthday) {
        // Clear entities reference as this is a 'New Game' state
        AbstractEntity.entities.clear();

        // Load the map that was selected on the StartScreen
        this.currentMap = new Map(FileOpUtil.START_SCREEN_MAP_NAME);

        // generate homes for all AI in the ResidentialZone
        this.generateAIResidence();

        // add new player default age 18 born 1/1/1972 for starting date 1/1/1990
        this.player = new PlayerCharacter(birthday, firstName, lastName);

        // give player a default cheap house
        this.generatePlayerResidence();

        // add the new Residence objects to the map
        this.associateNewResidence();

        // load job info
        this.loadJobInfo();
    }

    // Logical

    /**
     * Will load and create Job objects from file
     */
    public void loadJobInfo() {
        String[] jobs = FileOpUtil.getFileNamesFromDirectory(FileOpUtil.MAP_JOBS_PATH);
        for(String fileName: jobs) {
            new Job(fileName);
        }
    }

    /**
     * Will find the residential zone and
     * for every AbstractCharacter present it will assign them a new Residence
     */
    public void generateAIResidence() {
        currentMap.getPlaces().stream().filter(
                p -> p.getType() == Place.PLACE_TYPE.RESIDENTIAL_ZONE
        ).forEach(
                p -> p.getCharacters().forEach(
                        c -> {
                            Residence temp = new Residence(String.format("%s Home", c.getLastName()), 30, p);
                            c.setHome(temp);
                            p.getConnections().add(temp);
                        }
                )
        );
    }

    /**
     * Will find the residential zone and
     * create a new Residence for the player
     * will also move the player to the home
     */
    public void generatePlayerResidence() {
        Residence defaultHome = null;
        Place resZone;
        for(Place check: this.currentMap.getPlaces()) {
            if(check.getType() == Place.PLACE_TYPE.RESIDENTIAL_ZONE) {
                resZone = check;
                defaultHome = new Residence(String.format(
                        "%s Home (Your Home)",
                        this.player.getLastName()),
                        25,
                        resZone
                );
                resZone.getConnections().add(defaultHome);
                this.currentMap.getPlaces().add(defaultHome);
                break;
            }
        }
        this.player.setHome(defaultHome);
        this.player.setCurrentLocation(defaultHome);
    }

    /**
     * Will find Residence not associated with currentMap and
     * add them to the currentMap places
     */
    public void associateNewResidence() {
        for(AbstractEntity e: AbstractEntity.entities) {
            if(e instanceof Residence) {
                Residence newRes = (Residence) e;
                if(!(currentMap.getPlaces().contains(newRes))) {
                    currentMap.getPlaces().add(newRes);
                }
            }
        }
    }

    // Pure getters and setters

    /**
     * Will return the current PlayerCharacter object
     * @return PlayerCharacter representing the current player
     */
    public PlayerCharacter getPlayer() {
        return player;
    }

    /**
     * Will return the current date as a LocalDate
     * @return LocalDate representing the current date
     */
    public LocalDate getCurrentDate() {
        return currentDate;
    }

    /**
     * Will return the current Map
     * @return Map representing the current map
     */
    public Map getCurrentMap() {
        return currentMap;
    }

    /**
     * Will set the currentDate to the provided LocalDate
     * @param currentDate LocalDate representing the new currentDate
     */
    public void setCurrentDate(LocalDate currentDate) {
        this.currentDate = currentDate;
    }

}
