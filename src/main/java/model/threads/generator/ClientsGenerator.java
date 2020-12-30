package model.threads.generator;

import model.Logger;
import model.config.ConfigGenerator;
import model.dataobjects.Client;
import model.dataobjects.File;
import model.enums.TypeFileBySize;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class ClientsGenerator implements Runnable {

    private double probabilityRandomHeavyFile;
    private double probabilityRandomMediumFile;
    private double probabilityRandomLightFile;
    private int thresholdBetweenLightAndMediumFile;
    private int thresholdBetweenMediumAndHeavyFile;
    private int maxSizeOfFile;
    private final long minSizeOfFile = 1;
    private int howManyFilesCanBeHaveClient;
    private int howManyClientsCanGenerate;
    private boolean run = true;
    private long timeDuration;
    private TimeUnit timeUnitForDuration;
    private ReceiverInformationAboutActivation clientsReceiver;
    private LocalDateTime startLocalDataTime;
    private long timePauseDuration;
    private TimeUnit timeUnitForPause;
    private Random random = new Random();

    public ClientsGenerator(ConfigGenerator configGenerator) {

        this.probabilityRandomHeavyFile = configGenerator.getProbabilityRandomHeavyFile() / 100;
        this.probabilityRandomMediumFile = configGenerator.getProbabilityRandomMediumFile() / 100;
        this.probabilityRandomLightFile = configGenerator.getProbabilityRandomLightFile() / 100;
        this.thresholdBetweenLightAndMediumFile = configGenerator.getThresholdBetweenLightAndMediumFile();
        this.thresholdBetweenMediumAndHeavyFile = configGenerator.getThresholdBetweenMediumAndHeavyFile();
        this.maxSizeOfFile = configGenerator.getMaxSizeOfFile();
        this.howManyFilesCanBeHaveClient = configGenerator.getHowManyFilesCanBeHaveClient();
        this.howManyClientsCanGenerate = configGenerator.getHowManyClientsCanGenerate();
        this.timeDuration = configGenerator.getTimeDuration();
        this.timeUnitForDuration = configGenerator.getTimeUnitForDuration();
        this.timePauseDuration = configGenerator.getTimePauseDuration();
        this.timeUnitForPause = configGenerator.getTimeUnitForPause();
        Thread.currentThread().setName("CLIENTS_GENERATOR");
    }

    @Override
    public void run() {
        if (!Optional.ofNullable(startLocalDataTime).isPresent()) {
            startLocalDataTime = LocalDateTime.now();
        }

        LocalDateTime now = LocalDateTime.now();
        final long duration = timeUnitForDuration.toNanos(timeDuration);
        long diff = ChronoUnit.NANOS.between(startLocalDataTime, now);
        while (diff < duration) {
            clientsReceiver.receiveClients(generate());
            clientsReceiver.receiveInfoAboutActive(true);
            sleep(timeUnitForPause.toMillis(timePauseDuration));
            now = LocalDateTime.now();
            diff = ChronoUnit.NANOS.between(startLocalDataTime, now);
        }
        clientsReceiver.receiveInfoAboutActive(false);
        Logger.getInstance().log("Koniec Generowania");
        Logger.getInstance().log("Stop generator thread");
    }

    private void sleep(long milliseconds) {
        if (milliseconds > 0) {
            try {
                TimeUnit.MILLISECONDS.sleep(milliseconds);
            } catch (InterruptedException e) {
                Thread.interrupted();
            }

        }
    }

    private List<Client> generate() {
        List<Client> listGeneratedOfClients = new ArrayList<>();
        int numberOfClients = random.nextInt(howManyClientsCanGenerate - 1) + 1;
        for (int iteratorClient = 0; iteratorClient < numberOfClients; iteratorClient++) {
            List<File> listGeneratedOfFiles = new ArrayList<>();
            int numberOfFiles = random.nextInt(howManyFilesCanBeHaveClient - 1) + 1;
            for (int iteratorFile = 0; iteratorFile < numberOfFiles; iteratorFile++) {
                TypeFileBySize typeOfFile = randomTypeFileBySize();
                int sizeOfFile = randomSizeAccordingWithTypeOfFile(typeOfFile);
                listGeneratedOfFiles.add(new File(sizeOfFile, typeOfFile));
            }
            listGeneratedOfClients.add(new Client(listGeneratedOfFiles));
        }
        return listGeneratedOfClients;
    }

    private int randomSizeAccordingWithTypeOfFile(TypeFileBySize typeFileBySize) {
        int size = 1;
        switch (typeFileBySize) {
            case SMALL: {
                size = random.nextInt(thresholdBetweenLightAndMediumFile);
                break;
            }
            case MEDIUM: {
                size = random.nextInt(thresholdBetweenLightAndMediumFile) + (thresholdBetweenMediumAndHeavyFile - thresholdBetweenLightAndMediumFile);
                break;
            }
            case LARGE: {
                size = random.nextInt(thresholdBetweenMediumAndHeavyFile) + (maxSizeOfFile - thresholdBetweenMediumAndHeavyFile);
                break;
            }
        }
        if (size == 0)
            size++;
        return size;
    }

    private TypeFileBySize randomTypeFileBySize() {
        final double probability = random.nextDouble();
        List<Double> probabilityOfLists = Arrays.asList(probabilityRandomLightFile,
                probabilityRandomLightFile + probabilityRandomMediumFile);
        int index = 0;
        for (Double probabilityForFile : probabilityOfLists) {
            if (probability <= probabilityForFile) {
                return TypeFileBySize.values()[index];
            }
            index++;
        }
        return TypeFileBySize.LARGE;
    }

    public void setClientsReceiver(ReceiverInformationAboutActivation clientsReceiver) {
        this.clientsReceiver = clientsReceiver;
    }
}
