package model;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import lombok.Getter;
import model.algorithms.SetWageAlgorithm;
import model.config.ConfigGenerator;
import model.dataobjects.ViewObjectForDisc;
import model.observer.Observer;
import model.threads.disc.Disc;
import model.threads.generator.ClientsGenerator;
import model.threads.loadbalancer.LoadBalancer;
import model.threads.warehouse.Warehouse;
import org.apache.commons.lang3.time.StopWatch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Getter

public class Simulator implements Simulation {

    private ClientsGenerator clientsGenerator;
    private List<Disc> discs;
    private Warehouse warehouse;
    private LoadBalancer loadBalancer;
    private SetWageAlgorithm algorithmToSetWageForClients;
    private int numberOfDiscs;
    private ExecutorService executor;
    private final int numberOfNecessaryFunctionalThreads = 3;
    private List<Runnable> threads;
    private List<Runnable> stoppedThreads = new ArrayList<>();
    private boolean simulationIsRunning = false;
    private StopWatch stopWatch;
    private Label labelOnTimeSimulation;
    private Button start;
    private Button stop;

    private Map<Integer, ViewObjectForDisc> viewControlsForDiscsMap;

    public Simulator(ConfigGenerator configGenerator, int numberOfDiscs, SetWageAlgorithm algorithmToSetWageForClients,
                     Map<Integer, ViewObjectForDisc> viewControlsForDiscsMap, Label labelOnTimeSimulation) {
        this.clientsGenerator = new ClientsGenerator(configGenerator);
        this.discs = prepareDiscs(numberOfDiscs);
        this.numberOfDiscs = numberOfDiscs;
        this.warehouse = new Warehouse();
        this.algorithmToSetWageForClients = algorithmToSetWageForClients;
        this.algorithmToSetWageForClients.setConfigGenerator(configGenerator);
        this.algorithmToSetWageForClients.setStatisticWarehouse(warehouse);
        this.loadBalancer = new LoadBalancer(algorithmToSetWageForClients, warehouse, discs.size());
        this.viewControlsForDiscsMap = viewControlsForDiscsMap;
        this.labelOnTimeSimulation = labelOnTimeSimulation;
    }

    @Override
    public void run() {
        simulationIsRunning = true;
        loadBalancer.setLabelOnTimeSimulation(labelOnTimeSimulation);
        bindLoadBalancerToDiscs(discs, loadBalancer);
        clientsGenerator.setClientsReceiver(warehouse);
        executor = Executors.newFixedThreadPool(numberOfNecessaryFunctionalThreads + discs.size());

        threads = new ArrayList<>(Arrays.asList(warehouse, loadBalancer, clientsGenerator));
        threads.addAll(discs);
        for (Runnable thread : threads) {
            executor.execute(thread);
        }
        start.setDisable(true);
        stop.setDisable(false);
    }

    @Override
    public void stop() { ;
        executor.shutdown();
        try {
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException ex) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
        simulationIsRunning = false;
        start.setDisable(false);
        stop.setDisable(true);
    }

    @Override
    public boolean isStopped() {
            return !simulationIsRunning;
    }

    public Disc getDisc(int numberOfDisc) {
        return discs.get(numberOfDisc);
    }

    public Warehouse getWareHouse() {
        return warehouse;
    }

    public void setStart(Button start) {
        this.start = start;
    }

    public void setStop(Button stop) {
        this.stop = stop;
    }

    private List<Disc> prepareDiscs(int numberOfDiscs) {
        List<Disc> discs = new ArrayList<>();
        for (int i = 0; i < numberOfDiscs; i++) {
            discs.add(new Disc());
        }
        return discs;
    }

    private void bindLoadBalancerToDiscs(List<Disc> discs, Observer observer) {
        for (Disc disc : discs) {
            disc.attach(observer);
            disc.setViewObjectForDisc(viewControlsForDiscsMap.get(disc.getNumberOfDisc()));
        }
    }
}
