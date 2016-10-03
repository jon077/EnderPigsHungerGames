package com.enderpigs.hungergames.plugin;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
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
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;
import org.spongepowered.api.event.entity.DestructEntityEvent.Death;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;

import com.google.inject.Inject;

@Plugin(id = "com_enderpigs_game",
        name = "Game",
        version = "1.0",
        description = "Main plugin for the game")
public class Game {
    
    private Scoreboard scoreboard;

    public interface PostMessage{
        void post(String message);
    }
    
    public static class GameCommand implements CommandExecutor {

        private final Logger logger;
        private final PluginContainer container;
        private final PostMessage postMessage;
        private Scoreboard scoreboard;

        public GameCommand(Scoreboard scoreboard, PluginContainer container, Logger logger) {
            this.scoreboard = scoreboard;
            this.container = container;
            this.logger = logger;

            postMessage = new EnderpigsPostMessage(logger);
        }

        @Override
        public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

            final String command = args.<String>getOne("command").get();
            
            if("start".equals(command)){
                logger.info("Starting game");
                
                postMessage.post(
                        "The EnderPigs Hunger Games is starting.  "
                        + "It's a hardcore game you can win.  Battle zombies and your friends today! "
                        + " Use the address: hungergames.enderpigs.com");
            }else if("leaderboard".equals(command)){
                
                
                final String border = "===========================================";
                src.sendMessage(Text.of(border));
                
                Map<String, Integer> scores = scoreboard.getScores();
                scores.keySet().stream().forEach(name -> {
                    final String rightPadName = StringUtils.rightPad(name, 10);
                    final String leftPadScore = StringUtils.leftPad(
                            String.valueOf(scores.get(name)), 4);
                    
                    src.sendMessage(Text.of(rightPadName + " " + leftPadScore));
                });
                src.sendMessage(Text.of(border));
            }
            
            return CommandResult.success();
        }
    }

    public static class Scoreboard{
        
        private final Map<String, Integer> scores = new HashMap<>();

        public void addKill(EntityType type, String playerName) {
            
            Integer currentScore = scores.get(playerName);
            if(currentScore == null){
                currentScore = 0;
            }
            
            int killScore = 0;
            if(EntityTypes.ZOMBIE.equals(type)){
                killScore = 1;
            }else if(EntityTypes.SKELETON.equals(type)){
                killScore = 2;
            }else if(EntityTypes.HUMAN.equals(type)){
                killScore = 10;
            }else if(EntityTypes.PIG.equals(type)){
                killScore = -1;
            }else if(EntityTypes.PIG_ZOMBIE.equals(type)){
                killScore = 1;
            }else if(EntityTypes.SPIDER.equals(type)){
                killScore = 1;
            }
            
            scores.put(playerName, currentScore + killScore);
        }
        
        public Map<String, Integer> getScores(){
            
            return scores.entrySet().stream()
                    .sorted(Map.Entry.comparingByValue(Collections.reverseOrder()))
                    .collect(Collectors.toMap(Map.Entry::getKey, 
                            Map.Entry::getValue, 
                            (e1, e2) -> e1, 
                            LinkedHashMap::new));
        }
    }
    
    public Game() {
        scoreboard = new Scoreboard();
    }
    @Inject
    private PluginContainer container;

    @Inject
    private Logger logger;

    @Listener
    public void onGamePreInitialization(GamePreInitializationEvent event) {
        logger.info("Initializing game command!!!!");

        Map<String, String> arg1 = new HashMap<String, String> (){
            private static final long serialVersionUID = -2579299545550150417L;
            {
            put("start","start");
            put("leaderboard","leaderboard");
            put("stop","stop");
            }};

        CommandSpec myCommandSpec = CommandSpec.builder()
            .description(Text.of("Game Command"))
            .permission("gameplugin.command.game")
            .arguments(
                    GenericArguments.onlyOne(GenericArguments.choices(Text.of("command"), arg1)))
            .executor(new GameCommand(scoreboard, container, logger))
            .build();

    Sponge.getCommandManager().register(this, myCommandSpec, "game");

    }
    
    @Listener
    public void onEntityDeath(Death death){
        
        final Cause cause = death.getCause();
        logger.info("Zombie died.  Cause: {}", cause);
        
        Optional<EntityDamageSource> entityDamageSource = cause.first(EntityDamageSource.class);
        if(entityDamageSource.isPresent()){
            Entity entity = entityDamageSource.get().getSource();
            
            if(EntityTypes.PLAYER.equals(entity.getType())){
                Player player = (Player) entity;
                logger.info("Player {} killed a zombie", player.getName());
                
                scoreboard.addKill(death.getTargetEntity().getType(), player.getName());
            }
        }
    }
}


