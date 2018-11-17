package basemod.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import basemod.BaseMod;
import basemod.DevConsole;
import basemod.helpers.ConvertHelper;

public class HandCommand extends AbstractIntermediateCommand {
	public static final String[] ADD_ERROR = {"could not parse previous command", 
			"options are:", "* add [id] {count} {upgrade amt}"};
	public static final String[] REMOVE_ERROR = {"could not parse previous command", 
			"options are:", "* remove [id]", "* remove all"};
	public static final String[] DISCARD_ERROR = {"could not parse previous command", 
			"options are:", "* discard [id]", "* discard all"};
	public static final String[] SET_ERROR = {"could not parse previous command", 
			"options are:", "* set damage [id] [amount]",
			"* set block [id] [amount]", "* set magic [id] [amount]",
			"* set cost [id] [amount]"};
	public static final String[] DEFAULT_ERROR = { "could not parse previous command", 
	"options are:", "* add [id] {count} {upgrade amt}", "* remove [id]",
	"* remove all", "* discard [id]", "* discard all", "* set damage [id] [amount]",
	"* set block [id] [amount]", "* set magic [id] [amount]",
	"* set cost [id] [amount]" };
	
	private int countIndex;
	private String cardName;
	private String[] cardNameArray;

	public HandCommand() {
		addCommand();
		removeCommand();
		discardCommand();
		setCommand();
	}
	
	private void prepareCardName(int nameStart, String[] tokens) {
		countIndex = tokens.length - 1;
		while (ConvertHelper.tryParseInt(tokens[countIndex], 0) != 0) {
			countIndex--;
		}
		cardNameArray = Arrays.copyOfRange(tokens, nameStart, countIndex + 1);
		cardName = String.join(" ", cardNameArray);
		
		// If the ID was written using underscores, find the original ID
		if (BaseMod.underScoreCardIDs.containsKey(cardName)) {
			cardName = BaseMod.underScoreCardIDs.get(cardName);
		}
	}
	
	private void prepareSetCardName(String[] tokens) {
		cardNameArray = Arrays.copyOfRange(tokens, 3, tokens.length - 1);
		cardName = String.join(" ", cardNameArray);
		
		// If the ID was written using underscores, find the original ID
		if (BaseMod.underScoreCardIDs.containsKey(cardName)) {
			cardName = BaseMod.underScoreCardIDs.get(cardName);
		}
	}
	
	private void addCommand() {
		SimpleIntermediateCommand add = new SimpleIntermediateCommand();
		add.setPossibleSubCommands(CommandsHelper::getCardIds);
		add.setDefaultErrorMessage(String.join(InvalidCommandException.LINE_DELIMITER, ADD_ERROR));
		add.setCanRunChecker(s -> {
			if (AbstractDungeon.player == null) {
				throw new InvalidCommandException("cannot add cards when player doesn't exist");
			}
		});
		// should run end command when there are just 3 commands (hand add id)
		add.setEndCommandChecker((s, arr) -> arr.length == 3);
		putSubCommand("add", add);
		putSubCommand("a", add);
		
		SimpleIntermediateCommand count = CommandsHelper.newSmallNumbersTemplate();
		// should run end command when there are just 4 commands (hand add id count)
		count.setEndCommandChecker((s, arr) -> arr.length == 4);
		count.setDefaultErrorMessage(String.join(InvalidCommandException.LINE_DELIMITER, ADD_ERROR));
		add.setDefaultSubCommand(count);
		
		SimpleIntermediateCommand upgrades = CommandsHelper.newSmallNumbersTemplate();
		upgrades.setDefaultErrorMessage(String.join(InvalidCommandException.LINE_DELIMITER, ADD_ERROR));
		count.setDefaultSubCommand(upgrades);
		
		SimpleEndCommand end = new SimpleEndCommand(this::executeAdd);
		add.setEndCommand(end);
		count.setEndCommand(end);
		upgrades.setDefaultSubCommand(end);
	}
	
	private void executeAdd(String token, String[] tokens) {
		prepareCardName(2, tokens);
		
		AbstractCard c = CardLibrary.getCard(cardName);
		if (c == null) {
			throw new InvalidCommandException("could not find card " + cardName);
		}
		
		// card count
		int count = 1;
		if (tokens.length > countIndex + 1 && ConvertHelper.tryParseInt(tokens[countIndex + 1], 0) != 0) {
			count = ConvertHelper.tryParseInt(tokens[countIndex + 1], 0);
		}

		int upgradeCount = 0;
		if (tokens.length > countIndex + 2) {
			upgradeCount = ConvertHelper.tryParseInt(tokens[countIndex + 2], 0);
		}

		DevConsole.log("adding " + count + (count == 1 ? " copy of " : " copies of ") + cardName + " with " + upgradeCount + " upgrade(s)");

		for (int i = 0; i < count; i++) {
			AbstractCard copy = c.makeCopy();
			for (int j = 0; j < upgradeCount; j++) {
				copy.upgrade();
			}

			AbstractDungeon.actionManager.addToBottom(new MakeTempCardInHandAction(copy, true));
		}
	}
	
	private Collection<String> cardIdsAndAll() {
		Collection<String> result = CommandsHelper.getCardIds();
		result.add("all");
		return result;
	}
	
	private void removeCommand() {
		SimpleIntermediateCommand remove = new SimpleIntermediateCommand();
		remove.setPossibleSubCommands(this::cardIdsAndAll);
		remove.setDefaultErrorMessage(String.join(InvalidCommandException.LINE_DELIMITER, REMOVE_ERROR));
		remove.setCanRunChecker(s -> {
			if (AbstractDungeon.player == null) {
				throw new InvalidCommandException("cannot remove cards when player doesn't exist");
			}
		});
		
		putSubCommand("remove", remove);
		putSubCommand("r", remove);
		
		SimpleEndCommand end = new SimpleEndCommand(this::executeRemove);
		remove.setDefaultSubCommand(end);
	}
	
	private void executeRemove(String token, String[] tokens) {
		prepareCardName(2, tokens);
		
		// remove all cards
		if (tokens[2].equalsIgnoreCase("all")) {
			for (AbstractCard c : new ArrayList<>(AbstractDungeon.player.hand.group)) {
				AbstractDungeon.player.hand.moveToExhaustPile(c);
			}
			return;
			// remove single card
		} else {
			boolean removed = false;
			AbstractCard toRemove = null;
			for (AbstractCard c : AbstractDungeon.player.hand.group) {
				if (removed) {
					break;
				}
				if (c.cardID.equals(cardName)) {
					toRemove = c;
					removed = true;
				}
			}
			if (removed) {
				AbstractDungeon.player.hand.moveToExhaustPile(toRemove);
			}
		}
	}
	
	private void discardCommand() {
		SimpleIntermediateCommand discard = new SimpleIntermediateCommand();
		discard.setPossibleSubCommands(this::cardIdsAndAll);
		discard.setDefaultErrorMessage(String.join(InvalidCommandException.LINE_DELIMITER, DISCARD_ERROR));
		discard.setCanRunChecker(s -> {
			if (AbstractDungeon.player == null) {
				throw new InvalidCommandException("cannot discard cards when player doesn't exist");
			}
		});
		
		putSubCommand("discard", discard);
		putSubCommand("d", discard);
		
		SimpleEndCommand end = new SimpleEndCommand(this::executeDiscard);
		discard.setDefaultSubCommand(end);
	}
	
	private void executeDiscard(String token, String[] tokens) {
		prepareCardName(2, tokens);
		if (tokens[2].equalsIgnoreCase("all")) {
			// discard all cards
			for (AbstractCard c : new ArrayList<>(AbstractDungeon.player.hand.group)) {
				AbstractDungeon.player.hand.moveToDiscardPile(c);
				c.triggerOnManualDiscard();
				GameActionManager.incrementDiscard(false);
			}
		} else {
			// discard single card
			for (AbstractCard c : AbstractDungeon.player.hand.group) {
				if (c.cardID.equals(cardName)) {
					AbstractDungeon.player.hand.moveToDiscardPile(c);
					c.triggerOnManualDiscard();
					GameActionManager.incrementDiscard(false);
					return;
				}
			}
		}
	}
	
	private void setCommand() {
		SimpleIntermediateCommand set = new SimpleIntermediateCommand();
		set.setDefaultErrorMessage(String.join(InvalidCommandException.LINE_DELIMITER, SET_ERROR));
		set.setCanRunChecker(s -> {
			if (AbstractDungeon.player == null) {
				throw new InvalidCommandException("cannot set card attributes when player doesn't exist");
			}
		});
		set.setPossibleSubCommands(() -> {
			// only show full words as suggestions, not d, b, m, c, 
			return set.subCommands.keySet().stream()
					.filter(s -> s.length() > 1)
					.collect(Collectors.toList());
		});
		
		putSubCommand("set", set);
		putSubCommand("s", set);
		
		setDamageCommand(set);
		setBlockCommand(set);
		setMagicCommand(set);
		setCostCommand(set);
	}

	private void setDamageCommand(SimpleIntermediateCommand set) {
		SimpleIntermediateCommand damage = new SimpleIntermediateCommand();
		damage.setDefaultErrorMessage(String.join(InvalidCommandException.LINE_DELIMITER, SET_ERROR));
		damage.setPossibleSubCommands(this::cardIdsAndAll);
		
		set.putSubCommand("damage", damage);
		set.putSubCommand("d", damage);
		
		SimpleIntermediateCommand damageValue = CommandsHelper.newMediumNumbersTemplate();
		damageValue.setDefaultErrorMessage(String.join(InvalidCommandException.LINE_DELIMITER, SET_ERROR));
		
		damage.setDefaultSubCommand(damageValue);
		
		SimpleEndCommand damageEnd = new SimpleEndCommand((str, tokens) -> executeSet(str, tokens, damageAction));
		damageValue.setDefaultSubCommand(damageEnd);
	}
	
	private BiConsumer<AbstractCard, Integer> damageAction = (c, v) -> {
		if (c.baseDamage != v) c.upgradedDamage = true;
		c.baseDamage = v;
	};
	
	private void setBlockCommand(SimpleIntermediateCommand set) {
		SimpleIntermediateCommand block = new SimpleIntermediateCommand();
		block.setDefaultErrorMessage(String.join(InvalidCommandException.LINE_DELIMITER, SET_ERROR));
		block.setPossibleSubCommands(this::cardIdsAndAll);
		
		set.putSubCommand("block", block);
		set.putSubCommand("b", block);
		
		SimpleIntermediateCommand blockValue = CommandsHelper.newMediumNumbersTemplate();
		blockValue.setDefaultErrorMessage(String.join(InvalidCommandException.LINE_DELIMITER, SET_ERROR));
		
		block.setDefaultSubCommand(blockValue);
		
		SimpleEndCommand blockEnd = new SimpleEndCommand((str, tokens) -> executeSet(str, tokens, blockAction));
		blockValue.setDefaultSubCommand(blockEnd);
	}
	
	private BiConsumer<AbstractCard, Integer> blockAction = (c, v) -> {
		if (c.baseBlock != v) c.upgradedBlock = true;
		c.baseBlock = v;
	};
	
	private void setMagicCommand(SimpleIntermediateCommand set) {
		SimpleIntermediateCommand magic = new SimpleIntermediateCommand();
		magic.setDefaultErrorMessage(String.join(InvalidCommandException.LINE_DELIMITER, SET_ERROR));
		magic.setPossibleSubCommands(this::cardIdsAndAll);
		
		set.putSubCommand("magic", magic);
		set.putSubCommand("m", magic);
		
		SimpleIntermediateCommand magicValue = CommandsHelper.newMediumNumbersTemplate();
		magicValue.setDefaultErrorMessage(String.join(InvalidCommandException.LINE_DELIMITER, SET_ERROR));
		
		magic.setDefaultSubCommand(magicValue);
		
		SimpleEndCommand magicEnd = new SimpleEndCommand((str, tokens) -> executeSet(str, tokens, magicAction));
		magicValue.setDefaultSubCommand(magicEnd);
	}
	
	private BiConsumer<AbstractCard, Integer> magicAction = (c, v) -> {
		if (c.baseMagicNumber != v) c.upgradedMagicNumber = true;
		c.magicNumber = c.baseMagicNumber = v;
	};
	
	private void setCostCommand(SimpleIntermediateCommand set) {
		SimpleIntermediateCommand cost = new SimpleIntermediateCommand();
		cost.setDefaultErrorMessage(String.join(InvalidCommandException.LINE_DELIMITER, SET_ERROR));
		cost.setPossibleSubCommands(this::cardIdsAndAll);
		
		set.putSubCommand("cost", cost);
		set.putSubCommand("c", cost);
		
		SimpleIntermediateCommand costValue = CommandsHelper.newSmallNumbersTemplate();
		costValue.setDefaultErrorMessage(String.join(InvalidCommandException.LINE_DELIMITER, SET_ERROR));
		
		cost.setDefaultSubCommand(costValue);
		
		SimpleEndCommand costEnd = new SimpleEndCommand((str, tokens) -> executeSet(str, tokens, costAction));
		costValue.setDefaultSubCommand(costEnd);
	}
	
	private BiConsumer<AbstractCard, Integer> costAction = (c, v) -> {
		if (c.cost != v) {
			c.updateCost(v - c.cost);
		}
	};
	
	private void executeSet(String token, String[] tokens, BiConsumer<AbstractCard, Integer> setAction) {
		prepareSetCardName(tokens);
		
		boolean all = tokens[3].equalsIgnoreCase("all");
		String numberToken = tokens[tokens.length - 1];
		int v = 0;
		try {
			v = Integer.parseInt(numberToken);
		} catch (NumberFormatException ex) {
			throw new InvalidCommandException(numberToken + " is not a valid number", ex);
		}
		for (AbstractCard c : new ArrayList<>(AbstractDungeon.player.hand.group)) {
			if (all || c.cardID.equals(cardName)) {
				setAction.accept(c, v);
				c.displayUpgrades();
				c.applyPowers();
				if (!all) break;
			}
		}
	}

	@Override
	public String defaultErrorMessage() {
		return String.join(InvalidCommandException.LINE_DELIMITER, DEFAULT_ERROR);
	}
	
	@Override
	public Collection<String> possibleSubCommands() {
		return super.possibleSubCommands().stream()
				// only show full words as suggestions, not a, d, r, s
				.filter(s -> s.length() > 1)
				.collect(Collectors.toList());
	}

}
