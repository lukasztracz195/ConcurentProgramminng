package model.threads.disc;

import lombok.Getter;
import model.dataobjects.Client;
import model.dataobjects.File;
import model.enums.StatusOfClient;
import model.enums.StatusOfDisc;
import model.observer.Observer;
import model.observer.Subscriber;
import model.threads.StopThread;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

import static java.lang.Thread.currentThread;

@Getter
public class Disc implements Runnable, Subscriber, ClientReceiver, StopThread {

    private static int staticNumberDisc = 0;
    private List<Observer> observers = new CopyOnWriteArrayList<>();
    private List<File> savedFiles = new CopyOnWriteArrayList<>();
    private StatusOfDisc status = StatusOfDisc.WAITING_ON_REQUEST;
    private int numberOfDisc;
    private boolean run = true;
    private Client clientsToServe;
    private boolean sentRequestAboutClient = false;

    public Disc() {
        this.numberOfDisc = staticNumberDisc;
        Thread.currentThread().setName("DISC_"+numberOfDisc);
        staticNumberDisc++;
    }

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
    public void subscribe(ClientReceiver clientReceiver) {
        for (Observer observer : observers) {
            observer.update(this);
        }
    }

    @Override
    public void receiveClient(Client client) {
        if (status == StatusOfDisc.WAITING_ON_REQUEST) {
            clientsToServe = client;
            System.out.println(String.format("Disc %d start serving client %d with %d files about size %d",numberOfDisc,
                    clientsToServe.getNumberOfClient(), clientsToServe.getNumberOfFiles(), clientsToServe.getSizeOfRequest()));
            this.status = StatusOfDisc.BUSY;
        }
    }

    @Override
    public Optional<Client> getServingClient() {
        Optional<Client> optionalClient = Optional.ofNullable(clientsToServe);
        if (optionalClient.isPresent()) {
            if (clientsToServe.getStatus() == StatusOfClient.SERVED) {
                System.out.println(String.format("Disc %d Client %d was served", numberOfDisc,
                        clientsToServe.getNumberOfClient()));
                clientsToServe = null;
            }
            else if (clientsToServe.getStatus() == StatusOfClient.WAITING_ON_SERVING) {
                System.out.println(String.format("Disc %d Client %d return to queue", numberOfDisc,
                        clientsToServe.getNumberOfClient()));
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
        System.out.println("Stop thread disc number " + numberOfDisc);
    }

    private void saveFile() {
        clientsToServe.setStatus(StatusOfClient.IN_PROGRESS);
        Optional<File> optionalFileToSave = clientsToServe.getFile();
        if (optionalFileToSave.isPresent()) {
            File fileToSave = optionalFileToSave.get();
            long idFile = fileToSave.getUniqueNumberOfFile();
            if (fileToSave.getSize() > 0) {
                System.out.println(String.format("Disc %d start saveFile file ID(%d) of client of number %d size of request before saveFile %d",
                        numberOfDisc, fileToSave.getUniqueNumberOfFile(), clientsToServe.getNumberOfClient(), clientsToServe.getSizeOfRequest()));
                fileToSave.save(numberOfDisc);
                System.out.println(String.format("Disc %d finished saveFile file ID(%d) of client of number %d size of request after saveFile %d",
                        numberOfDisc, idFile, clientsToServe.getNumberOfClient(), clientsToServe.getSizeOfRequest()));
            }
            if (clientsToServe.getSizeOfRequest() > 0) {
                clientsToServe.setStatus(StatusOfClient.WAITING_ON_SERVING);
            } else {
                clientsToServe.setStatus(StatusOfClient.SERVED);
            }
        } else {
            clientsToServe.setStatus(StatusOfClient.SERVED);
        }
        this.status = StatusOfDisc.WAITING_ON_REQUEST;
    }
}
