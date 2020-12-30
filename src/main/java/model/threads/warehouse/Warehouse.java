package model.threads.warehouse;

import model.Logger;
import model.dataobjects.Client;
import model.statistics.StatisticNode;
import model.threads.generator.ReceiverInformationAboutActivation;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Warehouse implements Runnable, ClientsSupplier, ReceiverInformationAboutActivation , IOStatistics{

    private List<Client> clients = new CopyOnWriteArrayList<>();
    private volatile boolean run = true;
    private boolean generatorIsActive = false;
    private List<StatisticNode> statisticsNodeList = new CopyOnWriteArrayList<>();

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
        Logger.getInstance().log("Stop warehouse thread");
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

    @Override
    public void addStatisticNode(StatisticNode statisticNode) {
        statisticsNodeList.add(statisticNode);
    }

    @Override
    public List<StatisticNode> getStatisticNode() {
        return statisticsNodeList;
    }
}
