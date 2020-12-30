package model.threads.disc;

import javafx.application.Platform;
import javafx.scene.control.ProgressBar;
import lombok.Getter;
import lombok.SneakyThrows;
import model.Logger;
import model.dataobjects.Client;
import model.dataobjects.File;
import model.dataobjects.ViewObjectForDisc;
import model.enums.StatusOfClient;
import model.enums.StatusOfDisc;
import model.exceptions.ClientWasStarvedException;
import model.observer.Observer;
import model.observer.Subscriber;
import model.threads.StopThread;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

@Getter
public class Disc implements Runnable, Subscriber, ClientReceiver, StopThread {

    private static int staticNumberDisc = 0;
    private List<Observer> observers = new CopyOnWriteArrayList<>();
    private List<File> savedFiles = new CopyOnWriteArrayList<>();
    private StatusOfDisc status = StatusOfDisc.WAITING_ON_REQUEST;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:s.SSS.n");
    private int numberOfDisc;
    private boolean run = true;
    private Client clientsToServe;
    private boolean sentRequestAboutClient = false;

    private ViewObjectForDisc viewObjectForDisc;

    public Disc() {
        this.numberOfDisc = staticNumberDisc;
        Thread.currentThread().setName("DISC_"+numberOfDisc);
        staticNumberDisc++;
    }

    @SneakyThrows
    @Override
    public void run() {
        while (run) {
            if (status == StatusOfDisc.WAITING_ON_REQUEST) {
                subscribe(this);
            }
            if (this.status == StatusOfDisc.BUSY) {
                if (Optional.ofNullable(clientsToServe).isPresent()) {
                    saveFile();
                }
            }
        }
    }

    @Override
    public void attach(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void detach(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void subscribe(ClientReceiver clientReceiver) throws ClientWasStarvedException {
        for (Observer observer : observers) {
            observer.update(this);
        }
    }

    @Override
    public void receiveClient(Client client) {
        if (status == StatusOfDisc.WAITING_ON_REQUEST) {
            Platform.runLater(() ->
            viewObjectForDisc.getLabelClientId().setText(String.valueOf(client.getNumberOfClient())));
            clientsToServe = client;
            Logger.getInstance().log(String.format("%s|DISC[%d]:WAITING_ON_REQUEST->BUSY|Client[%d] WAITING_ON_SERVING->IN_PROGRESS|SizeOfRequest[%d]|NumberOffFilesToSave[%d]",
                    LocalDateTime.now().format(formatter),numberOfDisc,clientsToServe.getNumberOfClient(),
                    clientsToServe.getSizeOfRequest(),clientsToServe.getNumberOfFilesToSave()));
            this.status = StatusOfDisc.BUSY;
        }
    }

    @Override
    public Optional<Client> getServingClient() {
        Optional<Client> optionalClient = Optional.ofNullable(clientsToServe);
        if (optionalClient.isPresent()) {
            if (clientsToServe.getStatusOfClient() == StatusOfClient.SERVED) {
                Logger.getInstance().log(String.format("%s|DISC[%d]:BUSY->WAITING_ON_SERVING|CLIENT[%d]IN_PROGRESS->SERVED|SizeOfRequest[%d]|NumberOffFilesToSave[%d]",
                        LocalDateTime.now().format(formatter), numberOfDisc, clientsToServe.getNumberOfClient(),
                        clientsToServe.getSizeOfRequest(), clientsToServe.getNumberOfFilesToSave()));
                clientsToServe = null;
            }
            else if (clientsToServe.getStatusOfClient() == StatusOfClient.WAITING_ON_SERVING) {
                Logger.getInstance().log(String.format("%s|DISC[%d]:BUSY->WAITING_ON_SERVING|CLIENT[%d]IN_PROGRESS->WAITING_ON_SERVING|SizeOfRequest[%d]|NumberOffFilesToSave[%d]",
                        LocalDateTime.now().format(formatter), numberOfDisc, clientsToServe.getNumberOfClient(),
                        clientsToServe.getSizeOfRequest(), clientsToServe.getNumberOfFilesToSave()));
                clientsToServe = null;
            }

        }
        return optionalClient;

    }

    @Override
    public void deleteClient() {
        clientsToServe = null;
    }

    @Override
    public void stop() {
        this.run = false;
        Logger.getInstance().log("Stop thread disc number " + numberOfDisc);
    }

    public void setViewObjectForDisc(ViewObjectForDisc viewObjectForDisc) {
        this.viewObjectForDisc = viewObjectForDisc;
    }

    private void saveFile() {
        clientsToServe.setStatusOfClient(StatusOfClient.IN_PROGRESS);
        Optional<File> optionalFileToSave = clientsToServe.getFileToSave();
        if (optionalFileToSave.isPresent()) {
            File fileToSave = optionalFileToSave.get();
            Platform.runLater(() ->
            {
                viewObjectForDisc.getLabelFileId().setText(String.valueOf(fileToSave.getUniqueNumberOfFile()));
                viewObjectForDisc.getLabelTypeOfFile().setText(fileToSave.getTypeFileBySize().toString());
            });

            fileToSave.setProgressBar(viewObjectForDisc.getProgressBar());
            if (fileToSave.getSize() > 0) {
                Logger.getInstance().log(String.format("%s|DISC[%d]:BUSY|CLIENT[%d]:IN_PROGRESS|FILE[%d]WAITING_ON_SAVE->SAVING|SizeOfRequest[%d]",
                        LocalDateTime.now().format(formatter), numberOfDisc,clientsToServe.getNumberOfClient(),
                        fileToSave.getUniqueNumberOfFile(), clientsToServe.getSizeOfRequest()));
                fileToSave.save(numberOfDisc);
                Logger.getInstance().log(String.format("%s|DISC[%d]:BUSY|CLIENT[%d]:IN_PROGRESS|FILE[%d]SAVING->SAVED|SizeOfRequest[%d]",
                        LocalDateTime.now().format(formatter), numberOfDisc,clientsToServe.getNumberOfClient(),
                        fileToSave.getUniqueNumberOfFile(), clientsToServe.getSizeOfRequest()));
                Platform.runLater(() ->
                 viewObjectForDisc.getListSavedOfFiles().getItems().add(fileToSave));
            }
            if (clientsToServe.getSizeOfRequest() > 0) {
                clientsToServe.setStatusOfClient(StatusOfClient.WAITING_ON_SERVING);
            } else {
                clientsToServe.setStatusOfClient(StatusOfClient.SERVED);
            }
        } else {
            clientsToServe.setStatusOfClient(StatusOfClient.SERVED);
        }
        this.status = StatusOfDisc.WAITING_ON_REQUEST;
    }

    public int getNumberOfDisc(){
        return numberOfDisc;
    }
}
