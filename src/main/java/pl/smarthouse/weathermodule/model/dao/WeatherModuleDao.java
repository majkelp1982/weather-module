package pl.smarthouse.weathermodule.model.dao;

import lombok.Data;
import lombok.experimental.SuperBuilder;
import pl.smarthouse.sharedobjects.dao.ModuleDao;
import pl.smarthouse.sharedobjects.dto.weather.Sun;
import pl.smarthouse.smartmodule.model.actors.type.bme280.Bme280Response;
import pl.smarthouse.smartmodule.model.actors.type.sds011.Sds011Response;

@Data
@SuperBuilder
public class WeatherModuleDao extends ModuleDao {
  private Sds011Response sds011Response;
  private Bme280Response bme280Response;
  private Sun sun;
}
