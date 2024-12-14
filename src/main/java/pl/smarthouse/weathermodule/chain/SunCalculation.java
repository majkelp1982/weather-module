package pl.smarthouse.weathermodule.chain;

import java.time.LocalDateTime;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.shredzone.commons.suncalc.SunTimes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pl.smarthouse.sharedobjects.dto.weather.Sun;
import pl.smarthouse.sharedobjects.dto.weather.SunState;
import pl.smarthouse.weathermodule.configurations.WeatherModuleConfiguration;

@Service
@RequiredArgsConstructor
@EnableScheduling
public class SunCalculation {
  private final WeatherModuleConfiguration weatherModuleConfiguration;

  private static final Logger log = LoggerFactory.getLogger(SunCalculation.class);

  @Scheduled(fixedDelay = 10 * 60 * 1000)
  void calculateSunState() {
    LocalDateTime now = LocalDateTime.now();
    SunTimes times = SunTimes.compute().on(now).at(50.2584100, 19.02754).execute();

    Sun sun = weatherModuleConfiguration.getWeatherModuleDao().getSun();
    sun.setSunRise(Objects.requireNonNull(times.getRise()).toLocalTime());
    sun.setSunSet(Objects.requireNonNull(times.getSet()).toLocalTime());
    sun.setSunState(
        (now.isAfter(sun.getSunRise().atDate(now.toLocalDate()))
                && now.isBefore(sun.getSunSet().atDate(now.toLocalDate())))
            ? SunState.RISE
            : SunState.SET);
    log.info("Calculating sun times finished. Sun: {}", sun);
  }
}
