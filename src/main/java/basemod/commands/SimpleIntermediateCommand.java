package basemod.commands;

import java.util.Collection;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class SimpleIntermediateCommand extends AbstractIntermediateCommand {

	private Supplier<Collection<String>> possibleSubCommands;
	private Consumer<String> canRunChecker;
	private BiPredicate<String, String[]> endCommandChecker;
	private UnaryOperator<String> tokenTransformer;

	public SimpleIntermediateCommand() {
		super();
	}

	/**
	 * A Collection of possible subcommands. These values will be displayed to the user with the
	 * Autocomplete. The Collection does not have to be ordered. If you do not set these, they will
	 * default to the keys of the sub commands map. You should set these if you use the
	 * defaultSubCommand.
	 */
	@Override
	public Collection<String> possibleSubCommands() {
		if (possibleSubCommands != null) {
			return possibleSubCommands.get();
		} else {
			return subCommands.keySet();
		}
	}

	/**
	 * A Collection of possible subcommands. These values will be displayed to the user with the
	 * Autocomplete. The Collection does not have to be ordered. If you do not set these, or set
	 * them to null, they will default to the keys of the sub commands map. You should set these if
	 * you use the defaultSubCommand.
	 * 
	 * @param possibles The Supplier that returns a collection of the possible sub commands. This is
	 *        to make it easier to change values based on circumstances, e.g. the number of potion
	 *        slots the player has
	 */
	public void setPossibleSubCommands(Supplier<Collection<String>> possibles) {
		this.possibleSubCommands = possibles;
	}

	/**
	 * Sets the default autocomplete message to the message supplied. The default message is displayed in the auto cmpletion
	 * if the user enters a value for the current token that is not mapped by this command.
	 * This can be used to indicate what the value should be, e.g. a number.
	 * 
	 * @param message the default message. If this is null, "no match found" will be displayed
	 */
	public void setDefaultAutocompleteMessage(String message) {
		this.defaultAutocompleteMessage = message;
	}
	
	/**
	 * Sets the default message to the message supplied. The default message is displayed in the dev
	 * console if the runs a command that can not be executed
	 * 
	 * @param message the default message. If this is null, "could not parse previous command" will be displayed
	 */
	public void setDefaultErrorMessage(String message) {
		this.defaultErrorMessage = message;
	}

	/**
	 * Checks if this command can be run using the canRunChecker
	 */
	@Override
	public void checkCanRun(String token) throws UnsupportedCommandException {
		if (canRunChecker != null) {
			canRunChecker.accept(token);
		}
	}

	/**
	 * Sets the consumer that will be run in the checkCanRun method. If this is null, checkCanRun
	 * will always succeed
	 * 
	 * @param checker a Consumer that takes the current token as its argument. To indicate that a
	 *        command can not be run, an UnsupportedCommandException should be thrown with a message
	 *        that displays in the dev console
	 */
	public void setCanRunChecker(Consumer<String> checker) {
		this.canRunChecker = checker;
	}
	
	@Override
	public boolean shouldRunEndCommand(String token, String[] fullCommand) {
		return endCommandChecker == null || endCommandChecker.test(token, fullCommand);
	}
	
	/**
	 * Sets the end command checker, which returns true if the current invocation of this command should
	 * not go further in the chain but instead return the endCommand
	 * 
	 * @param endCommandChecker can be null to indicate to never run the end command
	 */
	public void setEndCommandChecker(BiPredicate<String, String[]> endCommandChecker) {
		this.endCommandChecker = endCommandChecker;
	}
	
	@Override
	public String transformToken(String token) {
		return tokenTransformer == null ? super.transformToken(token) : tokenTransformer.apply(token);
	}
	
	/**
	 * Set the token transformer, which transforms a token before it is passed to the run method. Id this is not set, or null 
	 * gets passed, the default implementation will be used, which converts the String to lower case
	 */
	public void setTokenTransformer(UnaryOperator<String> transformer) {
		tokenTransformer = transformer;
	}
}
