package pl.smarthouse.weathermodule.properties;

import pl.smarthouse.smartmodule.model.actors.type.bme280.Bme280Mode;

public class ActorProperties {
  public static final Bme280Mode I2C_MODE = Bme280Mode.I2C;
  public static final int BME280_ADDRESS = 0x76;
  public static final String BME280 = "bme280";
  public static final String SDS011 = "sds011";
}
