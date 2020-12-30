package model.algorithms;

import model.config.ConfigGenerator;
import model.dataobjects.Client;
import model.enums.StatusOfClient;
import model.exceptions.ClientWasStarvedException;
import model.statistics.StatisticCalculator;
import model.statistics.StatisticNode;
import model.threads.warehouse.IOStatistics;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.LongStream;

public class AlgorithmWage implements SetWageAlgorithm {

    private IOStatistics warehouseStatistic;
    private ConfigGenerator configGenerator;
    //przychodzi klient milonowy ma ileś tam pliku

    // przeliczyć wartość rycerskości uzależnioną od ilości plików
    // dużo klientów co mają mało plików to chętniej wpuszczamy
    // dużo klientów co mają dużo plikó to niechętnie wpuszcamy

    //algorytm_rycerskości

    //Jeśli w zbiorze mamy dużo klientów z małą liczbą plików i przyjdzie client

    //ten co czeka długo i ma mało do zapisania powinien dostać najwyższe prio
    //mało do zapisania -> mało aukcji do końca i mało danych do zapisania ten jest faworyzowany

    //
    //jesteśmy daleko od zagłodzenia to nadajemy najwyższe prio tym co będą najdłużej mielić

    //ustalic stałą zagłodzenia

    private final List<Double> standardDevHistory = new ArrayList<>();

    @Override
    public Optional<Client> selectClientByWages(List<Client> clients) throws ClientWasStarvedException {

        List<Client> clientsWaitingOnServing = clients.stream()
                .filter(f -> f.getStatusOfClient() == StatusOfClient.WAITING_ON_SERVING)
                .collect(Collectors.toList());

        if (!clients.isEmpty()) {

            StatisticNode statisticNode = new StatisticNode((clientsWaitingOnServing));
            warehouseStatistic.addStatisticNode(statisticNode);

            final double averageSizeOfRequest = clientsWaitingOnServing.stream()
                    .flatMapToDouble(f-> DoubleStream.of(f.getSizeOfRequest())).average().orElse(0L);

            for (Client client : clientsWaitingOnServing) {

                final double averageWaitingOnAuctionTime = clientsWaitingOnServing.stream()
                        .flatMapToDouble(f->
                                DoubleStream.of(f.getNumberOfMillisFromLastFinishedAction()
                                        .orElse(f.getNumberOfMillisFromCreateRequest())))
                        .average().orElse(0D);
                final long numberOfClientNotUsedAuction = clientsWaitingOnServing.stream()
                        .filter(f->f.getNumberOfMillisFromLastFinishedAction().isPresent()).count();
                //waga = rozmiar_request + średni_czas
                //        jedne składnik odpowiedzilany za czs, drugi za pojemność ( Z TEJ SUMY ILOCZYNU MA WYNIKAĆ NIE ZAGŁODZIMY PIERWSZEGO ALE JEŻELI JEDNOCZEŚNIE)


                final double f = (client.getSizeOfRequest()/statisticNode.getAverageNumberOfFilesToServing());
                final double capacityFactor = Math.log(client.getSizeOfRequest()/averageSizeOfRequest);  //składnik odpowiedzialny za pojemność
                final double timeFactor = -(Math.sqrt(
                        client.getNumberOfMillisFromLastFinishedAction()
                                .orElse(client.getNumberOfMillisFromCreateRequest())/averageWaitingOnAuctionTime) + numberOfClientNotUsedAuction);

                final double oldWeight = client.getWeight();
                final double wage =
                        oldWeight +
                        capacityFactor +
                        timeFactor;
                client.setWeight(wage);
            }
            Collections.sort(clientsWaitingOnServing);

            if (clientsWaitingOnServing.isEmpty()) {
                return Optional.empty();
            }
            Client selectedClient = clientsWaitingOnServing.get(0);
            selectedClient.setTimeOfLastWinningAuction(LocalDateTime.now());
            return Optional.of(selectedClient);
        }
        return Optional.empty();

    }

    @Override
    public void setConfigGenerator(ConfigGenerator configGenerator) {
        this.configGenerator = configGenerator;
    }

    @Override
    public void setStatisticWarehouse(IOStatistics ioStatistics) {
        this.warehouseStatistic = ioStatistics;
    }

    private double relu(double value) {
        if (value <= 0.0) {
            return 0.0;
        }
        return value;
    }

}
