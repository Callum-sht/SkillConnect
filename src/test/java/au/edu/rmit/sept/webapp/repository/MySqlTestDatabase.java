package au.edu.rmit.sept.webapp.repository;

import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

final class MySqlTestDatabase {

    private static final String DATABASE_NAME = "skillconnect_test";
    private static final String DEFAULT_URL =
            "jdbc:mysql://localhost:3306/" + DATABASE_NAME;

    private MySqlTestDatabase() {
    }

    static JdbcTemplate createJdbcTemplate() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl(optionalEnv(
                "SKILLCONNECT_TEST_DB_URL", DEFAULT_URL));
        dataSource.setUsername(requiredEnv("SKILLCONNECT_TEST_DB_USERNAME"));
        dataSource.setPassword(requiredEnvAllowingBlank(
                "SKILLCONNECT_TEST_DB_PASSWORD"));

        new ResourceDatabasePopulator(new ClassPathResource("schema.sql"))
                .execute(dataSource);

        return new JdbcTemplate(dataSource);
    }

    static void clean(JdbcTemplate jdbcTemplate) {
        String databaseName = jdbcTemplate.queryForObject(
                "SELECT DATABASE()",
                String.class);
        if (!DATABASE_NAME.equals(databaseName)) {
            throw new IllegalStateException(
                    "Refusing to clean non-test database: " + databaseName);
        }

        jdbcTemplate.update("DELETE FROM message");
        jdbcTemplate.update("DELETE FROM conversation");
        jdbcTemplate.update("DELETE FROM review");
        jdbcTemplate.update("DELETE FROM booking");
        jdbcTemplate.update("DELETE FROM service_listings");
        jdbcTemplate.update("DELETE FROM seeker_account");
        jdbcTemplate.update("DELETE FROM provider_account");
        jdbcTemplate.update("DELETE FROM users");
    }

    private static String requiredEnv(String name) {
        String value = System.getenv(name);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Set " + name
                    + " to run MySQL repository integration tests.");
        }
        return value;
    }

    private static String requiredEnvAllowingBlank(String name) {
        String value = System.getenv(name);
        if (value == null) {
            throw new IllegalStateException("Set " + name
                    + " to run MySQL repository integration tests.");
        }
        return value;
    }

    private static String optionalEnv(String name, String defaultValue) {
        String value = System.getenv(name);
        return value == null || value.isBlank() ? defaultValue : value;
    }
}
