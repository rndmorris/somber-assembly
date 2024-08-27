package dev.rndmorris.somberassembly.potions;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import net.minecraft.potion.Potion;

import dev.rndmorris.somberassembly.SomberAssembly;
import dev.rndmorris.somberassembly.lib.ArrayUtil;

public class SomberPotions {

    private static final int customPotionCount = 1;
    public static PotionDeathStench deathMask;

    public static void init() {
        final var availableIds = findEmptyPotionIds();

        final var potionList = new ArrayList<>(Arrays.asList(Potion.potionTypes));

        // expand the potion list to make more room, if needed
        // I'm sure there's a more efficient way to do this, but this should work for now
        while (availableIds.size() < customPotionCount) {
            availableIds.add(potionList.size());
            potionList.add(null);
        }

        var potionIdIndex = 0;
        var potionId = availableIds.get(potionIdIndex++);
        deathMask = new PotionDeathStench(potionId);
        potionList.set(potionId, deathMask);

        updatePotionArray(potionList);
    }

    private static List<Integer> findEmptyPotionIds() {
        final var lastVanillaId = 23;
        final var potionTypes = Potion.potionTypes;
        final var foundIds = new ArrayList<Integer>();
        for (var index = lastVanillaId; index < potionTypes.length; ++index) {
            if (potionTypes[index] == null) {
                foundIds.add(index);
            }
        }
        return foundIds;
    }

    private static void updatePotionArray(List<Potion> withList) {
        final var newPotionTypes = ArrayUtil.fillFromList(new Potion[withList.size()], withList);
        try {
            final var potionTypesField = tryGetPotionTypesField();
            Objects.requireNonNull(potionTypesField)
                .set(null, newPotionTypes);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            SomberAssembly.LOG
                .error("An error ocurred updating potions. Please share the server log with the mod author.");
            throw new RuntimeException(e);
        }
    }

    private static Field tryGetPotionTypesField() throws NoSuchFieldException, IllegalAccessException {
        for (var field : Potion.class.getDeclaredFields()) {
            final var name = field.getName();
            if ("potionTypes".equals(name) || "field_76425_a".equals(name)) {
                var modfield = Field.class.getDeclaredField("modifiers");
                modfield.setAccessible(true);
                modfield.setInt(field, field.getModifiers() & ~Modifier.FINAL);
                return field;
            }
        }
        return null;
    }
}
