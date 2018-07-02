package helpers;

import lombok.EqualsAndHashCode;
import lombok.Value;
import play.mvc.Result;

@EqualsAndHashCode(callSuper = true)
@Value
public class InvalidCredentialsException extends RuntimeException {

    private Result errorResult;
}
