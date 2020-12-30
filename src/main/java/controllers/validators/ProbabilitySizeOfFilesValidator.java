package controllers.validators;

import java.util.ArrayList;
import java.util.List;

public class ProbabilitySizeOfFilesValidator extends AbstractValidator{

    private List<Integer>  probabilities;

    @Override
    public void validate() {
        super.validate();
        int sum = (int)probabilities.stream().mapToDouble(Integer::intValue).sum();
        if(sum < 100){
            validated =false;
            errorMessage = "Sum probabilities is lower that 1.0";
        }else if(sum > 100){
            validated =false;
            errorMessage = "Sum probabilities is grate that 1.0";
        }else {
            validated = true;
        }
    }

    public static ProbabilitySizeOfFilesValidator of(List<Integer> probabilities){
        return new ProbabilitySizeOfFilesValidator(probabilities);
    }

    private ProbabilitySizeOfFilesValidator(List<Integer> probabilities) {
        this.probabilities = probabilities;
    }
}
