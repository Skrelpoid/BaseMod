package basemod.commands;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class SimpleEndCommand extends AbstractEndCommand {
		
	private BiConsumer<String, String[]> onRun;
	private Consumer<String> canRunChecker;
		
	public SimpleEndCommand (BiConsumer<String, String[]> onRun) {
		this.onRun = Objects.requireNonNull(onRun, "The onRun Consumer must not be null!");
	}
		
	/**
	 * Runs the command. Since this is an end command, it returns null, which will end the command chain.
	 */
	@Override
	public CustomCommand run(String token, String[] fullCommand) {
		onRun.accept(token, fullCommand);
		return null;
	}
	
	/**
	 * Sets the consumer that will be run in the checkCanRun method. If this is null, checkCanRun
	 * will always succeed
	 * 
	 * @param checker a Consumer that takes the current token as its argument. To indicate that a
	 *        command can not be run, an UnsupportedCommandException should be thrown with a message
	 *        that displays in the dev console
	 */
	public void setCanRunChecker(Consumer<String> canRunChecker) {
		this.canRunChecker = canRunChecker;
	}
	
	@Override
	public void checkCanRun(String token) throws InvalidCommandException {
		if (canRunChecker != null) {
			canRunChecker.accept(token);
		}
	}
}