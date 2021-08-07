package apiHelperExample;

import java.io.IOException;
import java.util.Properties;

/**
 * Loads test suite configuration from resource files.
 */
public class SuiteConfiguration {

  private Properties properties;

  public SuiteConfiguration() throws IOException {
  	this(System.getProperty("application.properties"));
  }

  public SuiteConfiguration(String fromResource) throws IOException {
    properties = new Properties();
    properties.load(SuiteConfiguration.class.getResourceAsStream(fromResource));
  }

  public String getProperty(String name) {
    return properties.getProperty(name);
  }
}
