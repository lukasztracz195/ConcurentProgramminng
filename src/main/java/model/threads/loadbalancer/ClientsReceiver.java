package model.threads.loadbalancer;

import model.dataobjects.Client;

import java.util.List;

public interface ClientsReceiver {

    void receiveClients(List<Client> clients);
}
