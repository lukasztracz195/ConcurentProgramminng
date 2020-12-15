package model.dataobjects;

import lombok.Getter;
import model.enums.StatusOfClient;
import model.enums.StatusOfFile;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@Getter
public class Client implements Comparable<Client> {

    private List<File> files;
    private double wage;
    private long initialSizeOfFiles;
    private static long iteratorOfNumberOfClient = 0;
    private final long numberOfClient;
    private StatusOfClient status;
    private final LocalDateTime timeCreateOfClient;
    private LocalDateTime timeLastFinishedAuction;

    public Client(List<File> files) {
        this.timeCreateOfClient = LocalDateTime.now();
        this.numberOfClient = iteratorOfNumberOfClient;
        this.files = files;
        Collections.sort(files);
        this.initialSizeOfFiles = files.stream().flatMapToInt(f -> IntStream.of(f.getSize())).sum();
        status = StatusOfClient.WAITING_ON_SERVING;
        iteratorOfNumberOfClient++;
    }

    public Client(Client client) {
        this.timeCreateOfClient = client.getTimeCreateOfClient();
        this.numberOfClient = client.numberOfClient;
        this.files = client.getFiles();
        Collections.sort(files);
        this.initialSizeOfFiles = files.stream().flatMapToInt(f -> IntStream.of(f.getSize())).sum();
        status = StatusOfClient.WAITING_ON_SERVING;
    }

    public void setStatus(StatusOfClient status) {
        this.status = status;
    }

    public void setWage(double wage) {
        this.wage = wage;
    }


    public long getNumberOfMillisFromLastFinishedAction() {
        if (timeLastFinishedAuction == null) {
            return Long.MAX_VALUE;
        }
        LocalDateTime current = LocalDateTime.now();
        return ChronoUnit.MILLIS.between(timeLastFinishedAuction, current);
    }

    public long getNumberOfFiles() {
        return files.size();
    }

    public long getNumberOfFilesToSave() {
        return files.stream().filter(f->f.getStatusOfFile() == StatusOfFile.WAITING_ON_SAVE).count();
    }

    public void setTimeLastFinishedAuction(LocalDateTime timeLastFinishedAuction) {
        this.timeLastFinishedAuction = timeLastFinishedAuction;
    }

    public Optional<File> getFile() {
        if (files.isEmpty()) {
            return Optional.empty();
        }
        return files.stream()
                .filter(f -> f.getStatusOfFile() == StatusOfFile.WAITING_ON_SAVE)
                .sorted()
                .findFirst();
    }

    public long getSizeOfRequest() {
        if (files == null) {
            return 0;
        }
        return files.stream()
                .filter(f -> f.getStatusOfFile() == StatusOfFile.WAITING_ON_SAVE)
                .flatMapToInt(f -> IntStream.of(f.getSize()))
                .sum();
    }

    @Override
    public int compareTo(Client o) {
        return Double.compare(this.wage, o.getWage());
    }
}
