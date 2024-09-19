package dev.rndmorris.somberassembly.common.configs;

import net.minecraft.world.GameRules;

import java.util.Objects;

public class SomberGameRules {

    // Put string constants of game rule names here, so they can be looked up elsewhere


    public static void onWorldLoad(GameRules gameRules) {
        // initialize custom game rules here
        final var customGameRules = new IGameRuleDeclaration[] {

        };

        for (var newRule : customGameRules) {
            final var ruleName = newRule.getRuleName();
            if (!gameRules.hasRule(ruleName)) {
                gameRules.setOrCreateGameRule(ruleName, newRule.getDefaultValue());
            }
        }
    }

    public interface IGameRuleDeclaration {

        public String getRuleName();

        public String getDefaultValue();
    }

    private static abstract class GameRuleDeclaration implements IGameRuleDeclaration {

        protected final String ruleName;

        public GameRuleDeclaration(String ruleName) {
            this.ruleName = Objects.requireNonNull(ruleName);
        }

        @Override
        public String getRuleName() {
            return ruleName;
        }
    }

    public static class BooleanGameRule extends GameRuleDeclaration {

        private final boolean defaultValue;

        public BooleanGameRule(String gameRuleName, boolean defaultValue) {
            super(gameRuleName);
            this.defaultValue = defaultValue;
        }

        @Override
        public String getDefaultValue() {
            return String.valueOf(defaultValue);
        }
    }

    public static class DoubleGameRule extends GameRuleDeclaration {

        private final double defaultValue;

        public DoubleGameRule(String gameRuleName, double defaultValue) {
            super(gameRuleName);
            this.defaultValue = defaultValue;
        }

        @Override
        public String getDefaultValue() {
            return String.valueOf(defaultValue);
        }
    }

    public static class IntegerGameRule extends GameRuleDeclaration {

        private final int defaultValue;

        public IntegerGameRule(String gameRuleName, int defaultValue) {
            super(gameRuleName);
            this.defaultValue = defaultValue;
        }

        @Override
        public String getDefaultValue() {
            return String.valueOf(defaultValue);
        }
    }
}
