package org.spongepowered.cookbook.plugin;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.entity.spawn.EntitySpawnCause;
import org.spongepowered.api.event.cause.entity.spawn.SpawnTypes;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.flowpowered.math.vector.Vector3d;
import com.google.common.base.Enums;
import com.google.common.base.Optional;
import com.google.inject.Inject;

@Plugin(id = "com_enderpigs_zombies",
        name = "Zombies",
        version = "1.0",
        description = "Plugin for spawning zombies")
public class Zombies {
    
    public enum ZombieShape{
        PLUS(new Vector3d(4, 0, 0),
                new Vector3d(-4, 0, 0),
                new Vector3d(0, 0, 4),
                new Vector3d(0, 0, -4)
                ),
        
        LINE(   new Vector3d(3, 0, 0),
                new Vector3d(4, 0, 0),
                new Vector3d(5, 0, 0),
                new Vector3d(6, 0, 0),
                new Vector3d(7, 0, 0),
                new Vector3d(8, 0, 0),
                new Vector3d(9, 0, 0),
                new Vector3d(10, 0, 0)
                ),
        
        SURROUND(new Vector3d(4, 0, 0),
                new Vector3d(4, 0, -1),
                new Vector3d(4, 0, -2),
                new Vector3d(4, 0, 1),
                new Vector3d(4, 0, 2),
                
                new Vector3d(-4, 0, 0),
                new Vector3d(-4, 0, -1),
                new Vector3d(-4, 0, -2),
                new Vector3d(-4, 0, 1),
                new Vector3d(-4, 0, 2),
                
                new Vector3d(0, 0, 4),
                new Vector3d(-1, 0, 4),
                new Vector3d(-2, 0, 4),
                new Vector3d(1, 0, 4),
                new Vector3d(2, 0, 4),
                
                new Vector3d(0, 0, -4),
                new Vector3d(-1, 0, -4),
                new Vector3d(-2, 0, -4),
                new Vector3d(1, 0, -4),
                new Vector3d(2, 0, -4)
                );
        
        private List<Vector3d> shape;

        private ZombieShape(Vector3d... vector3ds) {
            shape = Arrays.asList(vector3ds);
        }
        
        public List<Vector3d> getShape() {
            return shape;
        }
    }
    
    public static class ZombieCommand implements CommandExecutor {

        private final Logger logger;
        private final PluginContainer container;

        public ZombieCommand(PluginContainer container, Logger logger) {
            this.container = container;
            this.logger = logger;
        }

        @Override
        public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

            final String command = args.<String>getOne("command").get();
            final World world = Sponge.getServer().getWorld("world").get();
            
            Optional<ZombieShape> zombieShapeOpt = Enums.getIfPresent(ZombieShape.class, command.toUpperCase());
            
            if(zombieShapeOpt.isPresent()){
                logger.info("Spawning zombies in shape: "+ zombieShapeOpt.get().name());
                
                world.getPlayers().stream().forEach(player -> {
                    Location<World> playerLocation = player.getLocation();
                    
                    zombieShapeOpt.get().getShape().stream().map(vector -> {
                        return playerLocation.add(vector);
                    }).forEach(location -> spawnZombie(location));
                });
            }
            
            return CommandResult.success();
        }

        private void spawnZombie(Location<World> location) {
            
            int count=0;
            if(count < 10 && !BlockTypes.AIR.equals(location.getBlockType())){
                location = location.add(0, 2, 0);
                count++;
            }
            
            World extent = location.getExtent();
            Entity entity = extent.createEntity(EntityTypes.ZOMBIE,
                    location.getPosition());
            extent.spawnEntity(entity, Cause.source(EntitySpawnCause.builder()
                    .entity(entity).type(SpawnTypes.PLUGIN).build()).build());
        }
    }

    @Inject
    private PluginContainer container;

    @Inject
    private Logger logger;

    @Listener
    public void onGamePreInitialization(GamePreInitializationEvent event) {
        logger.info("Initializing zombies command!!!!");

        Map<String, String> arg1 = new HashMap<String, String> (){
            private static final long serialVersionUID = -2579299545550150417L;
            {
                Arrays.asList(ZombieShape.values()).stream()
                .forEach(shape ->{
                    final String lowerName = shape.name().toLowerCase();
                    put(lowerName,lowerName);
                });
            }};

        CommandSpec myCommandSpec = CommandSpec.builder()
            .description(Text.of("Zombies Command"))
            .permission("zombiesplugin.command.zombies")
            .arguments(
                    GenericArguments.onlyOne(GenericArguments.choices(Text.of("command"), arg1)))
            .executor(new ZombieCommand(container, logger))
            .build();

    Sponge.getCommandManager().register(this, myCommandSpec, "zombies");

    }
}


