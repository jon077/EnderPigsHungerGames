package com.enderpigs.hungergames.plugin;

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
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.explosion.Explosion;

import com.flowpowered.math.vector.Vector3d;
import com.google.inject.Inject;



/******
 *  Command: /explode X,Y,Z
 * 
 * @author pigboy
 *
 */
@Plugin(id = "com_enderpigs_explode",
        name = "Explode",
        version = "1.0",
        description = "Plugin for exploding")
public class Explode {
    
    public static class ExplodeCommand implements CommandExecutor {

        private final Logger logger;
        private final PluginContainer container;

        public ExplodeCommand(PluginContainer container, Logger logger) {
            this.container = container;
            this.logger = logger;
        }

        @Override
        public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        	final Player player = args.<Player>getOne("player").get();
            final World world = Sponge.getServer().getWorld("world").get();
           
            Location<World> location = player.getLocation().add(new Vector3d(7, 0, 0));
            
            tnt(location, 3, Cause.source(container).build());
           
            return CommandResult.success();
        }
        
    	public static void tnt(Location<World> location, float radius, Cause cause) {
    		location.getExtent().triggerExplosion(Explosion.builder()
    				.location(location)
    				.shouldDamageEntities(true)
    				.shouldPlaySmoke(true)
    				.shouldBreakBlocks(true)
    				.radius(radius)
    				.build(), cause);
    	}

    }

    @Inject
    private PluginContainer container;

    @Inject
    private Logger logger;

    @Listener
    public void onGamePreInitialization(GamePreInitializationEvent event) {
        logger.info("Initializing explode command!!!!");


        CommandSpec myCommandSpec = CommandSpec.builder()
            .description(Text.of("Explode Command"))
            .permission("explodeplugin.command.explode")
            .arguments(
            		GenericArguments.onlyOne(GenericArguments.player(Text.of("player"))))
            .executor(new ExplodeCommand(container, logger))
            .build();

        Sponge.getCommandManager().register(this, myCommandSpec, "explode");

    }
}


