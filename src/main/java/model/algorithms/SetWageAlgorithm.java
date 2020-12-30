package model.algorithms;

import model.config.ConfigGenerator;
import model.dataobjects.Client;
import model.exceptions.ClientWasStarvedException;
import model.threads.warehouse.IOStatistics;

import java.util.List;
import java.util.Optional;

public interface SetWageAlgorithm {

    Optional<Client> selectClientByWages(List<Client> clients) throws ClientWasStarvedException;

    void setConfigGenerator(ConfigGenerator configGenerator);
    void setStatisticWarehouse(IOStatistics ioStatistics);
}
