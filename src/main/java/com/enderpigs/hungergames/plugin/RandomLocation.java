package com.enderpigs.hungergames.plugin;

import java.util.Random;

import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.flowpowered.math.vector.Vector3d;

public class RandomLocation{
    
    private int min;
    private int max;
    private int count = 0;
    private int iterations;
    private Random random;

    public RandomLocation(int min, int max, int iterations) {
        this.min = min;
        this.max = max;
        this.iterations = iterations;
        random = new Random();
    }
    
    protected Vector3d getRandomVector(){   
        System.out.println(" - count: " + (iterations - count));
        
        double countDelta = ((double)iterations - (double)count) / (double)iterations;
        System.out.println("  - countDelta: " + countDelta);
        
        int value = Math.round( (int)(countDelta * (double)(max - min)) ) + 1;
        System.out.println("  - value: " + value );
        
        count = count + 1;
        
        
        int randomX = random.nextInt(value) + min;
        int randomZ = random.nextInt(value) + min;
        
        randomX = random.nextInt(2) > 0 ? randomX * -1 : randomX;
        randomZ = random.nextInt(2) > 0 ? randomZ * -1 : randomZ;
        
        
        return new Vector3d((double)randomX, 0, (double)randomZ);
    }

    public Location<World> getLocation(Location<World> location) {
        return location.add(getRandomVector());
    }
    
    
}