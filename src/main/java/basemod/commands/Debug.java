package basemod.commands;

import com.megacrit.cardcrawl.core.Settings;
import basemod.DevConsole;

public class Debug extends AbstractIntermediateCommand {

	public Debug() {
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
		return "could not parse previous command\n" + 
				"options are:\n" + 
				"* true\n" + 
				"* false"; 
	}

}
