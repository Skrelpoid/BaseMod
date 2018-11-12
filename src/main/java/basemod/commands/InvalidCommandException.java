package basemod.commands;

public  class InvalidCommandException extends RuntimeException {
	
	public static final String LINE_DELIMITER = "\n";

	private static final long serialVersionUID = -8222054079373879992L;
		
	public InvalidCommandException(String message) {
		super(message);
	}
		
	public InvalidCommandException(String message, Throwable cause) {
		super(message, cause);
	}
		
}