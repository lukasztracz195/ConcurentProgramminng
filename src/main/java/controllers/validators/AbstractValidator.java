package controllers.validators;

public class AbstractValidator implements Validator{

    protected boolean validated;
    protected String errorMessage;

    @Override
    public void validate() {
        //todo implemnetation
    }

    @Override
    public boolean isValid() {
        validate();
        return validated;
    }

    @Override
    public String getErrorMessage() {
        return errorMessage;
    }
}
