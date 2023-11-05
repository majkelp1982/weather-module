package pl.smarthouse.weathermodule.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pl.smarthouse.sharedobjects.dto.weather.WeatherModuleDto;
import pl.smarthouse.weathermodule.service.WeatherModuleService;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping()
@RequiredArgsConstructor
public class WeatherModuleController {
  private final WeatherModuleService weatherModuleService;

  @GetMapping("/weather")
  public Mono<WeatherModuleDto> getWeatherModule() {
    return Mono.just(weatherModuleService.getWeatherModule());
  }
}
