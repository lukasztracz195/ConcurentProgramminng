package model.statistics;

import model.dataobjects.Client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HistroyOfJob {


    Map<Integer, List<Long>> mapDiscAndSelectedClients = new ConcurrentHashMap<>();

    Map<Integer, List<Long>> mapDiscAndNumberOfFilesFromClients = new ConcurrentHashMap<>();

    Map<Integer, List<Long>> mapDiscAndNumberOfInitialSize = new ConcurrentHashMap<>();

    Map<Integer, List<Long>> mapDiscAndNumberSizeOfRequest = new ConcurrentHashMap<>();

    public void add(Client client, int numberOfDisc) {
        addElementToMapDiscAndSelectedClients(client.getNumberOfClient(), numberOfDisc);
        addElementToMapDiscAndNumberOfFilesFromClients(client.getNumberOfFilesToSave(), numberOfDisc);
        addElementTomapDiscAndNumberOFInitialSize(client.getInitialNumberOfFiles(),numberOfDisc);
        addElementTomapDiscAndNumberSizeOfRequest(client.getSizeOfRequest(), numberOfDisc);
    }

    private void addElementToMapDiscAndSelectedClients(long numberOfClient, int numberOfDisc) {
        if (mapDiscAndSelectedClients.containsKey(numberOfDisc)) {
            List<Long> list = mapDiscAndSelectedClients.get(numberOfDisc);
            list.add(numberOfClient);
        } else {
            mapDiscAndSelectedClients.put(numberOfDisc, new ArrayList<Long>(Collections.singletonList(numberOfClient)));
        }
    }

    private void addElementToMapDiscAndNumberOfFilesFromClients(long numberOfFilesToSave, int numberOfDisc) {
        if (mapDiscAndNumberOfFilesFromClients.containsKey(numberOfDisc)) {
            List<Long> list = mapDiscAndNumberOfFilesFromClients.get(numberOfDisc);
            list.add(numberOfFilesToSave);
        } else {
            mapDiscAndNumberOfFilesFromClients.put(numberOfDisc,
                    new ArrayList<Long>(Collections.singletonList(numberOfFilesToSave)));
        }
    }

    private void addElementTomapDiscAndNumberOFInitialSize(long numberOfInitialSize, int numberOfDisc) {
        if (mapDiscAndNumberOfInitialSize.containsKey(numberOfDisc)) {
            List<Long> list = mapDiscAndNumberOfInitialSize.get(numberOfDisc);
            list.add(numberOfInitialSize);
        } else {
            mapDiscAndNumberOfInitialSize.put(numberOfDisc,
                    new ArrayList<Long>(Collections.singletonList(numberOfInitialSize)));
        }
    }

    private void addElementTomapDiscAndNumberSizeOfRequest(long sizeOfRequest, int numberOfDisc) {
        if (mapDiscAndNumberSizeOfRequest.containsKey(numberOfDisc)) {
            List<Long> list = mapDiscAndNumberSizeOfRequest.get(numberOfDisc);
            list.add(sizeOfRequest);
        } else {
            mapDiscAndNumberSizeOfRequest.put(numberOfDisc,
                    new ArrayList<Long>(Collections.singletonList(sizeOfRequest)));
        }
    }

    public void printStatistics() {
        for (Integer numberOfDisc : mapDiscAndSelectedClients.keySet()) {
            List<Long> list = mapDiscAndSelectedClients.get(numberOfDisc);
            System.out.println("ID klientów wybieranych na dysk (zmienia się kolejnosść w trakcie procesu, liczby zostają te same) DYSK:" + numberOfDisc);
            System.out.println(String.format("DISC[%d]: %S", numberOfDisc, list));

            List<Long> list2 = mapDiscAndNumberOfFilesFromClients.get(numberOfDisc);
            System.out.println("Liczba plików do zapisu na dysk (zmienia się w trakcie procesu, liczby się zmniejszają) DYSK:" + numberOfDisc);
            System.out.println(String.format("DISC[%d]: %S", numberOfDisc, list2));

            List<Long> list3 = mapDiscAndNumberOfInitialSize.get(numberOfDisc);
            System.out.println("Liczba plików klientów w momencie ich utworzenia ( nie zmienia się przez cały czas) DYSK:" + numberOfDisc);
            System.out.println(String.format("DISC[%d]: %S", numberOfDisc, list3));

            List<Long> list4 = mapDiscAndNumberSizeOfRequest.get(numberOfDisc);
            System.out.println("Rozmiar żądania klienta (zmienia się w trakcie procesu) DYSK:" + numberOfDisc);
            System.out.println(String.format("DISC[%d]: %S", numberOfDisc, list4));
        }
    }
}
