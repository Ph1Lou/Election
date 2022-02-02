package fr.ph1lou.elections.elections;

import fr.ph1lou.elections.events.MayorSelectionEvent;
import fr.ph1lou.elections.events.MayorVoteEvent;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.events.UpdateNameTagEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.impl.PotionModifier;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import org.bukkit.Bukkit;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class ElectionManager {

    private final WereWolfAPI api;
    private ElectionState electionState = ElectionState.NOT_BEGIN;
    private final Map<IPlayerWW, String> playerMessages = new HashMap<>();
    private final Map<IPlayerWW, IPlayerWW> votes = new HashMap<>();
    private IPlayerWW mayor;
    private MayorState mayorState = MayorState.values()[(int) Math.floor(new Random(System.currentTimeMillis()).nextFloat() * MayorState.values().length)];
    private boolean power = true;

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
        if(this.mayor !=null){
            Bukkit.getPluginManager().callEvent(new UpdateNameTagEvent(this.mayor));
        }
        this.mayor =mayor;
        if(mayor!=null){
            Bukkit.getPluginManager().callEvent(new UpdateNameTagEvent(mayor));
        }
    }

    public void addMessage(IPlayerWW playerWW, String message){
        this.playerMessages.put(playerWW,message);
    }

    public void addVote(IPlayerWW playerWW, IPlayerWW target){

        if(!playerWW.isState(StatePlayer.ALIVE)){
            return;
        }

        if(playerWW.equals(target)){
            return;
        }
        this.votes.put(playerWW,target);

        Bukkit.getPluginManager().callEvent(new MayorVoteEvent(playerWW,target));
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
                voters.add(playerWW1);
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
        this.setMayor(mayor.get());
        Bukkit.getPluginManager().callEvent(new MayorSelectionEvent(mayor.get(),this.mayorState,max.get()));

        if(this.mayorState == MayorState.FARMER){
            mayor.get().addPotionModifier(PotionModifier.add(PotionEffectType.SATURATION,"mayor"));
        }

        Bukkit.broadcastMessage(api.translate("elections.election.result",
                Formatter.format("&name&",mayor.get().getName()),
                Formatter.format("&votes&",max.get()),
                Formatter.format("&forme&",api.translate(this.getMayorState().getKey()))));

        mayor.get().sendMessageWithKey(this.mayorState.getDescription());
    }

    public boolean isPower() {
        return power;
    }

    public void unSetPower() {
        this.power = false;
    }

    public MayorState getMayorState() {
        return mayorState;
    }

    public void setMayorState(MayorState mayorState) {
        this.mayorState = mayorState;
    }
}
