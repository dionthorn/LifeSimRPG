package org.dionthorn.lifesimrpg;

import java.net.URI;
import java.time.LocalDate;

public class GameState {

    // Game Constants
    public static final LocalDate DAY_ONE = LocalDate.of(1990,1,1); // Start on 1/1/1990
    public static final LocalDate AGE_CAP = LocalDate.of(1972,1,1); // Birthday age cap date

    // Game Variables
    private LocalDate currentDate = LocalDate.of(1990,1,1); // keeps track of current date
    private final Character player;
    private final Map currentMap; // if we add more maps won't be final

    public GameState(String firstName, String lastName, LocalDate birthday) {
        // New Game is just a fresh GameState
        Entity.entities.clear();

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
        for(Entity e: Entity.entities) {
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
