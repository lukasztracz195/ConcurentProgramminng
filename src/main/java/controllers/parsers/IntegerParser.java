package controllers.parsers;

import java.util.Optional;

public class IntegerParser {

    public static Optional<Integer> parse(String testForParse) {
        return Optional.of(Integer.valueOf(testForParse));
    }
}
