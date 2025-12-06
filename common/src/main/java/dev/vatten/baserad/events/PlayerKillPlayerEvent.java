package dev.vatten.baserad.events;

import dev.vatten.baserad.VattenPlayer;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;

public class PlayerKillPlayerEvent extends PlayerEvent {
    @Getter
    private VattenPlayer killer;
    @Getter
    @Setter
    private Component deathMessage;

    public PlayerKillPlayerEvent(VattenPlayer player, VattenPlayer killer, Component deathMessage) {
        super(player);
        this.killer = killer;
        this.deathMessage = deathMessage;
    }
}
