package theEphemeral.powers;

import basemod.interfaces.CloneablePowerInterface;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.common.ExhaustAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import theEphemeral.EphemeralMod;
import theEphemeral.util.TextureLoader;

import java.util.ArrayList;

import static theEphemeral.EphemeralMod.makePowerPath;

public class ReturnToAetherPower extends AbstractPower implements CloneablePowerInterface {
    public static final String POWER_ID = EphemeralMod.makeID(ReturnToAetherPower.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private static final Texture tex84 = TextureLoader.getTexture(makePowerPath("ReturnToAether84.png"));
    private static final Texture tex32 = TextureLoader.getTexture(makePowerPath("ReturnToAether32.png"));

    public static final int MaxStackAmount = 999;

    public ReturnToAetherPower() {
        name = NAME;
        ID = POWER_ID;

        this.owner = AbstractDungeon.player;

        type = PowerType.BUFF;

        this.region128 = new TextureAtlas.AtlasRegion(tex84, 0, 0, 84, 84);
        this.region48 = new TextureAtlas.AtlasRegion(tex32, 0, 0, 32, 32);

        updateDescription();
    }

    @Override
    public void atEndOfTurnPreEndTurnCards(boolean isPlayer) {
        if (isPlayer) {
            flash();
            AbstractPlayer p = AbstractDungeon.player;
            ArrayList<AbstractCard> cursesAndStatuses = new ArrayList<>();
            for (AbstractCard c : p.hand.group) {
                if (c.type == AbstractCard.CardType.CURSE || c.type == AbstractCard.CardType.STATUS)
                    cursesAndStatuses.add(c);
            }

            for (AbstractCard c : cursesAndStatuses) {
                p.hand.moveToExhaustPile(c);
            }
        }
    }

    @Override
    public void atEndOfTurn(boolean isPlayer) {
        if (isPlayer) {
            AbstractPlayer p = AbstractDungeon.player;
            addToBot(new ExhaustAction(p, p, p.hand.size(), false));
        }
    }

    @Override
    public void updateDescription() {
        description = DESCRIPTIONS[0];
    }

    @Override
    public AbstractPower makeCopy() {
        return new ReturnToAetherPower();
    }
}
