package basemod.commands;

public  class UnsupportedCommandException extends RuntimeException {

	private static final long serialVersionUID = -8222054079373879992L;
		
	public UnsupportedCommandException(String message) {
		super(message);
	}
		
	public UnsupportedCommandException(String message, Throwable cause) {
		super(message, cause);
	}
		
}