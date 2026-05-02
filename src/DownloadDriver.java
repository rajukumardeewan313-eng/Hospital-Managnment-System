import java.io.InputStream;
 
import java.nio.file.Files;
import java.nio.file.Paths;

public class DownloadDriver {
    public static void main(String[] args) throws Exception {
        String url = "https://github.com/xerial/sqlite-jdbc/releases/download/3.44.0.0/sqlite-jdbc-3.44.0.0.jar";
        String outputPath = "lib/sqlite-jdbc-3.44.0.0.jar";

        System.out.println("Downloading SQLite JDBC Driver...");
        System.out.println("URL: " + url);

        try (InputStream in = java.net.URI.create(url).toURL().openStream()) {
            Files.copy(in, Paths.get(outputPath));
            System.out.println("Download completed!");
            System.out.println("File saved to: " + outputPath);
        } catch (Exception e) {
            System.err.println("Error downloading: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
