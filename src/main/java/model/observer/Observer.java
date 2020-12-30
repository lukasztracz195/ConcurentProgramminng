package model.observer;

import model.exceptions.ClientWasStarvedException;
import model.threads.disc.ClientReceiver;

public interface Observer {

    void update(ClientReceiver observer) throws ClientWasStarvedException;
}
