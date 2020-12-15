package model;

import lombok.Getter;
import model.algorithms.AlgorithmWage;
import model.algorithms.SetWageAlgorithm;
import model.config.ConfigGenerator;
import model.observer.Observer;
import model.threads.disc.Disc;
import model.threads.generator.ClientsGenerator;
import model.threads.loadbalancer.LoadBalancer;
import model.threads.warehouse.Warehouse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Getter

public class Simulator implements Simulation{

    private ClientsGenerator clientsGenerator;
    private List<Disc> discs;
    private Warehouse warehouse;
    private LoadBalancer loadBalancer;
    private SetWageAlgorithm algorithmToSetWageForClients;
    private int numberOfDiscs;
    private ExecutorService executor;
    private final int numberOfNecessaryFunctionalThreads = 3;
    private List<Runnable> threads;
    private List<Runnable> stoppedThreads;
    private boolean simulationIsRunning = false;

    public  Simulator(ConfigGenerator configGenerator, int numberOfDiscs, SetWageAlgorithm algorithmToSetWageForClients){
        this.clientsGenerator = new ClientsGenerator(configGenerator);
        this.discs = prepareDiscs(numberOfDiscs);
        this.numberOfDiscs = numberOfDiscs;
        this.warehouse = new Warehouse();
        this.algorithmToSetWageForClients = algorithmToSetWageForClients;
        this.loadBalancer = new LoadBalancer(algorithmToSetWageForClients,warehouse,discs.size());
    }

    @Override
    public void run() {
        simulationIsRunning = true;
        bindLoadBalancerToDiscs(discs,loadBalancer);
        clientsGenerator.setClientsReceiver(warehouse);
        executor = Executors.newFixedThreadPool(numberOfNecessaryFunctionalThreads + discs.size());

        threads = new ArrayList<>(Arrays.asList(warehouse, loadBalancer, clientsGenerator));
        threads.addAll(discs);
        for (Runnable thread : threads) {
            executor.execute(thread);
        }
    }

    @Override
    public void stop() {
       stoppedThreads = executor.shutdownNow();
        simulationIsRunning = false;
    }

    @Override
    public boolean isStopped() {
        if(stoppedThreads.size() == numberOfNecessaryFunctionalThreads + discs.size()){
            List<Thread> threads = stoppedThreads.stream().map(Thread::new).collect(Collectors.toList());
            return threads.stream().noneMatch(f->f.getState() == Thread.State.RUNNABLE);
        }
        return !simulationIsRunning;
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
        }
    }
}
