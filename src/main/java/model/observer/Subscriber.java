package model.observer;

import model.threads.disc.ClientReceiver;

public interface Subscriber {

    void attach(Observer observer);
    void detach(Observer observer);
    void subscribe(ClientReceiver observer);
}
