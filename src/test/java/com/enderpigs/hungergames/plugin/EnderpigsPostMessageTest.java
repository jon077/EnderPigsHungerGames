package com.enderpigs.hungergames.plugin;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enderpigs.hungergames.plugin.EnderpigsPostMessage;
import com.enderpigs.hungergames.plugin.Game.PostMessage;

public class EnderpigsPostMessageTest {

    final private static Logger logger = LoggerFactory.getLogger(EnderpigsPostMessageTest.class);
    
    @Test
    public void testPost() {
        PostMessage postMessage = new EnderpigsPostMessage(logger);
        postMessage.post("The EnderPigs Hunger Games is starting.  "
                        + "It's a hardcore game you can win.  Battle zombies and your friends today! "
                        + " Use the address: hungergames.enderpigs.com");
        
        
    }
}
