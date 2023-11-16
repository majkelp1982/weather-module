package pl.smarthouse.weathermodule.error;

import java.time.LocalTime;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import pl.smarthouse.sharedobjects.dto.core.TimeRange;
import pl.smarthouse.smartmodule.utils.errorpredictions.PinErrorPredictionsUtils;
import pl.smarthouse.smartmonitoring.service.ErrorHandlingService;
import pl.smarthouse.weathermodule.service.WeatherModuleService;
import pl.smarthouse.weathermodule.utils.TimeRangeUtils;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class LightIntenseErrorPredictions {
  // Error messages
  private static final String LIGHT_INTENSE = "LIGHT_INTENSE";

  private static final TimeRange night =
      TimeRange.builder().from(LocalTime.of(23, 30)).to(LocalTime.of(4, 0)).build();
  private static final TimeRange day =
      TimeRange.builder().from(LocalTime.of(8, 0)).to(LocalTime.of(15, 0)).build();

  private final WeatherModuleService weatherModuleService;
  private final ErrorHandlingService errorHandlingService;

  @PostConstruct
  public void postConstructor() {
    PinErrorPredictionsUtils.setPinErrorPredictions(
        errorHandlingService,
        LIGHT_INTENSE,
        isResultValid(weatherModuleService.getLightIntenseSensorResponse().getPinValue()),
        weatherModuleService::getLightIntenseSensorResponse);
  }

  private boolean isResultValid(final int lightIntense) {
    if (TimeRangeUtils.inTimeRange(day) && lightIntense <= 0) {
      return false;
    }
    if (TimeRangeUtils.inTimeRange(night) && lightIntense > 0) {
      return false;
    }
    return true;
  }
}
