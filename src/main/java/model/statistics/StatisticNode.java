package model.statistics;

import lombok.Getter;
import model.dataobjects.Client;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static java.util.stream.DoubleStream.of;

@Getter
public class StatisticNode {

    private long maxNumberOfFilesFromClientsWaitingOnServing;
    private double averageNumberOfFilesToServing;
    private double averageNumberOfMillisWaitingClientsOnSelectToAuction;
    private long medianFromNumberOfFilesFromClientsWaitingOnServing;
    private long modeFromNumberOfFilesFromClientsWaitingOnServing;
    private double standardDeviationFromNumberOfFilesFromClientsWaitingOnServing;
    private double Q1FromNumberOfFilesFromClientsWaitingOnServing;
    private double Q3FromNumberOfFilesFromClientsWaitingOnServing;
    private long minFromNumberOfFilesFromClientsWaitingOnServing;
    private List<Long> listWithNumberOfFilesToServe;
    private List<Long> listWithNumberOfFilesToServeSorted;
    private double skewCoefficientD;
    private double skewCoefficientM;
    private double skewCoefficientQ;

    public StatisticNode(List<Client> clientsWaitingOnServing){
        this.listWithNumberOfFilesToServe = clientsWaitingOnServing.stream()
                .flatMapToLong(f -> LongStream.of(f.getNumberOfFilesToSave())).boxed().collect(Collectors.toList());

        this.maxNumberOfFilesFromClientsWaitingOnServing = listWithNumberOfFilesToServe.stream()
                .flatMapToLong(f -> LongStream.of(f.longValue()))
                .max()
                .orElse(0);

        this.minFromNumberOfFilesFromClientsWaitingOnServing = listWithNumberOfFilesToServe.stream()
                .flatMapToLong(f -> LongStream.of(f.longValue()))
                .min()
                .orElse(0);

        this.listWithNumberOfFilesToServeSorted = listWithNumberOfFilesToServe.stream()
                .sorted().collect(Collectors.toList());

        this.averageNumberOfFilesToServing = listWithNumberOfFilesToServe.stream()
                .flatMapToDouble(f -> of(f.longValue()))
                .average()
                .orElse(0);

        this.averageNumberOfMillisWaitingClientsOnSelectToAuction = clientsWaitingOnServing.stream()
                .flatMapToLong(f -> LongStream.of(f.getNumberOfMillisFromLastFinishedAction().orElse(0L)))
                .average().orElse(0L);

        this.medianFromNumberOfFilesFromClientsWaitingOnServing = StatisticCalculator
                .countMedian(listWithNumberOfFilesToServeSorted).orElse(0L);

        this.modeFromNumberOfFilesFromClientsWaitingOnServing = StatisticCalculator.countModeFromNumberOfFilesToServe(
                listWithNumberOfFilesToServeSorted)
                .orElse(0L);

        this.standardDeviationFromNumberOfFilesFromClientsWaitingOnServing = StatisticCalculator
                .countStandardDeviationForNumberOfFiles(listWithNumberOfFilesToServeSorted,
                averageNumberOfFilesToServing);

        this.Q1FromNumberOfFilesFromClientsWaitingOnServing = StatisticCalculator
                .countQ1(listWithNumberOfFilesToServeSorted, medianFromNumberOfFilesFromClientsWaitingOnServing)
                .orElse(0L);

        this.Q3FromNumberOfFilesFromClientsWaitingOnServing = StatisticCalculator
                .countQ3(listWithNumberOfFilesToServeSorted,
                        medianFromNumberOfFilesFromClientsWaitingOnServing)
                .orElse(0L);

        this.skewCoefficientD = StatisticCalculator.countSkewCoefficientD(averageNumberOfFilesToServing,
                modeFromNumberOfFilesFromClientsWaitingOnServing,
                standardDeviationFromNumberOfFilesFromClientsWaitingOnServing)
                .orElse(0D);

        this.skewCoefficientM = StatisticCalculator.countSkewCoefficientM(averageNumberOfFilesToServing,
                modeFromNumberOfFilesFromClientsWaitingOnServing,
                standardDeviationFromNumberOfFilesFromClientsWaitingOnServing)
                .orElse(0D);

        this.skewCoefficientQ = StatisticCalculator.countSkewCoefficientQ(Q1FromNumberOfFilesFromClientsWaitingOnServing,
                Q3FromNumberOfFilesFromClientsWaitingOnServing, medianFromNumberOfFilesFromClientsWaitingOnServing)
                .orElse(0D);
    }
}
