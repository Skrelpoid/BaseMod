package basemod.commands;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.BlightHelper;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.PotionHelper;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.localization.LocalizedStrings;
import basemod.BaseMod;
import basemod.ReflectionHacks;

/**
 * A Class that holds many helper and utility methods for creating and manipulating commands
 */
public final class CommandsHelper {

	private CommandsHelper() {
		throw new AssertionError("CommandsHelper cannot be instantiated");
	}
	
	/**
	 * @return a collection of Strings with numbers from 1 to 9 (inclusive)
	 */
	public static Collection<String> smallNumbers() {
		return IntStream.rangeClosed(1, 9).mapToObj(String::valueOf).collect(Collectors.toList());
	}
	
	/**
	 * Creates a new Command with a default auto complete message "number" and with numbers from 1 to 9 (inclusive)
	 * as possibleSubCommands. Setting the next command as the default sub command of this command is the prefered
	 * way to handle number arguments to allow any number
	 */
	public static AbstractIntermediateCommand newSmallNumberTemplate() {
		SimpleIntermediateCommand numberCommand = new SimpleIntermediateCommand();
		numberCommand.setDefaultAutocompleteMessage("number");
		numberCommand.setPossibleSubCommands(CommandsHelper::smallNumbers);
		return numberCommand;
	}
	
	/**
	 * @return a collection of Strings with numbers 10, 20, 30... to 90 (inclusive)
	 * and 100, 200, 300... to 900 (inclusive)
	 */
	public static Collection<String> mediumNumbers() {
		return IntStream.rangeClosed(1, 9)
				.flatMap(i -> IntStream.of(i * 10, i * 100))
				.mapToObj(String::valueOf)
				.collect(Collectors.toList());
	}
	
	/**
	 * Creates a new Command with a default auto complete message "number" and with numbers 10, 20, 30... to 90 (inclusive)
	 * and 100, 200, 300... to 900 (inclusive) as possibleSubCommands. Setting the next command as the default 
	 * sub command of this command is the prefered way to handle number arguments to allow any number
	 */
	public static AbstractIntermediateCommand newMediumNumberTemplate() {
		SimpleIntermediateCommand numberCommand = new SimpleIntermediateCommand();
		numberCommand.setDefaultAutocompleteMessage("number");
		numberCommand.setPossibleSubCommands(CommandsHelper::mediumNumbers);
		return numberCommand;
	}
	
	/**
	 * @return a collection of Strings with numbers 100, 200, 300... to 900 (inclusive)
	 * and 1000, 2000, 3000... to 9000 (inclusive)
	 */
	public static Collection<String> bigNumbers() {
		return IntStream.rangeClosed(1, 9)
				.flatMap(i -> IntStream.of(i * 100, i * 1000))
				.mapToObj(String::valueOf)
				.collect(Collectors.toList());
	}
	
	/**
	 * Creates a new Command with a default auto complete message "number" and with numbers 100, 200, 300... to 900 (inclusive)
	 * and 1000, 2000, 3000... to 9000 (inclusive) as possibleSubCommands. Setting the next command as the default 
	 * sub command of this command is the prefered way to handle number arguments to allow any number
	 */
	public static AbstractIntermediateCommand newbigNumberTemplate() {
		SimpleIntermediateCommand numberCommand = new SimpleIntermediateCommand();
		numberCommand.setDefaultAutocompleteMessage("number");
		numberCommand.setPossibleSubCommands(CommandsHelper::mediumNumbers);
		return numberCommand;
	}
	
	/**
	 * @param id the id you want to convert
	 * @return the id with every space replace with an underscore
	 */
	public static String toUnderScoreId(String id) {
		return id.replace(' ', '_');
	}
	
	/**
	 * @return a collection of card ids to be used by commands
	 */
	public static Collection<String> getCardIds() {
		return CardLibrary.cards.keySet().stream()
				.map(CommandsHelper::toUnderScoreId)
				.collect(Collectors.toList());
	}
	
	/**
	 * @return a collection of power ids to be used by commands
	 */
	public static Collection<String> getPowerIds() {
		return BaseMod.getPowerKeys().stream()
				.map(CommandsHelper::toUnderScoreId)
				.collect(Collectors.toList());
	}
	
	/**
	 * @return a collection of potion ids to be used by commands
	 */
	public static Collection<String> getPotionIds() {
		if (PotionHelper.potions == null) {
			return Collections.EMPTY_LIST;
		}
		return PotionHelper.potions.stream()
				.map(CommandsHelper::toUnderScoreId)
				.collect(Collectors.toList());
	}
	
	/**
	 * @return a collection of event ids to be used by commands
	 */
	public static Collection<String> getEventIds() {
		@SuppressWarnings("unchecked")
		Map<String, EventStrings> events = (Map<String, EventStrings>) (ReflectionHacks
				.getPrivateStatic(LocalizedStrings.class, "events"));
		if (events == null) {
			return Collections.EMPTY_LIST;
		}
		return events.keySet().stream()
				.map(CommandsHelper::toUnderScoreId)
				.collect(Collectors.toList());
	}
	
	/**
	 * @return a collection of encounter ids to be used by commands
	 */
	public static Collection<String> getEncounterIds() {
		return BaseMod.encounterList.stream()
				.map(CommandsHelper::toUnderScoreId)
				.collect(Collectors.toList());
	}
	
	/**
	 * @return a collection of relic ids to be used by commands
	 */
	public static Collection<String> getRelicIds() {
		return BaseMod.listAllRelicIDs().stream()
				.map(CommandsHelper::toUnderScoreId)
				.collect(Collectors.toList());
	}
	
	/**
	 * @return a collection of blight ids to be used by commands
	 */
	public static Collection<String> getBlightIds() {
		return BlightHelper.blights.stream()
				.map(CommandsHelper::toUnderScoreId)
				.collect(Collectors.toList());
	}
	
	/**
	 * @return a collection of strings representing valid potion ids at the time this method was called
	 */
	public static Collection<String> getPotionSlots() {
		if (AbstractDungeon.player == null) {
			return Collections.EMPTY_LIST;
		}
		return IntStream.range(0, AbstractDungeon.player.potionSlots)
				.mapToObj(String::valueOf)
				.collect(Collectors.toList());
	}


}
