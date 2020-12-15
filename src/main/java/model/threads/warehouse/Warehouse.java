package model.threads.warehouse;

import model.dataobjects.Client;
import model.threads.generator.ReceiverInformationAboutActivation;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Warehouse implements Runnable, ClientsSupplier, ReceiverInformationAboutActivation {

    private List<Client> clients = new CopyOnWriteArrayList<>();
    private volatile boolean run = true;
    private boolean generatorIsActive = false;

    public Warehouse() {
        Thread.currentThread().setName("WAREHOUSE");
    }

    @Override
    public void run() {
        while (run){
            //statement
        }
    }

    @Override
    public synchronized List<Client> supplierClients() {

        return clients;
    }


    @Override
    public synchronized void stop() {
        System.out.println("Stop warehouse thread");
        run = false;
    }

    @Override
    public void receiveClients(List<Client> generatedClients) {
        this.clients.addAll(generatedClients);
    }

    @Override
    public void receiveInfoAboutActive(boolean isActive) {
        generatorIsActive = isActive;
    }

    @Override
    public boolean generatorIsActive() {
        return generatorIsActive;
    }
}
