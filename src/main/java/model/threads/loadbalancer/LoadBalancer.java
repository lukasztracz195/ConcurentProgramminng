package model.threads.loadbalancer;

import javafx.application.Platform;
import javafx.scene.control.Label;
import model.Logger;
import model.algorithms.SetWageAlgorithm;
import model.dataobjects.Client;
import model.enums.StatusOfClient;
import model.exceptions.ClientWasStarvedException;
import model.observer.Observer;
import model.statistics.HistroyOfJob;
import model.threads.StopThread;
import model.threads.disc.ClientReceiver;
import model.threads.warehouse.ClientsSupplier;
import org.apache.commons.lang3.time.StopWatch;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class LoadBalancer implements Runnable, Observer, StopThread {

    private SetWageAlgorithm algorithm;
    private int numberOfDiscToServe;
    private int numberOfStoppedDisc = 0;
    private final ClientsSupplier warehouse;
    private boolean run = true;
    private boolean startedGeneratorOfClients = false;
    private HistroyOfJob histroyOfJob = new HistroyOfJob();
    private Label labelOnTimeSimulation;
    private StopWatch stopWatch;

    public LoadBalancer(SetWageAlgorithm algorithm, ClientsSupplier warehouse, int numberOfDiscToServe) {
        this.algorithm = algorithm;
        this.warehouse = warehouse;
        this.numberOfDiscToServe = numberOfDiscToServe;
        Thread.currentThread().setName("LOAD_BALANCER");
        this.stopWatch = new StopWatch();
    }

    @Override
    public void run() {
        while (run){

        }
    }

    @Override
    public synchronized void update(ClientReceiver clientReceiver) throws ClientWasStarvedException {
        startStopWatchIfNotStarted();
        updateTimeSimulationOnView();
        List<Client> clients = warehouse.supplierClients();
        Optional<Client> optionalClient = clientReceiver.getServingClient();
        boolean discIsStopped = false;
        if (optionalClient.isPresent()) {
            Client client = optionalClient.get();
            long numberOfInProgressClients = clients.stream()
                    .filter(f -> f.getStatusOfClient() == StatusOfClient.IN_PROGRESS)
                    .count();
            long numberOfWaitingOnServingClients = clients.stream()
                    .filter(f -> f.getStatusOfClient() == StatusOfClient.WAITING_ON_SERVING)
                    .count();

            if (numberOfWaitingOnServingClients == 0 &&
                    numberOfInProgressClients < numberOfDiscToServe &&
                    client.getStatusOfClient() == StatusOfClient.SERVED && !warehouse.generatorIsActive()) {
                clientReceiver.stop();
                numberOfStoppedDisc++;
                discIsStopped = true;
            }
        }
        if (!discIsStopped) {
            boolean generatorIsActive = warehouse.generatorIsActive();
            boolean allClientsWasServed = allClientsWasServed(clients);
            if (!allClientsWasServed || generatorIsActive) {
                Optional<Client> optionalSelected = algorithm.selectClientByWages(clients);

                optionalSelected.ifPresent((client)->{
                    histroyOfJob.add(client, clientReceiver.getNumberOfDisc());
                    clientReceiver.receiveClient(client);
                });
            }else{
                clientReceiver.stop();
                numberOfStoppedDisc++;
                if (numberOfStoppedDisc == numberOfDiscToServe) {
                    histroyOfJob.printStatistics();
                    warehouse.stop();
                    stop();
                }
            }
        } else {
            if (numberOfStoppedDisc == numberOfDiscToServe) {
                histroyOfJob.printStatistics();
                warehouse.stop();
                stop();
            }
        }
    }

    @Override
    public void stop() {
        Logger.getInstance().log("Stop load balancer thread");
        run = false;
    }

    private boolean allClientsWasServed(List<Client> clientsList) {
        if (clientsList.isEmpty()) {
            return false;
        }
        Optional<Client> optionalClient = clientsList.stream()
                .filter(f -> f.getStatusOfClient() == StatusOfClient.WAITING_ON_SERVING)
                .findAny();
        long numberOfServedClients = clientsList.stream().filter(f -> f.getStatusOfClient() == StatusOfClient.SERVED).count();
        long numberOfInProgressClients = clientsList.stream().filter(f -> f.getStatusOfClient() == StatusOfClient.IN_PROGRESS).count();
        long numberOfWaitingOnServingClients = clientsList.stream().filter(f -> f.getStatusOfClient() == StatusOfClient.WAITING_ON_SERVING).count();
        return clientsList.size() == numberOfServedClients && !optionalClient.isPresent();
    }

    public void setLabelOnTimeSimulation(Label labelOnTimeSimulation) {
        this.labelOnTimeSimulation = labelOnTimeSimulation;
    }

    private void updateTimeSimulationOnView() {
        Platform.runLater(() -> {
            final long timeInNanoseconds = stopWatch.getNanoTime();
            final long minutes = TimeUnit.NANOSECONDS.toMinutes(timeInNanoseconds);
            final long seconds = TimeUnit.NANOSECONDS.toSeconds(timeInNanoseconds);
            final long millis = TimeUnit.NANOSECONDS.toMillis(timeInNanoseconds);
            final String time = String.format("%d:%d:%d", minutes, seconds, millis);
            labelOnTimeSimulation.setText(time);
        });
    }

    private void startStopWatchIfNotStarted(){
        if(!stopWatch.isStarted()){
            stopWatch.start();
        }
    }
}
