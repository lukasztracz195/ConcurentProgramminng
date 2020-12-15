package model.threads.warehouse;

import model.dataobjects.Client;
import model.threads.Simulation;
import model.threads.StopThread;

import java.util.List;

public interface ClientsSupplier extends Simulation {

    List<Client> supplierClients();
}
