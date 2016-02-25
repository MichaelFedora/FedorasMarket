package io.github.michaelfedora.fedorasmarket.listeners;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.event.filter.cause.First;

/**
 * Created by Michael on 2/23/2016.
 */
public class PlayerInteractListener {

    @Listener
    public void onPlayerPrimary(InteractEntityEvent.Primary event, @First Player player) {

    }

    @Listener
    public void onPlayerSecondary(InteractEntityEvent.Secondary event, @First Player player) {

    }
}
