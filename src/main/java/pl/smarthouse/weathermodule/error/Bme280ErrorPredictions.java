package pl.smarthouse.weathermodule.error;

import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import pl.smarthouse.smartmodule.utils.errorpredictions.Bme280ErrorPredictionsUtils;
import pl.smarthouse.smartmonitoring.service.ErrorHandlingService;
import pl.smarthouse.weathermodule.properties.ActorProperties;
import pl.smarthouse.weathermodule.service.WeatherModuleService;

@Configuration
@RequiredArgsConstructor
public class Bme280ErrorPredictions {

  private final WeatherModuleService weatherModuleService;
  private final ErrorHandlingService errorHandlingService;

  @PostConstruct
  public void postConstructor() {
    Bme280ErrorPredictionsUtils.setBme280SensorErrorPredictions(
        errorHandlingService,
        ActorProperties.BME280,
        weatherModuleService::getBme280SensorResponse);
  }
}
