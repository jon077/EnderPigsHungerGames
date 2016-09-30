package org.spongepowered.cookbook.plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.block.tileentity.carrier.Chest;
import org.spongepowered.api.block.tileentity.carrier.TileEntityCarrier;
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
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.type.TileEntityInventory;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.google.common.base.Enums;
import com.google.common.base.Optional;
import com.google.inject.Inject;

@Plugin(id = "com_enderpigs_sponsor",
        name = "Sponsor",
        version = "1.0",
        description = "Give to your friends or enemies")
public class Sponsor {

    public enum SponsorGift{
        FOOD(
            ItemStack.of(ItemTypes.COOKED_RABBIT, 1),
            ItemStack.of(ItemTypes.BREAD, 1),
            ItemStack.of(ItemTypes.BAKED_POTATO, 3),
            ItemStack.of(ItemTypes.CARROT, 2),
            ItemStack.of(ItemTypes.COOKIE, 1)),
        ARMOR,
        WEAPONS;
        
        private Collection<ItemStack> itemStacks;

        private SponsorGift(ItemStack...itemStacks) {
            this.itemStacks = Arrays.asList(itemStacks);
        }
        
        public Collection<ItemStack> getItemStacks() {
            return itemStacks;
        }
    }
    
    public static class SponsorCommand implements CommandExecutor {

        private Logger logger;
        private PluginContainer container;

        public SponsorCommand(PluginContainer container, Logger logger) {
            this.container = container;
            this.logger = logger;
        }

        @Override
        public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

            final Player player = args.<Player>getOne("player").get();
            final String giftText = args.<String>getOne("gift").get();
            final String name = player.getName();
            
            Optional<SponsorGift> sponsorGift = Enums.getIfPresent(SponsorGift.class, giftText.toUpperCase());
            logger.info("Executing sponsor command on player: {} with gift: {}", name, sponsorGift);
            
            if(sponsorGift.isPresent()){
    
                player.sendMessage(Text.of("You are being sponsored.  Look in the chest at your feet."));
    
                final Location<World> playerLocation = player.getLocation();
                final Location<World> chestLocation = player.getWorld().getLocation(
                        playerLocation.getBlockX(),
                        playerLocation.getBlockY() - 1,
                        playerLocation.getBlockZ());
                
                chestLocation.setBlockType(BlockTypes.CHEST, Cause.source(container).build());
                
                final Chest chest = (Chest) chestLocation.getTileEntity().get();
                final TileEntityInventory<TileEntityCarrier> inventory = chest.getInventory();

                sponsorGift.get().getItemStacks().stream().forEach(stack -> inventory.offer(stack));
                
                }

            return CommandResult.success();
        }
    }

    @Inject
    private PluginContainer container;

    @Inject
    private Logger logger;

    @Listener
    public void onGamePreInitialization(GamePreInitializationEvent event) {
        logger.info("Initializing sponsor command!!!!");

        CommandSpec myCommandSpec = CommandSpec.builder()
            .description(Text.of("Sponsor Command"))
            .permission("sponsorplugin.command.smite")
            .arguments(
                    GenericArguments.onlyOne(GenericArguments.player(Text.of("player"))),
                    GenericArguments.remainingJoinedStrings(Text.of("gift")))
            .executor(new SponsorCommand(container, logger))
            .build();

    Sponge.getCommandManager().register(this, myCommandSpec, "sponsor");

    }
}
