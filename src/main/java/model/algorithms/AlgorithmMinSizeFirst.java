package model.algorithms;

import model.dataobjects.Client;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class AlgorithmMinSizeFirst implements SetWageAlgorithm {

    @Override
    public Optional<Client> selectClientByWages(List<Client> clients) {
        for (Client client : clients) {
            client.setWage(client.getInitialSizeOfFiles());
        }
        if (clients.isEmpty()) {
            return Optional.empty();
        }
        Collections.sort(clients);
        return Optional.of(clients.get(clients.size() - 1));
    }
}
