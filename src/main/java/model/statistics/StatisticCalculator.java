package model.statistics;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

public class StatisticCalculator  {

    public static Optional<Long> countModeFromNumberOfFilesToServe(List<Long> listOfLongs) {
        Map<Long, Long> frequencies = listOfLongs.stream()
                .collect(Collectors.groupingBy(i -> i, Collectors.counting()));

        long maxCount = frequencies.values().stream().mapToLong(Long::longValue).max().orElse(0);

        List<Long> all = frequencies.entrySet().stream()
                .filter(e -> e.getValue() == maxCount)
                .map(Map.Entry::getKey)
                .sorted()
                .collect(Collectors.toList());
        if (all.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(all.get(all.size() / 2));

    }

    public static  double countStandardDeviationForNumberOfFiles(List<Long> listOfLongs, double average) {
        double iterator = listOfLongs.stream()
                .flatMapToDouble(f -> DoubleStream.of(Math.pow(f.doubleValue() - average, 2))).sum();

        return Math.sqrt(iterator / listOfLongs.size());

    }

    public static  Optional<Long> countQ1(List<Long> listOfLongs, long median) {
        List<Long> valuesLowerThanMedian = listOfLongs.stream()
                .sorted()
                .filter(f -> f < median)
                .collect(Collectors.toList());
        if (valuesLowerThanMedian.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(valuesLowerThanMedian.get(valuesLowerThanMedian.size() / 2));
    }

    public static  Optional<Long> countQ3(List<Long> listOfLongs, long median) {
        List<Long> valuesLowerThanMedian = listOfLongs.stream()
                .sorted()
                .filter(f -> f > median)
                .collect(Collectors.toList());
        if (valuesLowerThanMedian.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(valuesLowerThanMedian.get(valuesLowerThanMedian.size() / 2));
    }


    public static  Optional<Double> countSkewCoefficientD(Double average, Long mode, Double standardDeviation) {

        if (standardDeviation == 0) {
            return Optional.empty();
        }
        return Optional.of(((average - mode) / standardDeviation));
    }

    public static  Optional<Double> countSkewCoefficientM(Double average, Long median, Double standardDeviation) {

        if (standardDeviation == 0) {
            return Optional.empty();
        }
        return Optional.of(3 * ((average - median) / standardDeviation));
    }

    public static  Optional<Double> countSkewCoefficientQ(Double Q1, Double Q3, Long median) {

        if (Q1 == 0 && Q3 == 0) {
            return Optional.empty();
        }
        return Optional.of(((Q1 + Q3) - 2 * median) / (Q3 - Q1));
    }

    public static  Optional<Long> countMedian(List<Long> listOfLongs) {

        if (listOfLongs.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(listOfLongs.get(listOfLongs.size() / 2));
    }

    public static  Optional<Double> countM3(List<Long> listOfLongs, Double average) {
        if (listOfLongs.isEmpty()) {
            return Optional.empty();
        }
        final double sum = listOfLongs.stream()
                .flatMapToDouble(f -> DoubleStream.of(Math.pow(f.doubleValue() - average, 3))).sum();
        return Optional.of(sum / listOfLongs.size());
    }

    public static  Optional<Double> countAsimetricFactor(Double M3, Double standardDeviation) {
        if (standardDeviation == 0) {
            return Optional.empty();
        }
        return Optional.of(M3 / Math.pow(standardDeviation, 3));
    }
}