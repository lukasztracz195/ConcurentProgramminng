package controllers.validators;

public class StringToDoubleValidator extends AbstractValidator implements StringValidator {

    private String stringToValidated;

    public static StringToDoubleValidator of(final String stringToValidated){
        return  new StringToDoubleValidator(stringToValidated);
    }
    public void validate() {
        try{
            Double.valueOf(stringToValidated);
            validated = true;
        }catch (NumberFormatException e){
            validated = false;
            errorMessage = String.format("String \"%s\" can't parse to Double", stringToValidated);
        }

    }

    private StringToDoubleValidator(final String stringToValidated){
        this.stringToValidated = stringToValidated;
    }

    @Override
    public String getValidatedString() {
        return stringToValidated;
    }
}
