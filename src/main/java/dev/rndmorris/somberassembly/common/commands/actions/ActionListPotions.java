package dev.rndmorris.somberassembly.common.commands.actions;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import net.minecraft.command.ICommandSender;
import net.minecraft.potion.Potion;

import dev.rndmorris.somberassembly.common.commands.CommandActionBase;
import dev.rndmorris.somberassembly.common.commands.SomberAssemblyCommand;
import dev.rndmorris.somberassembly.utils.CollectionUtil;

public class ActionListPotions extends CommandActionBase<SomberAssemblyCommand> {

    public ActionListPotions(SomberAssemblyCommand owningCommand) {
        super(owningCommand, "listPotions");
    }

    @Override
    public String getDescription() {
        return "List all potion effects and their ids";
    }

    @Override
    public String getArgumentsExample() {
        return "[<searchText>]";
    }

    @Override
    public List<String> getArgumentDescriptions() {
        return CollectionUtil.listOf("<searchText>: Optional. Only return potions containing this text.");
    }

    @Override
    public int getMinimumArguments() {
        return 0;
    }

    @Override
    public boolean execute(ICommandSender sender, List<String> args) {
        if (!super.execute(sender, args)) {
            return false;
        }
        final var searchText = collectSearchText(args, 0);

        var potions = Arrays.stream(Potion.potionTypes)
            .filter(Objects::nonNull);

        if (searchText != null && !searchText.isEmpty()) {
            final var search = searchText.toUpperCase();
            potions = potions.filter(
                p -> p.getName()
                    .toUpperCase()
                    .contains(search));
        }

        final var potionList = potions.map(p -> String.format("%1s (%2d)", p.getName(), p.id));

        sendList(sender, potionList);

        return true;
    }
}
