package model.threads.loadbalancer;

import model.algorithms.SetWageAlgorithm;
import model.dataobjects.Client;
import model.enums.StatusOfClient;
import model.observer.Observer;
import model.threads.StopThread;
import model.threads.disc.ClientReceiver;
import model.threads.warehouse.ClientsSupplier;

import java.util.List;
import java.util.Optional;

public class LoadBalancer implements Runnable, Observer, StopThread {

    private SetWageAlgorithm algorithm;
    private int numberOfDiscToServe;
    private int numberOfStoppedDisc = 0;
    private final ClientsSupplier warehouse;
    private boolean run = true;
    private boolean startedGeneratorOfClients = false;

    public LoadBalancer(SetWageAlgorithm algorithm, ClientsSupplier warehouse, int numberOfDiscToServe) {
        this.algorithm = algorithm;
        this.warehouse = warehouse;
        this.numberOfDiscToServe = numberOfDiscToServe;
        Thread.currentThread().setName("LOAD_BALANCER");
    }

    @Override
    public void run() {
        while (run){

        }
    }

    @Override
    public synchronized void update(ClientReceiver clientReceiver) {
        List<Client> clients = warehouse.supplierClients();
        Optional<Client> optionalClient = clientReceiver.getServingClient();
        boolean discIsStopped = false;
        if (optionalClient.isPresent()) {
            Client client = optionalClient.get();
            long numberOfInProgressClients = clients.stream()
                    .filter(f -> f.getStatus() == StatusOfClient.IN_PROGRESS)
                    .count();
            long numberOfWaitingOnServingClients = clients.stream()
                    .filter(f -> f.getStatus() == StatusOfClient.WAITING_ON_SERVING)
                    .count();

            if (numberOfWaitingOnServingClients == 0 &&
                    numberOfInProgressClients < numberOfDiscToServe &&
                    client.getStatus() == StatusOfClient.SERVED && !warehouse.generatorIsActive()) {
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
                optionalSelected.ifPresent(clientReceiver::receiveClient);
            }else{
                clientReceiver.stop();
                numberOfStoppedDisc++;
                if (numberOfStoppedDisc == numberOfDiscToServe) {
                    warehouse.stop();
                    stop();
                }
            }
        } else {
            if (numberOfStoppedDisc == numberOfDiscToServe) {
                warehouse.stop();
                stop();
            }
        }
    }

    @Override
    public void stop() {
        System.out.println("Stop load balancer thread");
        run = false;
    }

    private boolean allClientsWasServed(List<Client> clientsList) {
        if (clientsList.isEmpty()) {
            return false;
        }
        Optional<Client> optionalClient = clientsList.stream()
                .filter(f -> f.getStatus() == StatusOfClient.WAITING_ON_SERVING)
                .findAny();
        long numberOfServedClients = clientsList.stream().filter(f -> f.getStatus() == StatusOfClient.SERVED).count();
        long numberOfInProgressClients = clientsList.stream().filter(f -> f.getStatus() == StatusOfClient.IN_PROGRESS).count();
        long numberOfWaitingOnServingClients = clientsList.stream().filter(f -> f.getStatus() == StatusOfClient.WAITING_ON_SERVING).count();
        return clientsList.size() == numberOfServedClients && !optionalClient.isPresent();
    }
}
