package org.dionthorn.lifesimrpg;

import java.net.URI;
import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Static utility class for generating random names
 */
public class NameDataUtil {

    // static used for storing firstNames/lastNames data for AI and the random object
    public static final ArrayList<String> firstNames = new ArrayList<>();
    public static final ArrayList<String> lastNames = new ArrayList<>();
    public static final Random rand = new Random();

    /**
     * Will return a LocalDate representing a random valid Date between 1/1/1960 and gameState.AGE_CAP
     * @return LocalDate representing a random valid Date between 1/1/1960 and gameState.AGE_CAP
     */
    public static LocalDate getRandomDate() {
        LocalDate start = LocalDate.of(1960, Month.JANUARY, 1);
        long days = ChronoUnit.DAYS.between(start, GameState.AGE_CAP);
        return start.plusDays(rand.nextInt((int) days + 1));
    }

    /**
     * Will return a String representing a random first name pulled from resources/AI/firstName.dat
     * @return String representing a random first name pulled from resources/AI/firstName.dat
     */
    public static String getRandomFirstName() {
        if(firstNames.size() == 0) {
            String[] names = FileOpUtil.getFileLines(URI.create(FileOpUtil.NAME_PATH + "firstName.dat"));
            Collections.addAll(firstNames, names);
        }
        return firstNames.get(rand.nextInt(firstNames.size()));
    }

    /**
     * Will return a String representing a random last name pulled from resources/AI/lastName.dat
     * @return String representing a random last name pulled from resources/AI/lastName.dat
     */
    public static String getRandomLastName() {
        if(lastNames.size() == 0) {
            String[] names = FileOpUtil.getFileLines(URI.create(FileOpUtil.NAME_PATH + "lastName.dat"));
            Collections.addAll(lastNames, names);
        }
        return lastNames.get(rand.nextInt(lastNames.size()));
    }

}
