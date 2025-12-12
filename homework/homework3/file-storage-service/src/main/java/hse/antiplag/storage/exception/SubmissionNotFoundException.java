package hse.antiplag.storage.exception;

public class SubmissionNotFoundException extends RuntimeException {
    public SubmissionNotFoundException(Long id) {
        super("Submission with id=" + id + " not found");
    }
}
