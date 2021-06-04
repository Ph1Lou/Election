package fr.ph1lou.elections.elections;

import io.github.ph1lou.werewolfapi.Formatter;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.events.UpdateNameTagEvent;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class ElectionManager {

    private final WereWolfAPI api;
    private ElectionState electionState = ElectionState.NOT_BEGIN;
    private final Map<IPlayerWW, String> playerMessages = new HashMap<>();
    private final Map<IPlayerWW, IPlayerWW> votes = new HashMap<>();
    private IPlayerWW mayor;

    public ElectionManager(WereWolfAPI api){
        this.api=api;
    }

    public void setState(ElectionState electionState) {
        this.electionState = electionState;
    }

    public Optional<IPlayerWW> getMayor(){
        return Optional.ofNullable(this.mayor);
    }

    public void setMayor(@Nullable IPlayerWW mayor){
        if(this.mayor!=null){
            Bukkit.getPluginManager().callEvent(new UpdateNameTagEvent(this.mayor));
        }
        this.mayor=mayor;
        if(mayor!=null){
            Bukkit.getPluginManager().callEvent(new UpdateNameTagEvent(mayor));
        }
    }

    public void addMessage(IPlayerWW playerWW, String message){
        this.playerMessages.put(playerWW,message);
    }

    public void addVote(IPlayerWW playerWW, IPlayerWW target){
        this.votes.put(playerWW,target);
    }

    public Optional<String> getPlayerMessage(IPlayerWW playerWW) {
        if(this.playerMessages.containsKey(playerWW)){
            return Optional.of(this.playerMessages.get(playerWW));
        }
        return Optional.empty();
    }

    public Set<IPlayerWW> getCandidates(){
        return this.playerMessages.keySet();
    }

    public Set<IPlayerWW> getVoters(IPlayerWW playerWW){
        Set<IPlayerWW> voters = new HashSet<>();
        this.votes.forEach((playerWW1, playerWW2) -> {
            if(playerWW2.equals(playerWW)){
                voters.add(playerWW2);
            }
        });
        return voters;
    }

    public boolean isState(ElectionState state) {
        return this.electionState == state;
    }


    public void getResult() {
        Map<IPlayerWW,Integer> votes = new HashMap<>();
        AtomicInteger max= new AtomicInteger();
        AtomicReference<IPlayerWW> mayor = new AtomicReference<>();
        this.votes.values().forEach(playerWW -> votes.merge(playerWW,1,Integer::sum));

        votes.forEach((playerWW, integer) -> {
            if(integer> max.get()){
                max.set(integer);
                mayor.set(playerWW);
            }
        });

        if(max.get()==0) return;

        this.setState(ElectionState.FINISH);

        Bukkit.broadcastMessage(api.translate("werewolf.election.result",
                Formatter.format("&name&",mayor.get()),
                Formatter.format("&votes&",max.get())));
    }
}
