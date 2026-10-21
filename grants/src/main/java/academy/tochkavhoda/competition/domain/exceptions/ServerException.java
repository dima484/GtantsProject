package academy.tochkavhoda.competition.domain.exceptions;

public class ServerException extends Exception {

    private static final long serialVersionUID = 1L;

    private final ErrorCode errorCode;

    public ServerException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ServerException(ErrorCode errorCode, String details) {
        super(errorCode.getMessage() + ": " + details);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
