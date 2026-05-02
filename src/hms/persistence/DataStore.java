package hms.persistence;

import hms.service.HospitalSystem;

import java.io.*;
import java.nio.file.*;

/**
 * FILE I/O requirement: handles saving and loading HospitalSystem via serialization.
 * EXCEPTION HANDLING: wraps IOException and ClassNotFoundException.
 * Modular design — can be replaced with CSV implementation later.
 */
public class DataStore {

    private static final String DEFAULT_PATH = "data/hms.dat";

    /**
     * Saves the entire HospitalSystem state to a binary file.
     * @param system the HospitalSystem to save
     * @param path   file path (use null for default)
     * @throws IOException if writing fails
     */
    public static void save(HospitalSystem system, String path) throws IOException {
        String filePath = (path == null || path.trim().isEmpty()) ? DEFAULT_PATH : path;
        // Ensure parent directories exist
        Path parent = Paths.get(filePath).getParent();
        if (parent != null) Files.createDirectories(parent);

        try (ObjectOutputStream oos = new ObjectOutputStream(
                new BufferedOutputStream(new FileOutputStream(filePath)))) {
            oos.writeObject(system);
        }
    }

    /**
     * Loads HospitalSystem from a binary serialized file.
     * @param path file path (use null for default)
     * @return HospitalSystem loaded from disk, or a fresh instance if file not found
     * @throws IOException            if reading fails (corrupt file, permissions, etc.)
     * @throws ClassNotFoundException if the class version has changed
     */
    public static HospitalSystem load(String path) throws IOException, ClassNotFoundException {
        String filePath = (path == null || path.trim().isEmpty()) ? DEFAULT_PATH : path;

        if (!Files.exists(Paths.get(filePath))) {
            // No file yet — return fresh system
            return new HospitalSystem();
        }

        try (ObjectInputStream ois = new ObjectInputStream(
                new BufferedInputStream(new FileInputStream(filePath)))) {
            return (HospitalSystem) ois.readObject();
        }
    }

    /**
     * Checks whether a saved file exists at the default path.
     */
    public static boolean savedFileExists() {
        return Files.exists(Paths.get(DEFAULT_PATH));
    }

    public static String getDefaultPath() { return DEFAULT_PATH; }
}
