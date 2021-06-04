package fr.ph1lou.elections;

import fr.ph1lou.elections.elections.ElectionManager;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.events.UpdatePlayerNameTagEvent;
import io.github.ph1lou.werewolfapi.events.game.game_cycle.StartEvent;
import io.github.ph1lou.werewolfapi.events.game.life_cycle.FinalDeathEvent;
import io.github.ph1lou.werewolfapi.events.game.vote.VoteEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Map;

public class ElectionListener implements Listener {

    private final Main main;

    public ElectionListener(Main main) {
        this.main=main;
    }

    @EventHandler
    public void onNameTagUpdate(UpdatePlayerNameTagEvent event){
        this.main.getElectionManager().flatMap(ElectionManager::getMayor).ifPresent(playerWW -> {
            if (playerWW.getUUID().equals(event.getPlayerUUID())) {
                event.setPrefix(event.getPrefix() + this.main.getWw().getWereWolfAPI().translate("werewolf.election.star"));
            }
        });
    }

    @EventHandler
    public void onStart(StartEvent event){
        this.main.createGame(event.getWereWolfAPI());
    }

    @EventHandler
    public void onFinalDeath(FinalDeathEvent event){
        this.main.getElectionManager().ifPresent(electionManager -> electionManager.getMayor().ifPresent(playerWW -> {
            if(event.getPlayerWW().equals(playerWW)){
                electionManager.setMayor(null);
                Bukkit.broadcastMessage(this.main.getWw().getWereWolfAPI().translate("werewolf.election.death"));
            }
        }));
    }

    @EventHandler
    public void onVote(VoteEvent event){
        this.main.getElectionManager().flatMap(ElectionManager::getMayor).ifPresent(playerWW -> {
            if (playerWW.equals(event.getPlayerWW())) {
                Map<IPlayerWW,Integer> votes = this.main.getWw().getWereWolfAPI().getVote().getVotes();
                votes.put(event.getTargetWW(),votes.getOrDefault(event.getTargetWW(),0)+1);
            }
        });
    }
}
