/*
 *  Copyright 2010 BetaSteward_at_googlemail.com. All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without modification, are
 *  permitted provided that the following conditions are met:
 *
 *     1. Redistributions of source code must retain the above copyright notice, this list of
 *        conditions and the following disclaimer.
 *
 *     2. Redistributions in binary form must reproduce the above copyright notice, this list
 *        of conditions and the following disclaimer in the documentation and/or other materials
 *        provided with the distribution.
 *
 *  THIS SOFTWARE IS PROVIDED BY BetaSteward_at_googlemail.com ``AS IS'' AND ANY EXPRESS OR IMPLIED
 *  WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 *  FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL BetaSteward_at_googlemail.com OR
 *  CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 *  CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 *  SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 *  ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *  The views and conclusions contained in the software and documentation are those of the
 *  authors and should not be interpreted as representing official policies, either expressed
 *  or implied, of BetaSteward_at_googlemail.com.
 */
package mage.cards.c;

import java.util.LinkedHashSet;
import java.util.UUID;
import java.util.stream.Collectors;
import mage.MageInt;
import mage.MageObject;
import mage.abilities.Ability;
import mage.abilities.common.AsEntersBattlefieldAbility;
import mage.abilities.common.SimpleActivatedAbility;
import mage.abilities.common.SkipUntapOptionalAbility;
import mage.abilities.condition.common.SourceTappedCondition;
import mage.abilities.costs.common.TapSourceCost;
import mage.abilities.decorator.ConditionalContinuousEffect;
import mage.abilities.effects.OneShotEffect;
import mage.abilities.effects.common.continuous.GainControlTargetEffect;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.choices.Choice;
import mage.choices.ChoiceImpl;
import mage.constants.CardType;
import mage.constants.SubType;
import mage.constants.Duration;
import mage.constants.Outcome;
import mage.constants.Zone;
import mage.filter.common.FilterCreaturePermanent;
import mage.filter.predicate.Predicates;
import mage.filter.predicate.mageobject.ChosenSubtypePredicate;
import mage.game.Game;
import mage.game.permanent.Permanent;
import mage.players.Player;
import mage.target.common.TargetCreaturePermanent;
import mage.target.common.TargetOpponent;
import mage.util.CardUtil;

/**
 *
 * @author L_J
 */
public class CallousOppressor extends CardImpl {

    public CallousOppressor(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId,setInfo,new CardType[]{CardType.CREATURE},"{1}{U}{U}");
        this.subtype.add(SubType.CEPHALID);
        this.power = new MageInt(1);
        this.toughness = new MageInt(2);

        // You may choose not to untap Callous Oppressor during your untap step.
        this.addAbility(new SkipUntapOptionalAbility());

        // As Callous Oppressor enters the battlefield, an opponent chooses a creature type.
        this.addAbility(new AsEntersBattlefieldAbility(new OpponentChooseCreatureTypeEffect()));

        // {T}: Gain control of target creature that isn't of the chosen type for as long as Callous Oppressor remains tapped.
        FilterCreaturePermanent filter = new FilterCreaturePermanent("creature that isn't of the chosen type");
        filter.add(Predicates.not(new ChosenSubtypePredicate(this.getId())));
        ConditionalContinuousEffect effect = new ConditionalContinuousEffect(
                new GainControlTargetEffect(Duration.OneUse),
                SourceTappedCondition.instance,
                "Gain control of target creature for as long as Callous Oppressor remains tapped");
        Ability ability = new SimpleActivatedAbility(Zone.BATTLEFIELD, effect, new TapSourceCost());
        ability.addTarget(new TargetCreaturePermanent(filter));
        this.addAbility(ability);
    }

    public CallousOppressor(final CallousOppressor card) {
        super(card);
    }

    @Override
    public CallousOppressor copy() {
        return new CallousOppressor(this);
    }
}

class OpponentChooseCreatureTypeEffect extends OneShotEffect {

    public OpponentChooseCreatureTypeEffect() {
        super(Outcome.Benefit);
        staticText = "an opponent chooses a creature type";
    }

    public OpponentChooseCreatureTypeEffect(final OpponentChooseCreatureTypeEffect effect) {
        super(effect);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Player controller = game.getPlayer(source.getControllerId());
        MageObject mageObject = game.getPermanentEntering(source.getSourceId());
        if (mageObject == null) {
            mageObject = game.getObject(source.getSourceId());
        }
        if (controller != null) {
            TargetOpponent target = new TargetOpponent(true);
            if (target.canChoose(source.getSourceId(), controller.getId(), game)) {
                while (!target.isChosen() && target.canChoose(controller.getId(), game) && controller.canRespond()) {
                    controller.chooseTarget(outcome, target, source, game);
                }
            } else {
                return false;
            }
            Player opponent = game.getPlayer(target.getFirstTarget());
            if (opponent != null && mageObject != null) {
                Choice typeChoice = new ChoiceImpl(true);
                typeChoice.setMessage("Choose creature type");
                typeChoice.setChoices(SubType.getCreatureTypes(false).stream().map(SubType::toString).collect(Collectors.toCollection(LinkedHashSet::new)));
                while (!opponent.choose(outcome, typeChoice, game)) {
                    if (!opponent.canRespond()) {
                        return false;
                    }
                }
                if (typeChoice.getChoice() == null) {
                    return false;
                }
                if (!game.isSimulation()) {
                    game.informPlayers(mageObject.getName() + ": " + opponent.getLogName() + " has chosen " + typeChoice.getChoice());
                }
                game.getState().setValue(mageObject.getId() + "_type", SubType.byDescription(typeChoice.getChoice()));
                if (mageObject instanceof Permanent) {
                    ((Permanent) mageObject).addInfo("chosen type", CardUtil.addToolTipMarkTags("Chosen type: " + typeChoice.getChoice()), game);
                }
            }
        }
        return false;
    }

    @Override
    public OpponentChooseCreatureTypeEffect copy() {
        return new OpponentChooseCreatureTypeEffect(this);
    }

}
