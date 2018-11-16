package basemod.commands;

import com.megacrit.cardcrawl.core.Settings;
import basemod.DevConsole;

public class DebugCommand extends AbstractIntermediateCommand {
	
	public static String[] DEFAULT_ERROR = { "could not parse previous command", "options are:", "* true", "* false" };

	public DebugCommand() {
		SimpleEndCommand debugTrue = new SimpleEndCommand((t, c) -> setDebugModeAndLog(true));
		putSubCommand("true", debugTrue);
		SimpleEndCommand debugFalse = new SimpleEndCommand((t, c) -> setDebugModeAndLog(false));
		putSubCommand("false", debugFalse);
	}
	
	private void setDebugModeAndLog(boolean debug) {
		Settings.isDebug = debug;
		DevConsole.log("Setting debug mode to: " + debug);
	}
	
	@Override
	public String defaultErrorMessage() {
		return String.join(InvalidCommandException.LINE_DELIMITER, DEFAULT_ERROR);
	}

}
