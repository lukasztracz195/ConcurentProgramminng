package controllers.validators;

public class StringToIntegerValidator extends AbstractValidator implements StringValidator {

    private String stringToValidated;

    public static StringToIntegerValidator of(final String stringToValidated) {
        return new StringToIntegerValidator(stringToValidated);
    }

    @Override
    public void validate() {
        try {
            Integer.valueOf(stringToValidated);
            validated = true;
        } catch (NumberFormatException e) {
            validated = false;
            errorMessage = String.format("String \"%s\" can't parse to Int", stringToValidated);
        }
    }

    private StringToIntegerValidator(final String stringToValidated) {

        this.stringToValidated = stringToValidated;
    }

    @Override
    public String getValidatedString() {
        return stringToValidated;
    }
}
