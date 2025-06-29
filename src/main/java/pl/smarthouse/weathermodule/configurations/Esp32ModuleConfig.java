package pl.smarthouse.weathermodule.configurations;

import static pl.smarthouse.weathermodule.properties.ActorProperties.*;
import static pl.smarthouse.weathermodule.properties.Esp32ModuleProperties.*;

import javax.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import pl.smarthouse.smartmodule.model.actors.actor.ActorMap;
import pl.smarthouse.smartmodule.model.actors.type.bme280.Bme280;
import pl.smarthouse.smartmodule.model.actors.type.sds011.Sds011;
import pl.smarthouse.smartmodule.services.ManagerService;
import pl.smarthouse.smartmodule.services.ModuleService;

@Configuration
@RequiredArgsConstructor
@Getter
public class Esp32ModuleConfig {
  private final ModuleService moduleService;
  private final ManagerService managerService;

  // Module specific
  private pl.smarthouse.smartmodule.model.configuration.Configuration configuration;

  @PostConstruct
  public void postConstruct() {
    configuration =
        new pl.smarthouse.smartmodule.model.configuration.Configuration(
            MODULE_TYPE, FIRMWARE, VERSION, MAC_ADDRESS, createActors());
    moduleService.setConfiguration(configuration);
    managerService.setConfiguration(configuration);
  }

  private ActorMap createActors() {
    final ActorMap actorMap = new ActorMap();
    actorMap.putActor(new Bme280(BME280, I2C_MODE, BME280_ADDRESS));
    actorMap.putActor(new Sds011(SDS011));
    return actorMap;
  }
}
