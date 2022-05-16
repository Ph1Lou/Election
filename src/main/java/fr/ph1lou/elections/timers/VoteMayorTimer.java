package fr.ph1lou.elections.timers;


import fr.ph1lou.elections.Main;
import fr.ph1lou.elections.elections.ElectionManager;
import fr.ph1lou.elections.events.MayorElectionVoteEndEvent;
import fr.ph1lou.werewolfapi.GetWereWolfAPI;
import fr.ph1lou.werewolfapi.annotations.Timer;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.listeners.impl.ListenerManager;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;

@Timer(key = "elections.election.timer_vote_mayor",
        defaultValue = 90,
        meetUpValue = 90,
        onZero = MayorElectionVoteEndEvent.class,
        decrementAfterTimer = "elections.election.timer_application")
public class VoteMayorTimer extends ListenerManager {

    public VoteMayorTimer(GetWereWolfAPI main) {
        super(main);
    }

    @EventHandler
    public void onMayorVoteEnd(MayorElectionVoteEndEvent event){

        WereWolfAPI wereWolfAPI = this.getGame();
        Main main = JavaPlugin.getPlugin(Main.class);

        if(!wereWolfAPI.getConfig().isConfigActive("elections.election.name")){
            return;
        }

        main.getElectionManager()
                .ifPresent(ElectionManager::getResult);
    }
}
