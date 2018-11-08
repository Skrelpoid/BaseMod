package basemod.commands;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class SimpleEndCommand extends AbstractEndCommand {
		
	private BiConsumer<String, String[]> onRun;
	private Consumer<String> checkCanRun;
		
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
	
	@Override
	public void checkCanRun(String token) throws UnsupportedCommandException {
		checkCanRun.accept(token);
	}
}