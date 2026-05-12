package utilities;

import factory.DriverFactory;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.io.FileHandler;
import java.io.File;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ScreenshotHelper {

    private static final String DIR = "screenshots";

    static {
        File dir = new File(DIR);
        if (!dir.exists()) dir.mkdirs();
    }

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