package pl.smarthouse.weathermodule.chain;

import static pl.smarthouse.weathermodule.properties.ActorProperties.SDS011;

import java.util.Objects;
import java.util.function.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.smarthouse.smartchain.model.core.Chain;
import pl.smarthouse.smartchain.model.core.Step;
import pl.smarthouse.smartchain.service.ChainService;
import pl.smarthouse.smartchain.utils.PredicateUtils;
import pl.smarthouse.smartmodule.model.actors.type.sds011.Sds011;
import pl.smarthouse.smartmodule.model.actors.type.sds011.Sds011CommandType;
import pl.smarthouse.smartmodule.model.actors.type.sds011.Sds011State;
import pl.smarthouse.weathermodule.configurations.Esp32ModuleConfig;
import pl.smarthouse.weathermodule.service.WeatherModuleService;

@Service
public class Sds011Chain {
  private final Sds011 sds011;
  private final WeatherModuleService weatherModuleService;

  public Sds011Chain(
      @Autowired final Esp32ModuleConfig esp32ModuleConfig,
      @Autowired final WeatherModuleService weatherModuleService,
      @Autowired final ChainService chainService) {
    sds011 = (Sds011) esp32ModuleConfig.getConfiguration().getActorMap().getActor(SDS011);
    this.weatherModuleService = weatherModuleService;
    final Chain chain = createChain();
    chainService.addChain(chain);
  }

  private Chain createChain() {

    final Chain chain = new Chain("SDS011");
    // Wait 10 minutes and wakeup sensor
    chain.addStep(wait10MinutesAndWakeupSensor());
    // Wait until wakeup and wait 20 seconds
    chain.addStep(waitUntilWakeUpAndSendNoAction());
    // Wait 20 seconds and read sensor values
    chain.addStep(wait20SecondsAndReadSensorValues());
    // Wait until command read successful and sleep sensor
    chain.addStep(waitForResponseAndSaveResponseAndSleepSensor());
    // Wait until sleep successful and set command NO_ACTION
    chain.addStep(waitForResponseAndNoAction());
    return chain;
  }

  private Step wait10MinutesAndWakeupSensor() {

    return Step.builder()
        .stepDescription("Wakeup sensor")
        .conditionDescription("Waiting 10 minutes")
        .condition(PredicateUtils.delaySeconds(10 * 60))
        .action(sendWakeupSensor())
        .build();
  }

  private Runnable sendWakeupSensor() {
    return () -> {
      sds011.getCommandSet().setCommandType(Sds011CommandType.MODE);
      sds011.getCommandSet().setValue(Sds011State.WAKEUP.toString());
    };
  }

  private Step waitUntilWakeUpAndSendNoAction() {

    return Step.builder()
        .stepDescription("send no_action")
        .conditionDescription("Waiting for sensor to wakeup")
        .condition(isSds011Wakeup())
        .action(setNoAction())
        .build();
  }

  private Step wait20SecondsAndReadSensorValues() {

    return Step.builder()
        .stepDescription("Read sensor values")
        .conditionDescription("Wait 20 seconds")
        .condition(PredicateUtils.delaySeconds(20))
        .action(sendReadSds011Values())
        .build();
  }

  private Runnable sendReadSds011Values() {
    return () -> sds011.getCommandSet().setCommandType(Sds011CommandType.READ);
  }

  private Step waitForResponseAndSaveResponseAndSleepSensor() {
    return Step.builder()
        .stepDescription("Sleep sensor")
        .conditionDescription("Wait until command read successful")
        .condition(PredicateUtils.isResponseUpdated(sds011))
        .action(sendSleepSensor())
        .build();
  }

  public Predicate<Step> isSds011Wakeup() {
    return step ->
        Objects.isNull(sds011.getResponse())
            ? false
            : Sds011State.WAKEUP.equals(sds011.getResponse().getMode());
  }

  private Runnable sendSleepSensor() {
    return () -> {
      sds011.getCommandSet().setCommandType(Sds011CommandType.MODE);
      sds011.getCommandSet().setValue(Sds011State.SLEEP.toString());
    };
  }

  private Step waitForResponseAndNoAction() {
    return Step.builder()
        .stepDescription("Set SDS011 command to NO_ACTION")
        .conditionDescription("Wait until sleep command successful")
        .condition(isSds011Sleep())
        .action(setNoAction())
        .build();
  }

  public Predicate<Step> isSds011Sleep() {
    return step -> Sds011State.SLEEP.equals(sds011.getResponse().getMode());
  }

  private Runnable setNoAction() {
    return () -> {
      sds011.getCommandSet().setCommandType(Sds011CommandType.NO_ACTION);
      weatherModuleService.setSds011Response(sds011.getResponse());
    };
  }
}
