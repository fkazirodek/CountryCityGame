package exceptions;

/**
 * An exception thrown when player not found in database
 * @author Filip K.
 *
 */
public class PlayerNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public PlayerNotFoundException() {}
	
	public PlayerNotFoundException(String message) {
		super(message);
	}
}
