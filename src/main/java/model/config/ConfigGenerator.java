package model.config;

import lombok.*;
import model.threads.generator.ReceiverInformationAboutActivation;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ConfigGenerator {

    private double probabilityRandomHeavyFile;
    private double probabilityRandomMediumFile;
    private double probabilityRandomLightFile;
    private int thresholdBetweenLightAndMediumFile;
    private int thresholdBetweenMediumAndHeavyFile;
    private int maxSizeOfFile;
    private int howManyFilesCanBeHaveClient;
    private int howManyClientsCanGenerate;
    private long timeDuration;
    private TimeUnit timeUnitForDuration;
    private long timePauseDuration;
    private TimeUnit timeUnitForPause;


}
