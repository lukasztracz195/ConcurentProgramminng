package model.exceptions;

import model.config.ConfigGenerator;
import model.dataobjects.Client;

public class ClientWasStarvedException extends Exception {

    private Client starvedClient;
    private ConfigGenerator configGenerator;
    private final double CONSTANT_STARVING;

    public ClientWasStarvedException(Client client, ConfigGenerator configGenerator, double CONSTANT_STARVING) {
        this.starvedClient = client;
        this.configGenerator = configGenerator;
        this.CONSTANT_STARVING = CONSTANT_STARVING;
    }


    @Override
    public String toString(){
        return String.format("CONSTANT_STARVING= %f\nClient[%d]TimeFromLastFinishedAuction:%d ms |\nCLIENT_OF_DETAIL\n%s\nPARAMETERS_OF_SIMULATION\n%s",
                CONSTANT_STARVING,
                starvedClient.getNumberOfClient(),
                starvedClient.getNumberOfMillisFromLastFinishedAction().orElse(0L), starvedClient, configGenerator);
    }
}
