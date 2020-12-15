package model.dataobjects;

import lombok.Getter;
import model.enums.StatusOfFile;
import model.enums.TypeFileBySize;
import model.time.Sleepyhead;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

@Getter
public class File implements FileofData, Sleepyhead, Comparable<File> {

    private Integer size;
    private static int staticNumberOfFile = 0;

    private final long uniqueNumberOfFile;
    private TypeFileBySize typeFileBySize;
    private final Integer initialSize;
    private StatusOfFile statusOfFile;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:s.SSS.n");
    private final LocalDateTime timeCreateOfFile;
    private LocalDateTime timeStartSavingOfFile;
    private LocalDateTime timeFinishOfSavingFile;
    private int idDiscWhereIsSavedFile;

    public File(final int size, final TypeFileBySize typeFileBySize) {
        this.initialSize = size;
        this.size = size;
        this.typeFileBySize = typeFileBySize;
        this.statusOfFile = StatusOfFile.WAITING_ON_SAVE;
        this.timeCreateOfFile = LocalDateTime.now();
        this.uniqueNumberOfFile = staticNumberOfFile;
        staticNumberOfFile++;
    }

    @Override
    public void save(int discId) {
        timeStartSavingOfFile = LocalDateTime.now();
        statusOfFile = StatusOfFile.SAVING;
        System.out.println(String.format("Start saving file ID(%d) with size: %d %s",
                uniqueNumberOfFile, size ,LocalDateTime.now().format(formatter)));
        while (size > 0) {
            sleep(TIME_SLEEP_IN_MILLISECONDS);
            size--;
        }
        System.out.println(String.format("Finish saving file ID(%d) with size: %d %s",uniqueNumberOfFile,initialSize,
                LocalDateTime.now().format(formatter)));
        statusOfFile = StatusOfFile.SAVED;
        timeFinishOfSavingFile = LocalDateTime.now();
        idDiscWhereIsSavedFile = discId;
    }

    public void sleep(final long milliseconds) {
        if (milliseconds > 0) {
            try {
                TimeUnit.MILLISECONDS.sleep(milliseconds);
            } catch (InterruptedException e) {
                Thread.interrupted();
            }

        }
    }

    @Override
    public int compareTo(File o) {
        return Integer.compare(this.size, o.getSize());
    }
}
