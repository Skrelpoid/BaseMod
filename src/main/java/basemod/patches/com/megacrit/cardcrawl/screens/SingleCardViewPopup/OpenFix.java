package basemod.patches.com.megacrit.cardcrawl.screens.SingleCardViewPopup;

import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;

import basemod.patches.com.megacrit.cardcrawl.screens.mainMenu.CardLibraryScreen.EverythingFix;

/**
 * 
 * @author kioeeht from branch custom-content on ModTheSpire
 * https://github.com/kiooeht/ModTheSpire/tree/custom-content
 *
 */
public class OpenFix
{
    @SpirePatch(
        cls="com.megacrit.cardcrawl.screens.SingleCardViewPopup",
        method="open"
    )
    public static class Open
    {
        public static void Prefix(Object __obj_instance, Object cardObj, @ByRef Object[] groupObj)
        {
            if (groupObj[0] == null) {
                AbstractCard card = (AbstractCard)cardObj;
                groupObj[0] = EverythingFix.Initialize.cardGroupMap.get(card.color);
            }
        }
    }
}