package model.algorithms;

import model.dataobjects.Client;
import model.enums.StatusOfClient;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

public class AlgorithmWage implements SetWageAlgorithm {

    @Override
    public Optional<Client> selectClientByWages(List<Client> clients) {
        List<Client> clientsWaitingOnServing = clients.stream()
                .filter(f->f.getStatus() == StatusOfClient.WAITING_ON_SERVING)
                .collect(Collectors.toList());

        final long maxNumberOfFilesFromClients = clientsWaitingOnServing.stream()
                .flatMapToLong(f-> LongStream.of(f.getNumberOfFiles()))
                .max().orElse(0);

        for(Client client : clientsWaitingOnServing){
            final long estimatedTimeFinishedRequestInMillis = client.getSizeOfRequest() * client.getNumberOfFiles();
            final long wage = client.getNumberOfMillisFromLastFinishedAction()
                    + (maxNumberOfFilesFromClients- client.getNumberOfFilesToSave())
                    + estimatedTimeFinishedRequestInMillis;

            client.setWage(wage);
        }
        if(!clientsWaitingOnServing.isEmpty()) {
            Collections.sort(clientsWaitingOnServing);
            Client selectedClient = clientsWaitingOnServing.get(clientsWaitingOnServing.size() - 1);
            selectedClient.setTimeLastFinishedAuction(LocalDateTime.now());
            return Optional.of(selectedClient);
        }
        return Optional.empty();

    }
}
