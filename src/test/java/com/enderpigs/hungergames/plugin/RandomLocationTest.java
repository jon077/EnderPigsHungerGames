package com.enderpigs.hungergames.plugin;

import java.util.stream.IntStream;

import org.junit.Test;

public class RandomLocationTest {

    @Test
    public void testGetLocation() {
        RandomLocation randomLocation = new RandomLocation(4, 10, 20);
        
        IntStream.range(0, 20).forEach(x -> {
            System.out.println(randomLocation.getRandomVector());
        });
        
    }

}
