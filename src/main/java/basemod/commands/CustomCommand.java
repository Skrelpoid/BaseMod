package basemod.commands;

import java.util.Locale;

public abstract interface CustomCommand {
	
	/**
	 * Runs the command. This will either chain to the next command or end the command
	 * @param token the current token
	 * @param fullCommand the full command that was entered 
	 * @return the next command in the command chain, or null if this is an end command
	 */
	public CustomCommand run(String token, String[] fullCommand);
	
	/**
	 * Checks if the command can be run. This is will be called before each command in the chain, to check if
	 * the command can be run in the current circumstances
	 * @param token the current token
	 * @throws InvalidCommandException this is the exception that should be thrown if the command can not be run.
	 * The message of the exception will be displayed to the user in the dev console
	 */
	public void checkCanRun(String token) throws InvalidCommandException;
	
	/**
	 * This method gets called before the token is passed to the checkCanRun method. It transform a token so it can be understood
	 * by the run and the checkCanRun method. Example use cases would be making a string all lower case or looking up ids that were written with
	 * underscores.
	 * 
	 * IMPORTANT: The default implementation transforms all tokens to be lower case! If this is not wanted, override this method
	 * 
	 * @param token the token to transform
	 * @return the transformed token
	 */
	public default String transformToken(String token) {
		return token.toLowerCase(Locale.ROOT);
	}
	
}
