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
	protected String defaultMessage;
	
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
			return endCommand;
		}
		CustomCommand nextCommandInChain = subCommands.get(token);
		if (nextCommandInChain == null) {
			if (defaultSubCommand != null) {
				nextCommandInChain = defaultSubCommand;
			} else {
				throw new UnsupportedCommandException("could not run command");
			}
		}
		return nextCommandInChain;
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
	 * The default message is a String that gets displayed on the Autocomplete when no match was
	 * found. If this is null, the message will be "No Match found". <br>
	 * This can be used to indicate that the possibleSubCommands only include some of real possible
	 * values. An example usage would be to use this for arbitrary <b>numbers</b> to indicate to the
	 * user that their input is not wrong.
	 */
	public String defaultMessage() {
		return defaultMessage;
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
	 * Sets the end command. This end command will be chained from the run method if shouldRunEndCommand returns true.
	 * 
	 * @param cmd the end command to chain when shouldRunEndCommand is true
	 */
	public void setEndCommand(AbstractEndCommand cmd) {
		endCommand = cmd;
	}
}
