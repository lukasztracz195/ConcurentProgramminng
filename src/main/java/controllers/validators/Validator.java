package controllers.validators;

public interface Validator {

    void validate();

    public boolean isValid();

    public String getErrorMessage();
}
