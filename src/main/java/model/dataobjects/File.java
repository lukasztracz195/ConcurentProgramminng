package model.dataobjects;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import lombok.Getter;
import model.Logger;
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
    private Label labelFileId;
    private ProgressBar progressBar;

    public File(final int size, final TypeFileBySize typeFileBySize) {
        this.initialSize = size;
        this.size = size;
        this.typeFileBySize = typeFileBySize;
        this.statusOfFile = StatusOfFile.WAITING_ON_SAVE;
        this.timeCreateOfFile = LocalDateTime.now();
        this.uniqueNumberOfFile = staticNumberOfFile;
        staticNumberOfFile++;
    }

    public void setLabelFileId(Label labelFileId) {
        this.labelFileId = labelFileId;
    }

    public void setProgressBar(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    @Override
    public void save(int discId) {
        timeStartSavingOfFile = LocalDateTime.now();
        statusOfFile = StatusOfFile.SAVING;
        Logger.getInstance().log(String.format("%s|FILE[%d]WAITING_ON_SAVE->SAVING|SizeOfFile[%d]",
                LocalDateTime.now().format(formatter), uniqueNumberOfFile, size));
        while (size > 0) {
            sleep(TIME_SLEEP_IN_MILLISECONDS);
            if(progressBar != null){
                Platform.runLater(() ->
                        progressBar.setProgress(Math.abs((size/(double)initialSize)-1.0)));

            }
            size--;
        }
        Logger.getInstance().log(String.format("%s|FILE[%d]SAVING->SAVED|SizeOfFile[%d]",
                LocalDateTime.now().format(formatter), uniqueNumberOfFile, size));
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

    @Override
    public String toString(){
        return String.format("ID:%d|SIZE:%d",uniqueNumberOfFile, initialSize);
    }
}
