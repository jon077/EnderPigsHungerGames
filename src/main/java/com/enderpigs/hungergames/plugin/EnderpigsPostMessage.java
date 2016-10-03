package com.enderpigs.hungergames.plugin;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Base64;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;

import com.enderpigs.hungergames.plugin.Game.PostMessage;


public class EnderpigsPostMessage implements PostMessage{
    
    private final Logger logger;

    public EnderpigsPostMessage(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void post(String message) {
        logger.info("Sending message:  " + message);
        
        String authHeader = "Basic " + Base64.getEncoder().encodeToString(("YYY" + ":" + "XXXX").getBytes());
        
        logger.info(authHeader);
        
        try {
            URI url = new URI("http://enderpigs.com/wp-json/wp/v2/posts");
            
            Client client = ClientBuilder.newClient();
            Response response = client.target(url)
            .request(MediaType.APPLICATION_FORM_URLENCODED)
            .header("Authorization", authHeader)
            .post(Entity.form(new Form().param("title", "Hello Gaia")));
            
            int status = response.getStatus();
            logger.info("Posted message.  Response is: " + status);
            
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
      
        
                
    }
}