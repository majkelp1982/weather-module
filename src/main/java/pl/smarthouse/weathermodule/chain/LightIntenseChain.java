package pl.smarthouse.weathermodule.chain;

import static pl.smarthouse.weathermodule.properties.ActorProperties.LIGHT_INTENSE;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.smarthouse.smartchain.model.core.Chain;
import pl.smarthouse.smartchain.model.core.Step;
import pl.smarthouse.smartchain.service.ChainService;
import pl.smarthouse.smartchain.utils.PredicateUtils;
import pl.smarthouse.smartmodule.model.actors.type.bme280.Bme280CommandType;
import pl.smarthouse.smartmodule.model.actors.type.pin.Pin;
import pl.smarthouse.weathermodule.configurations.Esp32ModuleConfig;
import pl.smarthouse.weathermodule.service.WeatherModuleService;

@Service
public class LightIntenseChain {
  private final Pin pin;
  private final WeatherModuleService weatherModuleService;

  public LightIntenseChain(
      @Autowired final Esp32ModuleConfig esp32ModuleConfig,
      @Autowired final WeatherModuleService weatherModuleService,
      @Autowired final ChainService chainService) {
    pin = (Pin) esp32ModuleConfig.getConfiguration().getActorMap().getActor(LIGHT_INTENSE);
    this.weatherModuleService = weatherModuleService;
    final Chain chain = createChain();
    chainService.addChain(chain);
  }

  private Chain createChain() {

    final Chain chain = new Chain("Read lightIntense sensor");
    // Wait 30 seconds and read values from light intense pin
    chain.addStep(wait30secondsAndReadPinValue());
    // Wait until command read successful and set command NO_ACTION
    chain.addStep(waitForResponseAndSetNoAction());
    return chain;
  }

  private Step wait30secondsAndReadPinValue() {

    return Step.builder()
        .stepDescription("Read value from light intense pin")
        .conditionDescription("Waiting 30 seconds")
        .condition(PredicateUtils.delaySeconds(30))
        .action(sendReadCommand())
        .build();
  }

  private Runnable sendReadCommand() {
    return () -> pin.getCommandSet().setCommandType(Bme280CommandType.READ);
  }

  private Step waitForResponseAndSetNoAction() {
    return Step.builder()
        .stepDescription("Set Pin command to NO_ACTION")
        .conditionDescription("Wait until command read successful")
        .condition(PredicateUtils.isResponseUpdated(pin))
        .action(setNoAction())
        .build();
  }

  private Runnable setNoAction() {
    return () -> {
      weatherModuleService.convertToPercentAndSetLightIntense(pin.getResponse());
      pin.getCommandSet().setCommandType(Bme280CommandType.NO_ACTION);
    };
  }
}
