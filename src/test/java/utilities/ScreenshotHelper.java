package utilities;

import factory.DriverFactory;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.io.FileHandler;
import java.io.File;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Saves named, timestamped screenshots to the /screenshots folder.
 * Called manually from step definitions when you want to capture
 * a specific moment (e.g. after a filter match).
 */
public class ScreenshotHelper {

    private static final String DIR = "screenshots";

    static {
        File dir = new File(DIR);
        if (!dir.exists()) dir.mkdirs();
    }

    /**
     * Takes a screenshot and saves it under:
     *   screenshots/{name}_{yyyyMMdd_HHmmss}.png
     *
     * @param name  logical label (non-alphanumeric chars replaced with _)
     * @return absolute path of the saved file, or "" on failure
     */
    public static String takeScreenshot(String name) {
        try {
            String ts       = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String safeName = name.replaceAll("[^a-zA-Z0-9]", "_");
            String fileName = safeName + "_" + ts + ".png";

            File src  = ((TakesScreenshot) DriverFactory.getDriver())
                    .getScreenshotAs(OutputType.FILE);
            File dest = Paths.get(DIR, fileName).toFile();
            FileHandler.copy(src, dest);

            System.out.println("Screenshot saved: " + dest.getAbsolutePath());
            return dest.getAbsolutePath();
        } catch (Exception e) {
            System.out.println("Screenshot failed: " + e.getMessage());
            return "";
        }
    }
}