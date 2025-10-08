package listener;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import utill.FlyWayMigrateUtill;

import java.io.PrintStream;

@WebListener
public class FlywayMigrationListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        setupConsoleEncoding();
        System.out.println("Запуск Flyway миграций...");
        FlyWayMigrateUtill.applyMigrations();
        System.out.println("Flyway миграции завершены");
    }

    private void setupConsoleEncoding() {
        try {
            System.setOut(new PrintStream(System.out, true, "UTF-8"));
            System.setErr(new PrintStream(System.err, true, "UTF-8"));
            System.setProperty("console.encoding", "UTF-8");
        } catch (Exception e) {
            System.err.println("ERROR: Failed to set UTF-8 encoding: " + e.getMessage());
        }
    }
}
