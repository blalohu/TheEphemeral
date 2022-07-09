package theEphemeral.previewWidget;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.CardGroup.CardGroupType;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.*;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.relics.FrozenEye;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.MonsterRoom;
import theEphemeral.EphemeralMod;
import theEphemeral.vfx.FeatherEffect;

import java.util.ArrayList;
import java.util.List;

import static theEphemeral.EphemeralMod.makeEffectPath;

public class PreviewWidget {
    public static final int MAX_AUGURY = 7;
    public static final int AUGURY_MINUS_PER_TURN = 1;
    private static final float drawScale = 0.5f;
    private static final float HEADER_WIDTH = 150.0f;
    private static final float HALF_HEADER_WIDTH = HEADER_WIDTH / 2.0f;
    private static final float HEADER_HEIGHT = 50.0f;
    private static final float HALF_HEADER_HEIGHT = HEADER_HEIGHT / 2.0f;
    private static final Texture img = ImageMaster.loadImage(makeEffectPath("header.png"));
    private static final float WIDGET_X = 150.0f;
    private static final float WIDGET_VALUE_X = WIDGET_X + HALF_HEADER_WIDTH - 20.0f;
    private static final float WIDGET_Y = 750.0f;
    private static final float CARD_Y = WIDGET_Y - 150.0f;

    private static final String POWER_ID = EphemeralMod.makeID(PreviewWidget.class.getSimpleName());
    private static final PowerStrings tipStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    private static final String TIP_NAME = tipStrings.DESCRIPTIONS[0];
    private static final String TIP_DESC_1 = tipStrings.DESCRIPTIONS[1];
    private static final String TIP_DESC_2 = tipStrings.DESCRIPTIONS[2];

    // CardGroup that is actually getting displayed
    private final CardGroup previews = new CardGroup(CardGroupType.UNSPECIFIED);

    private int augury = 0;
    private int startOfTurnMod = 0;
    private boolean forceUpdate = false;

    private final Hitbox hb;
    private final AbstractPlayer p;
    protected float fontScale;
    private float timer = 0.0F;

    // instance of widget created at start of combat and destroyed at end of combat
    private static PreviewWidget widget;
    public static void StartOfCombat() {
        widget = new PreviewWidget();
    }
    public static void EndOfCombat() {
        widget = null;
    }

    public PreviewWidget() {
        //Settings.isDebug = true;
        hb = new Hitbox(HEADER_WIDTH * Settings.scale, HEADER_HEIGHT * Settings.scale);
        p = AbstractDungeon.player;
        fontScale = 0.7F;
    }

    public static void Clear() {
        if (widget != null && widget.isActive())
            widget.clear();
    }
    public void clear() {
        augury = 0;
        previews.clear();
    }

    public static void Update() {
        if (widget != null && widget.isActive())
            widget.update();
    }
    public void update() {
        // check to see if the draw pile has changed since last update
        CardGroup drawPile = p.drawPile;

        if (forceUpdate || needUpdate()) {
            forceUpdate = false;

            // clear old previews
            previews.clear();

            // add copies to the CardGroup and initialize visuals
            if (getRevealed() > 0) {
                int revealedIndex = getRevealedIndex();
                int drawPileIndexOffset = drawPile.size() - 1;

                for (int i = revealedIndex; i >= 0; i--) {
                    AbstractCard c = drawPile.group.get(drawPileIndexOffset - i);
                    AbstractCard cpy = c.makeSameInstanceOf();

                    cpy.setAngle(0.0F, true);
                    cpy.current_x = cpy.target_x = Settings.scale * WIDGET_X;
                    cpy.current_y = cpy.target_y = Settings.scale * (CARD_Y - (i * 40));
                    cpy.targetDrawScale = drawScale;
                    cpy.lighten(true);
                    cpy.transparency = 0.0f;
                    cpy.targetTransparency = 1.0f;

                    previews.addToBottom(cpy);
                }
            }
        }

        previews.update();

        hb.resize(HEADER_WIDTH * Settings.scale, HEADER_HEIGHT * Settings.scale);
        hb.move(Settings.scale * WIDGET_X, Settings.scale * WIDGET_Y);
        hb.update();

        if (hb.hovered) {
            TipHelper.renderGenericTip(
                    Settings.scale * (WIDGET_X + HALF_HEADER_WIDTH),
                    Settings.scale * WIDGET_Y,
                    TIP_NAME, TIP_DESC_1 + augury + TIP_DESC_2);
        }
        fontScale = MathHelper.scaleLerpSnap(fontScale, 0.7F);

        if (augury > 0) {
            timer -= Gdx.graphics.getDeltaTime();
            if (0.0F > timer) {
                timer += 0.2F;
                AbstractDungeon.effectsQueue.add(new FeatherEffect(WIDGET_X, WIDGET_Y));
            }
        }
    }

    private boolean needUpdate() {
        ArrayList<AbstractCard> drawGroup = p.drawPile.group;
        ArrayList<AbstractCard> preGroup = previews.group;

        if (previews.size() != getRevealed())
            return true;

        int revealedIndex = getRevealedIndex();
        int drawPileIndexOffset = p.drawPile.size() - 1;

        for (int i = revealedIndex; i >= 0; i--) {
            if (!roughlyEqual(drawGroup.get(drawPileIndexOffset - i), preGroup.get(i)))
                return true;
        }

        return false;
    }
    private boolean roughlyEqual(AbstractCard a, AbstractCard b) {
        return a.uuid == b.uuid
                && a.name.equals(b.name)
                && a.rawDescription.equals(b.rawDescription)
                && a.costForTurn == b.costForTurn;
    }

    public static int GetAugury() {
        if (widget != null && widget.isActive())
            return widget.getAugury();
        else
            return 0;
    }
    public int getAugury() {
        return augury;
    }

    public static void SetAugury(int newValue) {
        if (widget != null && widget.isActive())
            widget.setAugury(newValue);
    }
    public void setAugury(int newValue) {
        forceUpdate = true;

        augury = newValue;

        if (augury < 0)
            augury = 0;
        if (augury > MAX_AUGURY)
            augury = MAX_AUGURY;
    }

    public static void AddAugury(int amount) {
        if (widget != null && widget.isActive())
            widget.addAugury(amount);
    }
    public void addAugury(int amount) {
        SetAugury(amount + augury);
    }

    public static void StartOfTurn() {
        if (widget != null && widget.isActive())
            widget.startOfTurn();
    }
    public void startOfTurn() {
        startOfTurnMod = 0;
    }
    public static void StartOfTurnIncrease(int amount) {
        if (widget != null && widget.isActive())
            widget.startOfTurnIncrease(amount);
    }
    public void startOfTurnIncrease(int amount) {
        startOfTurnMod += amount;
    }

    public static void StartOfTurnPostDraw() {
        if (widget != null && widget.isActive())
            widget.startOfTurnPostDraw();
    }
    public void startOfTurnPostDraw() {
        if (!AbstractDungeon.player.hasRelic(FrozenEye.ID)) {
            startOfTurnMod -= AUGURY_MINUS_PER_TURN;
        }

        AddAugury(startOfTurnMod);
    }

    public static int GetRevealed() {
        if (widget != null && widget.isActive())
            return widget.getRevealed();
        else
            return 0;
    }
    public int getRevealed() {
        return Math.min(augury, p.drawPile.size());
    }

    private int getRevealedIndex() {
        return getRevealed() - 1;
    }

    public static List<AbstractCard> GetRevealedCards() {
        if (widget != null && widget.isActive())
            return widget.getRevealedCards();
        else
            return new ArrayList<>();
    }
    public List<AbstractCard> getRevealedCards() {
        return p.drawPile.group.subList(0, getRevealedIndex());
    }

    public static int GetRevealedAttacksCount() {
        if (widget != null && widget.isActive())
            return widget.getRevealedAttacksCount();
        else
            return 0;
    }
    public int getRevealedAttacksCount() {
        return (int) previews.group.stream().filter(x -> x.type == AbstractCard.CardType.ATTACK).count();
    }

    public static void Render(SpriteBatch sb) {
        if (widget != null && widget.isActive())
            widget.render(sb);
    }
    private void render(SpriteBatch sb) {
        if (augury > 0) {
            sb.setColor(1.0f, 1.0f, 1.0f, 1.0f);

            for (AbstractCard c : previews.group) {
                c.render(sb);
            }

            sb.setColor(1.0f, 1.0f, 1.0f, 1.0f);
            drawHeader(sb);
            renderText(sb);
            hb.render(sb);
        }
    }
    private void drawHeader(SpriteBatch sb) {
        sb.draw(img,
                Settings.scale * (WIDGET_X - HALF_HEADER_WIDTH),
                Settings.scale * (WIDGET_Y - HALF_HEADER_HEIGHT),
                HEADER_WIDTH / 2.0f, HEADER_HEIGHT / 2.0f,
                HEADER_WIDTH, HEADER_HEIGHT,
                Settings.scale,
                Settings.scale,
                0, 0, 0,
                (int)HEADER_WIDTH, (int)HEADER_HEIGHT,
                false, false);
    }
    protected void renderText(SpriteBatch sb) {
        FontHelper.renderFontCentered(sb, FontHelper.cardEnergyFont_L,
                Integer.toString(augury),
                Settings.scale * (WIDGET_VALUE_X),
                Settings.scale * (WIDGET_Y),
                new Color(1.0f, 1.0f, 1.0f, 1.0f), fontScale);
    }

    private boolean isActive() {
        return (AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT || AbstractDungeon.getCurrRoom() instanceof MonsterRoom) && !p.isDead;
    }
}
