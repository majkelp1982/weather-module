package pl.smarthouse.weathermodule.configurations;

import javax.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import pl.smarthouse.smartmodule.model.actors.type.bme280.Bme280Response;
import pl.smarthouse.smartmodule.model.actors.type.pin.PinResponse;
import pl.smarthouse.smartmodule.model.actors.type.pin.PinState;
import pl.smarthouse.smartmodule.model.actors.type.sds011.Sds011Response;
import pl.smarthouse.smartmodule.model.actors.type.sds011.Sds011State;
import pl.smarthouse.smartmonitoring.model.BooleanCompareProperties;
import pl.smarthouse.smartmonitoring.model.EnumCompareProperties;
import pl.smarthouse.smartmonitoring.model.NumberCompareProperties;
import pl.smarthouse.smartmonitoring.properties.defaults.Bme280DefaultProperties;
import pl.smarthouse.smartmonitoring.service.CompareProcessor;
import pl.smarthouse.smartmonitoring.service.MonitoringService;
import pl.smarthouse.weathermodule.model.dao.WeatherModuleDao;
import pl.smarthouse.weathermodule.properties.Esp32ModuleProperties;

@Configuration
@RequiredArgsConstructor
@Getter
@Slf4j
public class WeatherModuleConfiguration {
  private final CompareProcessor compareProcessor;
  private final MonitoringService monitoringService;
  private final Esp32ModuleConfig esp32ModuleConfig;
  private final Esp32ModuleProperties esp32ModuleProperties;
  private WeatherModuleDao weatherModuleDao;

  @PostConstruct
  void postConstruct() {
    final Bme280Response sensor = new Bme280Response();
    sensor.setError(true);
    final Sds011Response sds011Response = new Sds011Response();
    sds011Response.setMode(Sds011State.SLEEP);
    final PinResponse pinResponse = new PinResponse();
    pinResponse.setPinState(PinState.HIGH);
    pinResponse.setPinDefaultState(PinState.HIGH);
    weatherModuleDao =
        WeatherModuleDao.builder()
            .type(Esp32ModuleProperties.MODULE_TYPE)
            .bme280Response(new Bme280Response())
            .sds011Response(sds011Response)
            .lightIntense(pinResponse)
            .build();
    monitoringService.setModuleDaoObject(weatherModuleDao);
    setCompareProperties();
  }

  private void setCompareProperties() {
    compareProcessor.addMap("error", BooleanCompareProperties.builder().saveEnabled(true).build());
    compareProcessor.addMap(
        "errorPendingAcknowledge", BooleanCompareProperties.builder().saveEnabled(true).build());
    Bme280DefaultProperties.setDefaultProperties(compareProcessor, "bme280Response");
    compareProcessor.addMap(
        "sds011Response.error", BooleanCompareProperties.builder().saveEnabled(true).build());
    compareProcessor.addMap(
        "sds011Response.mode", EnumCompareProperties.builder().saveEnabled(true).build());
    compareProcessor.addMap(
        "sds011Response.pm025",
        NumberCompareProperties.builder().saveEnabled(true).saveTolerance(5).build());
    compareProcessor.addMap(
        "sds011Response.pm10",
        NumberCompareProperties.builder().saveEnabled(true).saveTolerance(5).build());

    compareProcessor.addMap(
        "lightIntense.error", BooleanCompareProperties.builder().saveEnabled(true).build());
    compareProcessor.addMap(
        "lightIntense.counter", NumberCompareProperties.builder().saveEnabled(false).build());
    compareProcessor.addMap(
        "lightIntense.pinDefaultState", EnumCompareProperties.builder().saveEnabled(false).build());
    compareProcessor.addMap(
        "lightIntense.pinState", EnumCompareProperties.builder().saveEnabled(false).build());
    compareProcessor.addMap(
        "lightIntense.pinValue",
        NumberCompareProperties.builder().saveEnabled(true).saveTolerance(5).build());
  }
}
