package basemod.commands;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * This is a supertype for intermediate commands. An intermediate command is a custom command that only
 * maps tokens to the next command in the chain. Under certain circumstances it can chain directly to an end command,
 * using shouldRunEndCommand, to enable commands with dynamic argument lengths / optional arguments
 */
public abstract class AbstractIntermediateCommand implements CustomCommand {
	protected Map<String, CustomCommand> subCommands;
	protected CustomCommand defaultSubCommand;
	protected AbstractEndCommand endCommand;
	protected String defaultAutocompleteMessage;
	protected String defaultErrorMessage;
	
	public AbstractIntermediateCommand() {
		subCommands = new HashMap<>();
	}
	
	/**
	 * Checks whether the given token is supported by this command by checking the subCommands and
	 * the defaultSubCommand. If there is no defaultSubCommand and the token has no command mapped
	 * to it, this method will throw an UnsupportedCommandException
	 * 
	 * @param token the current token of the command chain.
	 * @throws UnsupportedCommandException if there is no defaultSubCommand and the token has no
	 *         command mapped to it
	 */
	@Override
	public CustomCommand run(String token, String fullCommand[]) throws UnsupportedCommandException {
		if (shouldRunEndCommand(token, fullCommand)) {
			if (endCommand != null) {
				token = endCommand.transformToken(token);
				endCommand.checkCanRun(token);
				return endCommand.run(token, fullCommand);
			}
		}
		CustomCommand nextCommandInChain = subCommands.get(token);
		if (nextCommandInChain == null) {
			if (defaultSubCommand != null) {
				nextCommandInChain = defaultSubCommand;
			} else {
				throw new UnsupportedCommandException(defaultErrorMessage());
			}
		}
		return nextCommandInChain;
	}
	
	/** default implementation that does nothing */
	@Override
	public void checkCanRun(String token) throws UnsupportedCommandException {
		
	}
	
	/**
	 * This method checks, after the checkCanRun method, if this command is finished and should run the end command,
	 * that is associated with this command. This can be used to make commands with a flexible size of arguments. You can return that
	 * the end command should be run e.g. when the full Command has exactly 4 tokens
	 * 
	 * @param token the current token
	 * @param fullCommand the full command that was entered
	 * @return true if the end command should be run, otherwise false
	 */
	public  boolean shouldRunEndCommand(String token, String[] fullCommand) {
		return false;
	}
	
	/**
	 * A Collection of possible subcommands. These values will be displayed to the user with the
	 * Autocomplete. The Collection does not have to be ordered. The default implementation just returns the
	 * key set of the sub commands
	 */
	public Collection<String> possibleSubCommands() {
		return subCommands.keySet();
	}
	
	/**
	 * The default autocomplete message is a String that gets displayed on the Autocomplete when no match was
	 * found. If this is null, the message will be "no natch found". <br>
	 * This can be used to indicate that the possibleSubCommands only include some of real possible
	 * values. An example usage would be to use this for arbitrary <b>numbers</b> to indicate to the
	 * user that their input is not wrong.
	 */
	public String defaultAutocompleteMessage() {
		return defaultAutocompleteMessage == null ? "no match found" : defaultAutocompleteMessage;
	}
	
	/**
	 * The default error message is a String that gets printed to the dev console when a token is not mapped to a command.
	 * If this is null, the message will be "could not parse previous command". <br>
	 */
	public String defaultErrorMessage() {
		return defaultErrorMessage == null ? "could not parse previous command" : defaultErrorMessage;
	}
	
	/**
	 * Maps the token to the command. Overwrites the command if it already exists in the map
	 * 
	 * @param token the token that identifies the command
	 * @param cmd the command identified by the token
	 */
	public void putSubCommand(String token, CustomCommand cmd) {
		subCommands.put(token, cmd);
	}
	/**
	 * Returns the command mapped by the specified token, or null if it is not mapped
	 * @param token the token that maps the command
	 * @return null, or a command thats mapped by the specified token
	 */
	public CustomCommand getSubCommandFor(String token) {
		return subCommands.get(token);
	}
	
	/**
	 * removes the command identified by the token from the map of subcommands
	 * 
	 * @param token the token that identifies the command to be removed
	 * @return the command that was removed, or null if no command was removed
	 */
	public CustomCommand removeSubCommand(String token) {
		return subCommands.remove(token);
	}
	
	/**
	 * Sets the default sub command, which is the command that will be chained if the token in run is not mapped by this command
	 * The default sub command can be null to indicat ethat only tokens mapped by this command are valid.
	 */
	public void setDefaultSubCommand(CustomCommand cmd) {
		this.defaultSubCommand = cmd;
	}
	
	/**
	 * @return the default sub command, which will be chained if it is not null and teh token is not mapped by this command
	 */
	public CustomCommand getDefaultSubCommand() {
		return defaultSubCommand;
	}
	
	/**
	 * Sets the end command. This end command will be run from the run method if shouldRunEndCommand returns true.
	 * 
	 * @param cmd the end command to run when shouldRunEndCommand is true
	 */
	public void setEndCommand(AbstractEndCommand cmd) {
		endCommand = cmd;
	}
	
	/**
	 * Gets the end command. This end command will be run from the run method if shouldRunEndCommand returns true.
	 */
	public AbstractEndCommand getEndCommand() {
		return endCommand;
	}
}
