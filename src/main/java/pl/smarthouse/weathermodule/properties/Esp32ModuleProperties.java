package pl.smarthouse.weathermodule.properties;

import lombok.Getter;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class Esp32ModuleProperties {

  // Module specific
  public static final String FIRMWARE = "20240421.15";
  public static final String VERSION = "20240421.15";
  public static final String MAC_ADDRESS = "3C:71:BF:4D:60:00";
  public static final String MODULE_TYPE = "WEATHER";
}
