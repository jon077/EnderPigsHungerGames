package com.enderpigs.hungergames.plugin;

import java.util.Arrays;
import java.util.Optional;

import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.ConstructEntityEvent;
import org.spongepowered.api.event.entity.DestructEntityEvent.Death;
import org.spongepowered.api.event.network.ClientConnectionEvent.Login;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.World;

import com.google.inject.Inject;

@Plugin(id = "com_enderpigs_playerlifecycle",
        name = "PlayerLifecycle",
        version = "1.0",
        description = "Messages for players during the game")
public class PlayerLifecycle {

    public class Counter{
        int count;
        
        public Counter(int count) {
           this.count = count;
        }
        
        public int getCount() {
            return count;
        }
        
        public void add(int plus){
            count = count + plus;
        }
    }
    
    @Inject
    private PluginContainer container;

    @Inject
    private Logger logger;


    @Listener
    public void onPlayerJoin(Login login){

        Optional<String> name = login.getProfile().getName();
        if(name.isPresent()){
            final World world = EPSponge.world();
            String worldMessage = name.get() + " has joined the games.  May the odds be ever in your favor.";
            
            logger.info(worldMessage);
            
            Sponge.getScheduler().createTaskBuilder()
            .execute(() -> {
                Sponge.getServer()
                    .getBroadcastChannel()
                    .send(Text.of(worldMessage));
            })
            .delayTicks(60)
            .submit(container);

            
            
            Sponge.getScheduler().createTaskBuilder()
            .execute(() -> {
                
                Optional<Player> playerOptional = world.getPlayers().stream()
                        .filter(p -> name.get().equals(p.getName()))
                        .findFirst();
                
                if(playerOptional.isPresent()){
                    final Player player = playerOptional.get();
                    player.sendMessage(Text.of("This is a hardcore world."));
                    player.sendMessage(Text.of("If you die, you are out."));
                    player.sendMessage(Text.of("Go to enderpigs.com to see the score at the end of the games."));
                }
            })
            .delayTicks(120)
            .submit(container);
        }
    }
    
    
    @Listener
    public void onPlayerDead(Death death){
        
        
        
        final Living targetEntity = death.getTargetEntity();
        final EntityType type = targetEntity.getType();
        
        if(EntityTypes.PLAYER.equals(type)){
            
            final Player player = (Player) targetEntity;
            final String name = player.getName();
            
            logger.info("Death on the field: "+ name);
            
            final Counter counter = new Counter(300);
            Arrays.asList(
                    "      ** BOOM **",
                    "   **** BOOM ****",
                    "****** BOOM ******", 
                    "   " + name + " has fallen. RIP."
                    ).stream()
            .forEach(msg -> {
                Sponge.getScheduler().createTaskBuilder()
                .execute(() -> {
                    Sponge.getServer()
                    .getBroadcastChannel()
                    .send(Text.of(msg));
                })
                .delayTicks(counter.getCount())
                .submit(container);
                
                counter.add(60);
            });
        }
    }
}
