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

    private int probabilityRandomHeavyFile;
    private int probabilityRandomMediumFile;
    private int probabilityRandomLightFile;
    private int thresholdBetweenLightAndMediumFile;
    private int thresholdBetweenMediumAndHeavyFile;
    private int maxSizeOfFile;
    private int howManyFilesCanBeHaveClient;
    private int howManyClientsCanGenerate;
    private long timeDuration;
    private TimeUnit timeUnitForDuration;
    private long timePauseDuration;
    private TimeUnit timeUnitForPause;

    @Override
    public String toString() {
        return "ConfigGenerator{" +
                "probabilityRandomHeavyFile=" + probabilityRandomHeavyFile +
                ", probabilityRandomMediumFile=" + probabilityRandomMediumFile +
                ", probabilityRandomLightFile=" + probabilityRandomLightFile +
                ", thresholdBetweenLightAndMediumFile=" + thresholdBetweenLightAndMediumFile +
                ", thresholdBetweenMediumAndHeavyFile=" + thresholdBetweenMediumAndHeavyFile +
                ", maxSizeOfFile=" + maxSizeOfFile +
                ", howManyFilesCanBeHaveClient=" + howManyFilesCanBeHaveClient +
                ", howManyClientsCanGenerate=" + howManyClientsCanGenerate +
                ", timeDuration=" + timeDuration +
                ", timeUnitForDuration=" + timeUnitForDuration +
                ", timePauseDuration=" + timePauseDuration +
                ", timeUnitForPause=" + timeUnitForPause +
                '}';
    }
}
