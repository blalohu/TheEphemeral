package theEphemeral.cards;

import com.megacrit.cardcrawl.actions.unique.ApotheosisAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import theEphemeral.EphemeralMod;
import theEphemeral.cards.AbstractVanishingCard;
import theEphemeral.characters.TheEphemeral;

import static com.megacrit.cardcrawl.core.CardCrawlGame.languagePack;
import static theEphemeral.EphemeralMod.makeCardPath;

@SuppressWarnings("unused")
public class Coalesce extends AbstractDynamicCard {

    // TEXT DECLARATION

    public static final String ID = EphemeralMod.makeID(Coalesce.class.getSimpleName());
    public static final String IMG = makeCardPath("Coalesce.png");

    // /TEXT DECLARATION/


    // STAT DECLARATION

    private static final CardRarity RARITY = CardRarity.UNCOMMON;
    private static final CardTarget TARGET = CardTarget.SELF;
    private static final CardType TYPE = CardType.SKILL;
    public static final CardColor COLOR = TheEphemeral.Enums.COLOR_EPHEMERAL_PURPLE;

    private static final int COST = 2;
    //private static final int UPGRADED_COST = 0;


    // /STAT DECLARATION/


    public Coalesce() {
        super(ID, IMG, COST, TYPE, COLOR, RARITY, TARGET);
        //tags.add(CardTags.HEALING);
        isEthereal = true;
        exhaust = true;
    }



    // Actions the card should do.
    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        for (AbstractCard card : AbstractDungeon.player.masterDeck.group) {
            if (card instanceof AbstractVanishingCard) {
               ((AbstractVanishingCard) card).defaultBaseSecondMagicNumber ++;
            }
        }


    }
    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName();
            isEthereal = false;
            rawDescription = languagePack.getCardStrings(ID).UPGRADE_DESCRIPTION;
            initializeDescription();
        }
    }

}
