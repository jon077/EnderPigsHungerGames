package com.enderpigs.hungergames.plugin;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.world.World;

public class EPSponge {

    public static World world() {
        return Sponge.getServer().getWorld("world").get();
    }

}
