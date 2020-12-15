package model.observer;

import model.threads.disc.ClientReceiver;

public interface Observer {

    void update(ClientReceiver observer);
}
