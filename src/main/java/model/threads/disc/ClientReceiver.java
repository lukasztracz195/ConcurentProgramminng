package model.threads.disc;

import model.dataobjects.Client;
import model.threads.StopThread;

import java.util.Optional;

public interface ClientReceiver extends StopThread {

    void receiveClient(Client client);

    Optional<Client> getServingClient();

    void deleteClient();

    int getNumberOfDisc();
}
