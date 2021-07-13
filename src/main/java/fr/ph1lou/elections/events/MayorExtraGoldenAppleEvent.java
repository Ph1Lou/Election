package fr.ph1lou.elections.events;

import io.github.ph1lou.werewolfapi.IPlayerWW;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class MayorExtraGoldenAppleEvent extends Event {

    private static final HandlerList HANDLERS_LIST = new HandlerList();

    private final IPlayerWW playerWW;

    public MayorExtraGoldenAppleEvent(IPlayerWW playerWW) {
        this.playerWW =playerWW;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }

    public IPlayerWW getPlayerWW() {
        return playerWW;
    }
}
