package com.enderpigs.hungergames.plugin;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

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
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.entity.spawn.EntitySpawnCause;
import org.spongepowered.api.event.cause.entity.spawn.SpawnTypes;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.scheduler.SpongeExecutorService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.google.inject.Inject;

@Plugin(id = "com_enderpigs_storm",
        name = "Storm",
        version = "1.0",
        description = "Plugin for spawning storms of items")
public class Storm {
    
    public static class StormCommand implements CommandExecutor {
        
        private static final int STORM_SIZE = 10;
        private static final int DROP_NUMBER = 40;
        
        private final Random random = new Random();
        private final SpongeExecutorService executorService;
        
        private final Logger logger;
        private final PluginContainer container;

        public StormCommand(PluginContainer container, Logger logger) {
            this.container = container;
            this.logger = logger;
            this.executorService = Sponge.getScheduler().createSyncExecutor(container);
        }

        @Override
        public CommandResult execute(final CommandSource src, final CommandContext args) throws CommandException {

            final String command = args.<String>getOne("command").get();
            
            Optional<EntityType> entityType = ENTITY_TYPES.stream()
                .filter(e -> command.equals(e.getName()))
                .findFirst();
            
            if(entityType.isPresent()){
                final World world = Sponge.getServer().getWorld("world").get();
                world.getPlayers().stream().forEach(player -> rainEntities(entityType.get(), player));
            }
            
            
            return CommandResult.success();
        }

        private void rainEntities(final EntityType entityType, final Player player) {
            
                IntStream.range(0, DROP_NUMBER)
                .forEach(x -> {
                    
                    Sponge.getScheduler().createTaskBuilder()
                    .execute(() -> {
                        int randomX = random.nextInt(STORM_SIZE * 2) - STORM_SIZE;
                        int randomZ = random.nextInt(STORM_SIZE * 2) - STORM_SIZE;
                        int y = (EntityTypes.LIGHTNING.equals(entityType)) ? 0 : 5;
                        
                        Location<World> location = player.getLocation().add(randomX, y, randomZ);
                        
                        final Optional<Entity> optionalEntity = Optional
                                .of(player.getWorld().createEntity(entityType, 
                                        location.getBlockPosition()));
                        optionalEntity.ifPresent(entity -> player.getWorld().spawnEntity(entity,
                                Cause.source(EntitySpawnCause.builder().entity(entity).type(SpawnTypes.PLUGIN).build()).build()));
                    })
                    .delayTicks(x*10)
                    .submit(container);
            });
        }
    }
    
    private static final Collection<EntityType> ENTITY_TYPES = Arrays.asList(
            EntityTypes.PIG,
            EntityTypes.LIGHTNING,
            EntityTypes.BLAZE,
            EntityTypes.SMALL_FIREBALL
            );

    @Inject
    private PluginContainer container;

    @Inject
    private Logger logger;

    @Listener
    public void onGamePreInitialization(GamePreInitializationEvent event) {
        logger.info("Initializing storm command!!!!");

        Map<String, String> arg1 = new HashMap<String, String> (){
            private static final long serialVersionUID = -2579299545550150417L;
            {
                ENTITY_TYPES.stream()
                .forEach(shape ->{
                    final String lowerName =  shape.getName();
                    put(lowerName,lowerName);
                });
            }};

        CommandSpec myCommandSpec = CommandSpec.builder()
            .description(Text.of("Storm Command"))
            .permission("stormplugin.command.storm")
            .arguments(
                    GenericArguments.onlyOne(GenericArguments.choices(Text.of("command"), arg1)))
            .executor(new StormCommand(container, logger))
            .build();

        Sponge.getCommandManager().register(this, myCommandSpec, "storm");

    }
}


