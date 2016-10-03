package org.spongepowered.cookbook.plugin;

import org.hamcrest.core.IsEqual;
import org.junit.Assert;
import org.junit.Test;
import org.spongepowered.api.entity.EntityTypes;

public class GameTest extends Game {

    @Test
    public void testZombie() {
        
        Scoreboard scoreboard = new Game.Scoreboard();
        scoreboard.addKill(EntityTypes.ZOMBIE, "player1");
        
        Assert.assertThat(
                scoreboard.getScores().get("player1"),
                IsEqual.equalTo(1));
    }
    
    @Test
    public void testSkeleton() {
        
        Scoreboard scoreboard = new Game.Scoreboard();
        scoreboard.addKill(EntityTypes.SKELETON, "player1");
        
        Assert.assertThat(
                scoreboard.getScores().get("player1"),
                IsEqual.equalTo(2));
    }
    
    @Test
    public void testHuman() {
        
        Scoreboard scoreboard = new Game.Scoreboard();
        scoreboard.addKill(EntityTypes.HUMAN, "player1");
        
        Assert.assertThat(
                scoreboard.getScores().get("player1"),
                IsEqual.equalTo(10));
    }

    @Test
    public void testPig() {
        
        Scoreboard scoreboard = new Game.Scoreboard();
        scoreboard.addKill(EntityTypes.PIG, "player1");
        
        Assert.assertThat(
                scoreboard.getScores().get("player1"),
                IsEqual.equalTo(-1));
    }
    
    @Test
    public void testSpider() {
        
        Scoreboard scoreboard = new Game.Scoreboard();
        scoreboard.addKill(EntityTypes.SPIDER, "player1");
        
        Assert.assertThat(
                scoreboard.getScores().get("player1"),
                IsEqual.equalTo(1));
    }
    
    @Test
    public void testSorting() {
        
        Scoreboard scoreboard = new Game.Scoreboard();
        scoreboard.addKill(EntityTypes.SPIDER, "player1");
        scoreboard.addKill(EntityTypes.HUMAN, "player2");
        
        Assert.assertThat(
                scoreboard.getScores().keySet().iterator().next(),
                IsEqual.equalTo("player2"));
        
        scoreboard.addKill(EntityTypes.HUMAN, "player1");
        Assert.assertThat(
                scoreboard.getScores().keySet().iterator().next(),
                IsEqual.equalTo("player1"));
    }
}
