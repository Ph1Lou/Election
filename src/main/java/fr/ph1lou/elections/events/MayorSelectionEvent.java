package fr.ph1lou.elections.events;

import fr.ph1lou.elections.elections.MayorState;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class MayorSelectionEvent extends Event {

    private final MayorState state;
    private final IPlayerWW playerWW;
    private final int numberVotes;
    private static final HandlerList HANDLERS_LIST = new HandlerList();

    public MayorSelectionEvent(IPlayerWW playerWW, MayorState state, int numberVotes){
        this.playerWW = playerWW;
        this.state = state;
        this.numberVotes = numberVotes;
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

    public MayorState getState() {
        return state;
    }

    public int getNumberVotes() {
        return this.numberVotes;
    }
}
