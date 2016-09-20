package org.spongepowered.cookbook.plugin;

import java.util.Optional;

import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.entity.spawn.EntitySpawnCause;
import org.spongepowered.api.event.cause.entity.spawn.SpawnTypes;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;

import com.flowpowered.math.vector.Vector3i;
import com.google.inject.Inject;

@Plugin(id = "jon077",
        name = "Smite",
        version = "1.0",
        description = "Use me on your enemies")
public class Smite {

    public static class SmiteCommand implements CommandExecutor {

        private Logger logger;

        public SmiteCommand(Logger logger) {
            this.logger = logger;
        }

        @Override
        public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

            Player player = args.<Player>getOne("player").get();
            String message = args.<String>getOne("message").get();

            final String name = player.getName();
            logger.info("Executing command on player: {} with message: {}", name, message);

            src.sendMessage(Text.of("Smiting " + name + "!  " + message));

            Vector3i blockPosition = player.getLocation().getBlockPosition();
            final Optional<Entity> optionalEntity = Optional
                    .of(player.getWorld().createEntity(EntityTypes.LIGHTNING, blockPosition));
            optionalEntity.ifPresent(entity -> player.getWorld().spawnEntity(entity,
                    Cause.source(EntitySpawnCause.builder().entity(entity).type(SpawnTypes.PLUGIN).build()).build()));

            return CommandResult.success();
        }

    }

    @Inject
    private PluginContainer container;

    @Inject
    private Logger logger;

    @Listener
    public void onGamePreInitialization(GamePreInitializationEvent event) {
        logger.info("Initializing smite command!!!!");

        CommandSpec myCommandSpec = CommandSpec.builder()
            .description(Text.of("Smite Command"))
            .permission("smiteplugin.command.smite")
            .arguments(
                    GenericArguments.onlyOne(GenericArguments.player(Text.of("player"))),
                    GenericArguments.remainingJoinedStrings(Text.of("message")))
            .executor(new SmiteCommand(logger))
            .build();

    Sponge.getCommandManager().register(this, myCommandSpec, "smite");

    }

}
