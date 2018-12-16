package info.smartkit.dl4j;

import org.apache.commons.lang.SystemUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

import info.smartkit.dl4j.configs.PropertiesInitializer;

@PropertySources({ @PropertySource(value = "classpath:application-${spring.profiles.active}.properties") })
//@Configuration
//@EnableAutoConfiguration
//@ComponentScan
@SpringBootApplication
public class Application {
	
	private static Logger LOG = LogManager.getLogger(Application.class);

	//
	private static Class<Application> applicationClass = Application.class;

	//
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		//
		return application.sources(applicationClass);
	}

	public static void main(String[] args) {
		// SpringApplication.run(Application.class, args);
		//
		ConfigurableApplicationContext context = new SpringApplicationBuilder(applicationClass)
				.initializers(new PropertiesInitializer()).run(args);
		LOG.info("ApplicationContext:" + context.getDisplayName() + context.getStartupDate());
		//
		LOG.info("OS_NAME:" + SystemUtils.OS_NAME);
		//Please make sure the TESSDATA_PREFIX environment variable is set to the parent directory of your "tessdata" directory.
//		Properties props = System.getProperties();
//		props.setProperty("TESSDATA_PREFIX", "/Users/yangboz/git/laughing-bear/src/main/resources/tessdata/");

	}
}