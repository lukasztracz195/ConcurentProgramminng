package controllers.parsers;

import java.util.Optional;

public class DoubleParser {

    public static Optional<Double> parse(String testForParse) {
        return Optional.of(Double.valueOf(testForParse));
    }

}
