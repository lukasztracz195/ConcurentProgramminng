package model.algorithms;

import model.dataobjects.Client;

import java.util.List;
import java.util.Optional;

public interface SetWageAlgorithm {

    Optional<Client> selectClientByWages(List<Client> clients);
}
