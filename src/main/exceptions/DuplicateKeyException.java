package exceptions;

/**
 * An exception thrown when key already exist in database
 * @author Filip K.
 *
 */
public class DuplicateKeyException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public DuplicateKeyException(String message) {
		super(message);
	}
}
