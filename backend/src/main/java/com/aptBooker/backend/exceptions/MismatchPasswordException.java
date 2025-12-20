
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class MismatchPasswordException extends RuntimeException {
    public MismatchPasswordException(String message){
        super(message);
    }
}
