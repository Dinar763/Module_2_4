package utill;

import org.flywaydb.core.Flyway;
import org.hibernate.cfg.Configuration;

import java.io.PrintStream;

public class FlyWayMigrateUtill {
    public static void applyMigrations() {
        try {
            System.setOut(new PrintStream(System.out, true, "UTF-8"));
            System.setErr(new PrintStream(System.err, true, "UTF-8"));
            Configuration hiberConfig = new Configuration().configure();

            String url = hiberConfig.getProperty("hibernate.connection.url");
            String user = hiberConfig.getProperty("hibernate.connection.username");
            String password = hiberConfig.getProperty("hibernate.connection.password");

            Flyway flyway = Flyway.configure()
                                  .dataSource(url, user, password)
                                  .locations("classpath:db/migration")
                                  .baselineOnMigrate(true)
                                  .baselineVersion("0")
                                  .load();

            flyway.migrate();
            System.out.println("Миграции Flyway успешно применены");
        } catch (Exception e) {
            System.out.println("Ошибка в Flyway: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
