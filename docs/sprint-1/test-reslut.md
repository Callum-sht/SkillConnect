WARNING: A restricted method in java.lang.System has been called
WARNING: java.lang.System::load has been called by org.fusesource.jansi.internal.JansiLoader in an unnamed module (file:/Users/callumshen/.m2/wrapper/dists/apache-maven-3.9.7/2a4cb831/lib/jansi-2.4.1.jar)
WARNING: Use --enable-native-access=ALL-UNNAMED to avoid a warning for callers in this module
WARNING: Restricted methods will be blocked in a future release unless native access is enabled

WARNING: A terminally deprecated method in sun.misc.Unsafe has been called
WARNING: sun.misc.Unsafe::objectFieldOffset has been called by com.google.common.util.concurrent.AbstractFuture$UnsafeAtomicHelper (file:/Users/callumshen/.m2/wrapper/dists/apache-maven-3.9.7/2a4cb831/lib/guava-33.2.0-jre.jar)
WARNING: Please consider reporting this to the maintainers of class com.google.common.util.concurrent.AbstractFuture$UnsafeAtomicHelper
WARNING: sun.misc.Unsafe::objectFieldOffset will be removed in a future release
WARNING: Final field _cipher in class org.sonatype.plexus.components.sec.dispatcher.DefaultSecDispatcher has been mutated reflectively by class org.eclipse.sisu.bean.BeanPropertyField in unnamed module @143640d5 (file:/Users/callumshen/.m2/wrapper/dists/apache-maven-3.9.7/2a4cb831/lib/org.eclipse.sisu.inject-0.9.0.M2.jar)
WARNING: Use --enable-final-field-mutation=ALL-UNNAMED to avoid a warning
WARNING: Mutating final fields will be blocked in a future release unless final field mutation is enabled
[INFO] Scanning for projects...
[INFO] 
[INFO] ----------------------< au.edu.rmit.sept:webapp >-----------------------
[INFO] Building webapp 0.0.1-SNAPSHOT
[INFO]   from pom.xml
[INFO] --------------------------------[ jar ]---------------------------------
[INFO] 
[INFO] --- resources:3.3.1:resources (default-resources) @ webapp ---
[INFO] Copying 1 resource from src/main/resources to target/classes
[INFO] Copying 2 resources from src/main/resources to target/classes
[INFO] 
[INFO] --- compiler:3.13.0:compile (default-compile) @ webapp ---
[INFO] Nothing to compile - all classes are up to date.
[INFO] 
[INFO] --- resources:3.3.1:testResources (default-testResources) @ webapp ---
[INFO] skip non existing resourceDirectory /Users/callumshen/sept-2026-group-project-callum-sht/src/test/resources
[INFO] 
[INFO] --- compiler:3.13.0:testCompile (default-testCompile) @ webapp ---
[INFO] Nothing to compile - all classes are up to date.
[INFO] 
[INFO] --- surefire:3.2.5:test (default-test) @ webapp ---
[INFO] Using auto detected provider org.apache.maven.surefire.junitplatform.JUnitPlatformProvider
[INFO] 
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running au.edu.rmit.sept.webapp.repository.UserStatusPersistenceTest
23:09:52.071 [main] INFO org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseFactory -- Starting embedded database: url='jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=false', username='sa'
23:09:52.177 [main] INFO org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseFactory -- Shutting down embedded database: url='jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=false'
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.154 s -- in au.edu.rmit.sept.webapp.repository.UserStatusPersistenceTest
[INFO] Running au.edu.rmit.sept.webapp.repository.RepositoryIntegrationTest
23:09:52.183 [main] INFO org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseFactory -- Starting embedded database: url='jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=false', username='sa'
23:09:52.211 [main] INFO org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseFactory -- Shutting down embedded database: url='jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=false'
23:09:52.213 [main] INFO org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseFactory -- Starting embedded database: url='jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=false', username='sa'
23:09:52.227 [main] INFO org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseFactory -- Shutting down embedded database: url='jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=false'
23:09:52.229 [main] INFO org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseFactory -- Starting embedded database: url='jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=false', username='sa'
23:09:52.241 [main] INFO org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseFactory -- Shutting down embedded database: url='jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=false'
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.059 s -- in au.edu.rmit.sept.webapp.repository.RepositoryIntegrationTest
[INFO] Running au.edu.rmit.sept.webapp.controller.AuthControllerTest
23:09:52.416 [main] INFO org.springframework.mock.web.MockServletContext -- Initializing Spring TestDispatcherServlet ''
23:09:52.416 [main] INFO org.springframework.test.web.servlet.TestDispatcherServlet -- Initializing Servlet ''
23:09:52.417 [main] INFO org.springframework.test.web.servlet.TestDispatcherServlet -- Completed initialization in 1 ms
23:09:52.474 [main] INFO org.springframework.mock.web.MockServletContext -- Initializing Spring TestDispatcherServlet ''
23:09:52.474 [main] INFO org.springframework.test.web.servlet.TestDispatcherServlet -- Initializing Servlet ''
23:09:52.474 [main] INFO org.springframework.test.web.servlet.TestDispatcherServlet -- Completed initialization in 0 ms
23:09:52.481 [main] INFO org.springframework.mock.web.MockServletContext -- Initializing Spring TestDispatcherServlet ''
23:09:52.481 [main] INFO org.springframework.test.web.servlet.TestDispatcherServlet -- Initializing Servlet ''
23:09:52.481 [main] INFO org.springframework.test.web.servlet.TestDispatcherServlet -- Completed initialization in 0 ms
23:09:52.489 [main] INFO org.springframework.mock.web.MockServletContext -- Initializing Spring TestDispatcherServlet ''
23:09:52.490 [main] INFO org.springframework.test.web.servlet.TestDispatcherServlet -- Initializing Servlet ''
23:09:52.490 [main] INFO org.springframework.test.web.servlet.TestDispatcherServlet -- Completed initialization in 0 ms
23:09:52.496 [main] INFO org.springframework.mock.web.MockServletContext -- Initializing Spring TestDispatcherServlet ''
23:09:52.497 [main] INFO org.springframework.test.web.servlet.TestDispatcherServlet -- Initializing Servlet ''
23:09:52.497 [main] INFO org.springframework.test.web.servlet.TestDispatcherServlet -- Completed initialization in 0 ms
[INFO] Tests run: 5, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.279 s -- in au.edu.rmit.sept.webapp.controller.AuthControllerTest
[INFO] Running au.edu.rmit.sept.webapp.controller.SeekerPageControllerTest
23:09:52.530 [main] INFO org.springframework.mock.web.MockServletContext -- Initializing Spring TestDispatcherServlet ''
23:09:52.530 [main] INFO org.springframework.test.web.servlet.TestDispatcherServlet -- Initializing Servlet ''
23:09:52.530 [main] INFO org.springframework.test.web.servlet.TestDispatcherServlet -- Completed initialization in 0 ms
23:09:52.536 [main] INFO org.springframework.mock.web.MockServletContext -- Initializing Spring TestDispatcherServlet ''
23:09:52.536 [main] INFO org.springframework.test.web.servlet.TestDispatcherServlet -- Initializing Servlet ''
23:09:52.537 [main] INFO org.springframework.test.web.servlet.TestDispatcherServlet -- Completed initialization in 0 ms
23:09:52.545 [main] INFO org.springframework.mock.web.MockServletContext -- Initializing Spring TestDispatcherServlet ''
23:09:52.545 [main] INFO org.springframework.test.web.servlet.TestDispatcherServlet -- Initializing Servlet ''
23:09:52.545 [main] INFO org.springframework.test.web.servlet.TestDispatcherServlet -- Completed initialization in 0 ms
23:09:52.549 [main] INFO org.springframework.mock.web.MockServletContext -- Initializing Spring TestDispatcherServlet ''
23:09:52.549 [main] INFO org.springframework.test.web.servlet.TestDispatcherServlet -- Initializing Servlet ''
23:09:52.549 [main] INFO org.springframework.test.web.servlet.TestDispatcherServlet -- Completed initialization in 0 ms
23:09:52.552 [main] INFO org.springframework.mock.web.MockServletContext -- Initializing Spring TestDispatcherServlet ''
23:09:52.552 [main] INFO org.springframework.test.web.servlet.TestDispatcherServlet -- Initializing Servlet ''
23:09:52.553 [main] INFO org.springframework.test.web.servlet.TestDispatcherServlet -- Completed initialization in 1 ms
[INFO] Tests run: 5, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.033 s -- in au.edu.rmit.sept.webapp.controller.SeekerPageControllerTest
[INFO] Running au.edu.rmit.sept.webapp.controller.AdminUserControllerTest
23:09:52.562 [main] INFO org.springframework.mock.web.MockServletContext -- Initializing Spring TestDispatcherServlet ''
23:09:52.563 [main] INFO org.springframework.test.web.servlet.TestDispatcherServlet -- Initializing Servlet ''
23:09:52.563 [main] INFO org.springframework.test.web.servlet.TestDispatcherServlet -- Completed initialization in 0 ms
23:09:52.569 [main] INFO org.springframework.mock.web.MockServletContext -- Initializing Spring TestDispatcherServlet ''
23:09:52.569 [main] INFO org.springframework.test.web.servlet.TestDispatcherServlet -- Initializing Servlet ''
23:09:52.569 [main] INFO org.springframework.test.web.servlet.TestDispatcherServlet -- Completed initialization in 0 ms
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.013 s -- in au.edu.rmit.sept.webapp.controller.AdminUserControllerTest
[INFO] Running au.edu.rmit.sept.webapp.WebappApplicationTests
23:09:52.590 [main] INFO org.springframework.test.context.support.AnnotationConfigContextLoaderUtils -- Could not detect default configuration classes for test class [au.edu.rmit.sept.webapp.WebappApplicationTests]: WebappApplicationTests does not declare any static, non-private, non-final, nested classes annotated with @Configuration.
23:09:52.630 [main] INFO org.springframework.boot.test.context.SpringBootTestContextBootstrapper -- Found @SpringBootConfiguration au.edu.rmit.sept.webapp.WebappApplication for test class au.edu.rmit.sept.webapp.WebappApplicationTests

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/

 :: Spring Boot ::                (v3.3.2)

2026-09-20T23:09:52.741+10:00  INFO 21471 --- [webapp] [           main] a.e.r.s.webapp.WebappApplicationTests    : Starting WebappApplicationTests using Java 26.0.2.1 with PID 21471 (started by callumshen in /Users/callumshen/sept-2026-group-project-callum-sht)
2026-09-20T23:09:52.742+10:00  INFO 21471 --- [webapp] [           main] a.e.r.s.webapp.WebappApplicationTests    : No active profile set, falling back to 1 default profile: "default"
2026-09-20T23:09:53.056+10:00  INFO 21471 --- [webapp] [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Starting...
2026-09-20T23:09:53.062+10:00  INFO 21471 --- [webapp] [           main] com.zaxxer.hikari.pool.HikariPool        : HikariPool-1 - Added connection conn49: url=jdbc:h2:mem:skillconnect user=SA
2026-09-20T23:09:53.063+10:00  INFO 21471 --- [webapp] [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Start completed.
WARNING: A restricted method in java.lang.System has been called
WARNING: java.lang.System::load has been called by org.apache.tomcat.jni.Library in an unnamed module (file:/Users/callumshen/.m2/repository/org/apache/tomcat/embed/tomcat-embed-core/10.1.26/tomcat-embed-core-10.1.26.jar)
WARNING: Use --enable-native-access=ALL-UNNAMED to avoid a warning for callers in this module
WARNING: Restricted methods will be blocked in a future release unless native access is enabled

2026-09-20T23:09:53.278+10:00  WARN 21471 --- [webapp] [           main] ion$DefaultTemplateResolverConfiguration : Cannot find template location: classpath:/templates/ (please add some templates, check your Thymeleaf configuration, or set spring.thymeleaf.check-template-location=false)
2026-09-20T23:09:53.292+10:00  INFO 21471 --- [webapp] [           main] o.s.b.a.h2.H2ConsoleAutoConfiguration    : H2 console available at '/h2-console'. Database available at 'jdbc:h2:mem:skillconnect'
2026-09-20T23:09:53.316+10:00  INFO 21471 --- [webapp] [           main] a.e.r.s.webapp.WebappApplicationTests    : Started WebappApplicationTests in 0.659 seconds (process running for 1.503)
OpenJDK 64-Bit Server VM warning: Sharing is only supported for boot loader classes because bootstrap classpath has been appended
WARNING: A Java agent has been loaded dynamically (/Users/callumshen/.m2/repository/net/bytebuddy/byte-buddy-agent/1.14.18/byte-buddy-agent-1.14.18.jar)
WARNING: If a serviceability tool is in use, please run with -XX:+EnableDynamicAgentLoading to hide this warning
WARNING: If a serviceability tool is not in use, please run with -Djdk.instrument.traceUsage for more information
WARNING: Dynamic loading of agents will be disallowed by default in a future release
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 1.069 s -- in au.edu.rmit.sept.webapp.WebappApplicationTests
[INFO] Running au.edu.rmit.sept.webapp.service.UserServiceTest
[INFO] Tests run: 7, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.004 s -- in au.edu.rmit.sept.webapp.service.UserServiceTest
[INFO] Running au.edu.rmit.sept.webapp.service.ProviderServiceTest
[INFO] Tests run: 14, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.007 s -- in au.edu.rmit.sept.webapp.service.ProviderServiceTest
[INFO] Running au.edu.rmit.sept.webapp.service.BookingServiceTest
[INFO] Tests run: 4, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.001 s -- in au.edu.rmit.sept.webapp.service.BookingServiceTest
[INFO] Running au.edu.rmit.sept.webapp.service.AuthServiceTest
[INFO] Tests run: 11, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.653 s -- in au.edu.rmit.sept.webapp.service.AuthServiceTest
[INFO] Running au.edu.rmit.sept.webapp.service.ServiceListingServiceTest
[INFO] Tests run: 10, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.015 s -- in au.edu.rmit.sept.webapp.service.ServiceListingServiceTest
[INFO] Running au.edu.rmit.sept.webapp.service.SeekerServiceTest
[INFO] Tests run: 13, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.008 s -- in au.edu.rmit.sept.webapp.service.SeekerServiceTest
[INFO] 
[INFO] Results:
[INFO] 
[INFO] Tests run: 76, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  3.242 s
[INFO] Finished at: 2026-09-20T23:09:54+10:00
[INFO] ------------------------------------------------------------------------