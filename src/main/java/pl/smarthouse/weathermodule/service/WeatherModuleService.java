package pl.smarthouse.weathermodule.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import pl.smarthouse.sharedobjects.dto.weather.WeatherModuleDto;
import pl.smarthouse.smartmodule.model.actors.type.bme280.Bme280Response;
import pl.smarthouse.smartmodule.model.actors.type.sds011.Sds011Response;
import pl.smarthouse.weathermodule.chain.SunCalculation;
import pl.smarthouse.weathermodule.configurations.WeatherModuleConfiguration;

@Service
@RequiredArgsConstructor
public class WeatherModuleService {
  private final WeatherModuleConfiguration weatherModuleConfiguration;
  private final SunCalculation sunCalculation;
  private final ModelMapper modelMapper = new ModelMapper();

  public WeatherModuleDto getWeatherModule() {
    sunCalculation.calculateSunState();
    return modelMapper.map(
        weatherModuleConfiguration.getWeatherModuleDao(), WeatherModuleDto.class);
  }

  public void setBme280Sensor(final Bme280Response bme280Response) {
    weatherModuleConfiguration.getWeatherModuleDao().setBme280Response(bme280Response);
  }

  public void setSds011Response(final Sds011Response sds011Response) {
    weatherModuleConfiguration.getWeatherModuleDao().setSds011Response(sds011Response);
  }

  public Bme280Response getBme280SensorResponse() {
    return weatherModuleConfiguration.getWeatherModuleDao().getBme280Response();
  }
}
