package fr.ph1lou.elections.timers;


import fr.ph1lou.elections.Main;
import fr.ph1lou.elections.elections.ElectionState;
import fr.ph1lou.elections.events.MayorElectionVoteBeginEvent;
import fr.ph1lou.werewolfapi.GetWereWolfAPI;
import fr.ph1lou.werewolfapi.annotations.Timer;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.listeners.impl.ListenerManager;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;


@Timer(key = "elections.election.timer_application",
        defaultValue = 120,
        meetUpValue = 120,
        decrementAfterTimer = "elections.election.timer",
        onZero = MayorElectionVoteBeginEvent.class)
public class ApplicationTimer extends ListenerManager {

  public ApplicationTimer(GetWereWolfAPI main) {
    super(main);
  }

  @EventHandler
  public void onMayorVoteBegin(MayorElectionVoteBeginEvent event){

      WereWolfAPI wereWolfAPI = this.getGame();
      Main main = JavaPlugin.getPlugin(Main.class);

      if(!wereWolfAPI.getConfig().isConfigActive("elections.election.name")){
        return;
      }

      main.getElectionManager()
              .ifPresent(electionManager1 -> electionManager1.setState(ElectionState.ELECTION));
      Bukkit.broadcastMessage(wereWolfAPI.translate("elections.election.vote",
              Formatter.format("&timer&",
              Utils.conversion(wereWolfAPI.getConfig().getTimerValue("elections.election.timer_vote_mayor")))));
    }
}
