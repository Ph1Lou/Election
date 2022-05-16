package fr.ph1lou.elections.timers;

import fr.ph1lou.elections.Main;
import fr.ph1lou.elections.elections.ElectionState;
import fr.ph1lou.elections.events.MayorElectionApplicationBeginEvent;
import fr.ph1lou.werewolfapi.GetWereWolfAPI;
import fr.ph1lou.werewolfapi.annotations.Timer;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.listeners.impl.ListenerManager;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;

@Timer(key = "elections.election.timer", defaultValue = 1800,
        meetUpValue = 1800,
        decrementAfterRole = true,
        onZero = MayorElectionApplicationBeginEvent.class)
public class ElectionDuration extends ListenerManager {
    public ElectionDuration(GetWereWolfAPI main) {
        super(main);
    }

    @EventHandler
    public void onElectionBegin(MayorElectionApplicationBeginEvent event){

        Main main = JavaPlugin.getPlugin(Main.class);
        WereWolfAPI wereWolfAPI = this.getGame();

        if(!wereWolfAPI.getConfig().isConfigActive("elections.election.name")){
            return;
        }

        Bukkit.broadcastMessage(wereWolfAPI.translate("elections.election.begin", Formatter.format("&timer&",
                Utils.conversion(wereWolfAPI.getConfig().getTimerValue("elections.election.timer_application")))));
        main.getElectionManager()
                .ifPresent(electionManager1 -> electionManager1.setState(ElectionState.MESSAGE));
    }
}
