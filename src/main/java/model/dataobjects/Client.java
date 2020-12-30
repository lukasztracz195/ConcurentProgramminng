package model.dataobjects;

import lombok.Getter;
import model.enums.StatusOfClient;
import model.enums.StatusOfFile;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@Getter
public class Client implements Comparable<Client> {

    private static long staticIteratorOfTheNumberOfClient = 0;

    private List<File> fileList;
    private double weight;
    private long initialNumberOfFiles;
    private final long numberOfClient;
    private StatusOfClient statusOfClient;
    private final LocalDateTime creationOfTime;
    private LocalDateTime timeOfLastWinningAuction;

    public Client(List<File> fileList) {
        this.creationOfTime = LocalDateTime.now();
        this.numberOfClient = staticIteratorOfTheNumberOfClient;
        this.fileList = fileList;
        Collections.sort(fileList);
        this.initialNumberOfFiles = fileList.size();
        statusOfClient = StatusOfClient.WAITING_ON_SERVING;
        staticIteratorOfTheNumberOfClient++;
    }

    public Client(Client client) {
        this.creationOfTime = client.getCreationOfTime();
        this.numberOfClient = client.numberOfClient;
        this.fileList = client.getFileList();
        Collections.sort(fileList);
        this.initialNumberOfFiles = fileList.stream().flatMapToInt(f -> IntStream.of(f.getSize())).sum();
        statusOfClient = StatusOfClient.WAITING_ON_SERVING;
    }

    public void setStatusOfClient(StatusOfClient statusOfClient) {
        this.statusOfClient = statusOfClient;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }


    public Optional<Long> getNumberOfMillisFromLastFinishedAction() {
        if (timeOfLastWinningAuction == null) {
            return Optional.empty();
        }
        LocalDateTime current = LocalDateTime.now();
        return Optional.of(ChronoUnit.MILLIS.between(timeOfLastWinningAuction, current));
    }

    public Long getNumberOfMillisFromCreateRequest() {

        LocalDateTime current = LocalDateTime.now();
        return ChronoUnit.MILLIS.between(creationOfTime, current);
    }

    public long getNumberOfFiles() {
        return fileList.size();
    }

    public long getNumberOfFilesToSave() {
        return fileList.stream()
                .filter(f->f.getStatusOfFile() == StatusOfFile.WAITING_ON_SAVE)
                .count();
    }

    public void setTimeOfLastWinningAuction(LocalDateTime timeOfLastWinningAuction) {
        this.timeOfLastWinningAuction = timeOfLastWinningAuction;
    }

    public Optional<File> getFileToSave() {
        if (fileList.isEmpty()) {
            return Optional.empty();
        }
        return fileList.stream()
                .filter(f -> f.getStatusOfFile() == StatusOfFile.WAITING_ON_SAVE)
                .sorted()
                .findFirst();
    }

    public long getSizeOfRequest() {
        if (fileList.isEmpty()) {
            return 0;
        }
        return fileList.stream()
                .filter(f -> f.getStatusOfFile() == StatusOfFile.WAITING_ON_SAVE)
                .flatMapToInt(f -> IntStream.of(f.getSize()))
                .sum();
    }


    @Override
    public int compareTo(Client o) {
        return Double.compare(this.weight, o.getWeight());
    }

    @Override
    public String toString() {
        return "Client{" +
                ", weight=" + weight +
                ", initialNumberOfFiles=" + initialNumberOfFiles +
                ", sizeOfRequest=" + getSizeOfRequest() +
                ", numberOFilesToSave=" + getNumberOfFilesToSave()+
                ", numberOfClient=" + numberOfClient +
                ", statusOfClient=" + statusOfClient +
                ", creationOfTime=" + creationOfTime +
                ", timeOfLastWinningAuction=" + timeOfLastWinningAuction +
                '}';
    }
}
