package fr.ph1lou.elections.commands;

import fr.ph1lou.elections.Main;
import fr.ph1lou.elections.elections.ElectionGUI;
import fr.ph1lou.elections.elections.ElectionState;
import fr.ph1lou.werewolfapi.commands.ICommand;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import org.bukkit.entity.Player;

public class ElectionCommand implements ICommand {

    private final Main main;

    public ElectionCommand(Main main){
        this.main = main;
    }

    @Override
    public void execute(WereWolfAPI wereWolfAPI, Player player, String[] args) {
        main.getElectionManager()
                .ifPresent(electionManager -> {

                    IPlayerWW playerWW = wereWolfAPI.getPlayerWW(player.getUniqueId())
                            .orElse(null);

                    if (playerWW==null) return;

                    if (!wereWolfAPI.getConfig().isConfigActive("elections.election.name")) {
                        playerWW.sendMessageWithKey("elections.election.disable");
                        return;
                    }

                    if (electionManager.isState(ElectionState.NOT_BEGIN) ) {
                        playerWW.sendMessageWithKey("elections.election.not_begin");
                        return;
                    }

                    if (electionManager.isState(ElectionState.MESSAGE)) {

                        if(args.length==0){
                            playerWW.sendMessageWithKey("elections.election.empty");
                            return;
                        }
                        StringBuilder message = new StringBuilder();
                        for (String part : args) {
                            message.append(part).append(" ");
                        }
                        if(electionManager.getPlayerMessage(playerWW).isPresent()){
                            playerWW.sendMessageWithKey("elections.election.change");
                        }
                        else{
                            playerWW.sendMessageWithKey("elections.election.register");
                        }

                        electionManager.addMessage(playerWW,message.toString());
                        return;
                    }

                    if (electionManager.isState(ElectionState.ELECTION)) {
                        ElectionGUI.getInventory(player).open(player);
                        return;
                    }

                    if (electionManager.isState(ElectionState.FINISH)) {
                        playerWW.sendMessageWithKey("elections.election.finish");
                    }
                });

    }
}
