package mage.cards.b;

import mage.ObjectColor;
import mage.abilities.common.SimpleStaticAbility;
import mage.abilities.effects.common.AttachEffect;
import mage.abilities.effects.common.continuous.GainAbilityAttachedEffect;
import mage.abilities.keyword.EnchantAbility;
import mage.abilities.keyword.ProtectionAbility;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.AttachmentType;
import mage.constants.CardType;
import mage.constants.Outcome;
import mage.constants.SubType;
import mage.target.TargetPermanent;
import mage.target.common.TargetCreaturePermanent;

import java.util.UUID;

/**
 * @author LoneFox
 */
public final class BlueWard extends CardImpl {

    public BlueWard(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.ENCHANTMENT}, "{W}");
        this.subtype.add(SubType.AURA);

        // Enchant creature
        TargetPermanent auraTarget = new TargetCreaturePermanent();
        this.getSpellAbility().addTarget(auraTarget);
        this.getSpellAbility().addEffect(new AttachEffect(Outcome.Protect));
        this.addAbility(new EnchantAbility(auraTarget.getTargetName()));

        // Enchanted creature has protection from blue. This effect doesn't remove Blue Ward.
        this.addAbility(new SimpleStaticAbility(new GainAbilityAttachedEffect(
                ProtectionAbility.from(ObjectColor.BLUE), AttachmentType.AURA
        ).setDoesntRemoveItself(true)));
    }

    private BlueWard(final BlueWard card) {
        super(card);
    }

    @Override
    public BlueWard copy() {
        return new BlueWard(this);
    }
}
