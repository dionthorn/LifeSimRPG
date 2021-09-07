package org.dionthorn.lifesimrpg;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.*;
import java.util.ArrayList;

/**
 * Dedicated class for static methods related to file operations
 */
public final class FileOpUtil {

    // static flag and URI reference for JRT when in JLink/JPackage distribution
    public static boolean JRT = false;
    public static URI jrtBaseURI; // jrt:/LifeSimRPG/

    // Paths that mark folder locations
    public static URI NAME_PATH; // LifeSimRPG/NameData/
    public static URI GAME_MAP_PATH; // LifeSimRPG/Maps/
    public static URI GAME_FXML_PATH; // LifeSimRPG/FXML/
    public static String START_SCREEN_MAP_NAME; // {CurrentMap}
    public static URI MAP_PATH; // LifeSimRPG/Maps/{CurrentMap}/
    public static URI MAP_COURSES_PATH; // LifeSimRPG/Maps/{CurrentMap}/Courses
    public static URI MAP_JOBS_PATH; // LifeSimRPG/Maps/{CurrentMap}/Jobs
    public static URI MAP_FXML_PATH; // LifeSimRPG/Maps/{CurrentMap}/FXML

    private FileOpUtil() {
        // This is a static utility class that is not intended to be instantiated,
        // therefore we create a private constructor,
        // and declare the class as final, so it cannot be extended.
        // all calls should be of the form FileOpUtil.functionName()
    }

    /**
     * Will test if we are in a JRT distribution and set the JRT flag
     * As well as the jrtBaseURI, so we can access resources in JLink and JPackage
     */
    public static void checkJRT() {
        URL resource = App.class.getClassLoader().getResource("Credits.txt");
        if(resource == null || resource.getProtocol().equals("jrt")) {
            FileOpUtil.JRT = true;
            jrtBaseURI = URI.create("jrt:/LifeSimRPG/");
        }
    }

    /**
     * Will set the game level Paths
     */
    public static void initializePaths() {
        if(JRT) {
            NAME_PATH = URI.create(FileOpUtil.jrtBaseURI + "NameData/");
            GAME_FXML_PATH = URI.create(FileOpUtil.jrtBaseURI + "FXML/");
            GAME_MAP_PATH = URI.create(FileOpUtil.jrtBaseURI + "Maps/");
        } else {
            NAME_PATH = URI.create(String.valueOf(App.class.getResource("/NameData")));
            GAME_FXML_PATH = URI.create(String.valueOf(App.class.getResource("/FXML")));
            GAME_MAP_PATH = URI.create(String.valueOf(App.class.getResource("/Maps")));
        }
    }

    /**
     * Will set map level Paths based on provided mapName
     * @param mapName String representing the folder to target
     */
    public static void initializeMapPaths(String mapName) {
        START_SCREEN_MAP_NAME = mapName;
        MAP_PATH = URI.create(GAME_MAP_PATH + mapName + "/");
        MAP_FXML_PATH = URI.create(GAME_MAP_PATH + mapName + "/FXML/");
        MAP_JOBS_PATH = URI.create(GAME_MAP_PATH + mapName + "/Jobs/");
        MAP_COURSES_PATH = URI.create(GAME_MAP_PATH + mapName + "/Courses/");
    }

    /**
     * Returns the filenames from a directory as a string array where
     * each index is a name of a file including extensions
     * @param targetDirectory the target file
     * @return a string array where each index is the name of a file in the directory at path
     */
    public static String[] getFileNamesFromDirectory(URI targetDirectory) {
        ArrayList<String> fileNamesList = new ArrayList<>();
        String[] fileNames = new String[0];
        try {
            File[] files;
            if(targetDirectory.getScheme().equals("jrt")) {
                Path path = Path.of(targetDirectory);
                assert(Files.exists(path));
                FileSystem jrtFS = FileSystems.getFileSystem(URI.create("jrt:/"));
                assert(Files.exists(jrtFS.getPath(path.toString())));
                try {
                    DirectoryStream<Path> stream = Files.newDirectoryStream(jrtFS.getPath(path.toString()));
                    for(Path entry: stream) {
                        fileNamesList.add(entry.getFileName().toString());
                    }
                    fileNames = fileNamesList.toArray(new String[0]);
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
            files = new File(targetDirectory.getPath()).listFiles();
            if(files != null) {
                if(!targetDirectory.getScheme().equals("jrt")) {
                    fileNames = new String[files.length];
                    for(int i=0; i<files.length; i++) {
                        if(files[i].isFile()) {
                            fileNames[i] = files[i].getName();
                        }
                    }
                } else {
                    System.out.println("Using JRT Filesystem");
                }
            } else {
                if(!targetDirectory.getScheme().equals("jrt")) {
                    System.out.println("No Files Found In Directory " + targetDirectory);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileNames;
    }

    /**
     * Will return a String[] containing the names of all folders in targetDirectory
     * @param targetDirectory URI representing the target folder to pull child dir names from
     * @return String[] representing the child folder names inside targetDirectory
     */
    public static String[] getFolderNamesFromDirectory(URI targetDirectory) {
        File[] directories = null;
        String[] folderNames = new String[0];
        ArrayList<String> folderNamesList = new ArrayList<>();
        if(targetDirectory.getScheme().equals("jrt")) {
            Path path = Path.of(targetDirectory);
            assert(Files.exists(path));
            FileSystem jrtFS = FileSystems.getFileSystem(URI.create("jrt:/"));
            assert(Files.exists(jrtFS.getPath(path.toString())));
            try {
                DirectoryStream<Path> stream = Files.newDirectoryStream(jrtFS.getPath(path.toString()));
                for(Path entry: stream) {
                    if(Files.isDirectory(entry)) {
                        folderNamesList.add(entry.getFileName().toString());
                    }
                }
                folderNames = folderNamesList.toArray(new String[0]);
            } catch(Exception e) {
                e.printStackTrace();
            }
        } else {
            directories = new File(targetDirectory).listFiles(File::isDirectory);
        }
        if(directories != null) {
            folderNames = new String[directories.length];
            for(int i=0; i<directories.length; i++) {
                folderNames[i] = directories[i].getName();
            }
        }
        return folderNames;
    }

    /**
     * Will process a target file as if it were lines of String
     * @param targetFile the target file
     * @return a string array where each index is a line from the file
     */
    public static String[] getFileLines(URI targetFile) {
        String[] toReturn = new String[0];
        try {
            toReturn = Files.readAllLines(Path.of(targetFile)).toArray(new String[0]);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return toReturn;
    }

}
