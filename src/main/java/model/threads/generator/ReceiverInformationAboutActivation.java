package model.threads.generator;

import model.threads.warehouse.ClientsReceiver;

public interface ReceiverInformationAboutActivation extends ClientsReceiver {

    void receiveInfoAboutActive( boolean isActive);
}
