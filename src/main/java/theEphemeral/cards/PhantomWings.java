package theEphemeral.cards;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import theEphemeral.EphemeralMod;
import theEphemeral.characters.TheEphemeral;
import theEphemeral.powers.PhantomWingsPower;
import theEphemeral.previewWidget.PreviewWidget;

import static com.megacrit.cardcrawl.core.CardCrawlGame.languagePack;
import static theEphemeral.EphemeralMod.makeCardPath;

@SuppressWarnings("unused")
public class PhantomWings extends AbstractDynamicCard {

    // TEXT DECLARATION

    public static final String ID = EphemeralMod.makeID(PhantomWings.class.getSimpleName());
    public static final String IMG = makeCardPath("PhantomWings.png");

    // /TEXT DECLARATION/


    // STAT DECLARATION

    private static final CardRarity RARITY = CardRarity.UNCOMMON;
    private static final CardTarget TARGET = CardTarget.SELF;
    private static final CardType TYPE = CardType.POWER;
    public static final CardColor COLOR = TheEphemeral.Enums.COLOR_EPHEMERAL_PURPLE;

    private static final int COST = 1;
    private static final int MAGIC_NUMBER = 1;

    // /STAT DECLARATION/

    public PhantomWings() {
        super(ID, IMG, COST, TYPE, COLOR, RARITY, TARGET);
        magicNumber = baseMagicNumber = MAGIC_NUMBER;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        if (upgraded) {
            int block = PreviewWidget.GetAugury();
            if (block > 0) {
                addToBot(new GainBlockAction(AbstractDungeon.player, block));
            }
        }

        addToBot(new ApplyPowerAction(p, p,
                new PhantomWingsPower(magicNumber), magicNumber));
    }

    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName();
            rawDescription = languagePack.getCardStrings(ID).UPGRADE_DESCRIPTION;
            initializeDescription();
        }
    }
}
