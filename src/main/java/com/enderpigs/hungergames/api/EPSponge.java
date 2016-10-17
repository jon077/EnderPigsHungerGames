package com.enderpigs.hungergames.api;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.explosion.Explosion;

public class EPSponge {

    public static World world() {
        return Sponge.getServer().getWorld("world").get();
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
