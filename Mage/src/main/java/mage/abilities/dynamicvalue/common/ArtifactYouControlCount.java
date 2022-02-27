package mage.abilities.dynamicvalue.common;

import mage.abilities.Ability;
import mage.abilities.dynamicvalue.DynamicValue;
import mage.abilities.effects.Effect;
import mage.filter.StaticFilters;
import mage.game.Game;

/**
 * @author JayDi85
 */
public enum ArtifactYouControlCount implements DynamicValue {

    instance;

    @Override
    public int calculate(Game game, Ability sourceAbility, Effect effect) {
        return game.getBattlefield().count(StaticFilters.FILTER_CONTROLLED_PERMANENT_ARTIFACT, sourceAbility.getSourceId(), sourceAbility.getControllerId(), sourceAbility, game);
    }

    @Override
    public ArtifactYouControlCount copy() {
        return instance;
    }

    @Override
    public String toString() {
        return "1"; // uses "for each" effects, so must be 1, not X
    }

    @Override
    public String getMessage() {
        return "artifact you control";
    }
}
