package fr.ph1lou.elections;

import fr.ph1lou.elections.elections.ElectionManager;
import fr.ph1lou.elections.elections.MayorState;
import fr.ph1lou.elections.events.MayorDeathEvent;
import fr.ph1lou.elections.events.MayorExtraGoldenAppleEvent;
import fr.ph1lou.elections.events.MayorGoldenAppleEvent;
import fr.ph1lou.elections.events.MayorResurrectionEvent;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.events.UpdatePlayerNameTagEvent;
import fr.ph1lou.werewolfapi.events.game.game_cycle.StartEvent;
import fr.ph1lou.werewolfapi.events.game.life_cycle.FinalDeathEvent;
import fr.ph1lou.werewolfapi.events.game.life_cycle.ThirdDeathEvent;
import fr.ph1lou.werewolfapi.events.game.vote.VoteEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Map;


public class ElectionListener implements Listener {

    private final Main main;

    public ElectionListener(Main main) {
        this.main=main;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onNameTagUpdate(UpdatePlayerNameTagEvent event){
        this.main.getElectionManager().flatMap(ElectionManager::getMayor).ifPresent(playerWW -> {
            if (playerWW.getUUID().equals(event.getPlayerUUID())) {
                event.setPrefix(event.getPrefix() + this.main.getWereWolfAPI().getWereWolfAPI().translate("elections.election.star"));
            }
        });
    }

    @EventHandler
    public void onStart(StartEvent event){
        this.main.createGame(event.getWereWolfAPI());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onFinalDeath(FinalDeathEvent event){
        this.main.getElectionManager().ifPresent(electionManager -> electionManager.getMayor().ifPresent(playerWW -> {
            if(event.getPlayerWW().equals(playerWW)){
                electionManager.setMayor(null);
                Bukkit.broadcastMessage(this.main.getWereWolfAPI()
                        .getWereWolfAPI().translate("elections.election.death"));
                Bukkit.getPluginManager().callEvent(new MayorDeathEvent(event.getPlayerWW()));
            }
            else if(playerWW.isState(StatePlayer.ALIVE) && electionManager.getMayorState() == MayorState.UNDERTAKER){

                if(event.getPlayerWW().getRole().getCamp().equals(playerWW.getRole().getCamp())){
                    playerWW.addItem(new ItemStack(Material.GOLDEN_APPLE));
                    playerWW.sendMessageWithKey("elections.election.regime.undertaker.message");
                    Bukkit.getPluginManager().callEvent(new MayorGoldenAppleEvent(playerWW,event.getPlayerWW()));

                }
            }
        }));
    }


    @EventHandler
    public void onGoldenAppleCraft(CraftItemEvent event){
        this.main.getElectionManager().ifPresent(electionManager ->  electionManager.getMayor().ifPresent(playerWW -> {

            if(electionManager.getMayorState() != MayorState.FARMER){
                return;
            }

            if (event.getWhoClicked().getUniqueId().equals(playerWW.getUUID())) {
                WereWolfAPI game = main.getWereWolfAPI().getWereWolfAPI();

                if(!event.getRecipe().getResult().getType().equals(Material.GOLDEN_APPLE)){
                    return;
                }

                if(game.getRandom().nextFloat()*100<20){
                    playerWW.addItem(new ItemStack(Material.GOLDEN_APPLE));
                    Bukkit.getPluginManager().callEvent(new MayorExtraGoldenAppleEvent(playerWW));
                }
            }
        }));
    }

    @EventHandler
    public void onThirdDeathEvent(ThirdDeathEvent event) {

        if (event.isCancelled()) return;

        this.main.getElectionManager().ifPresent(electionManager -> electionManager.getMayor().ifPresent(playerWW -> {

            if(!electionManager.isPower()){
                return;
            }

            if(electionManager.getMayorState() != MayorState.DOCTOR){
                return;
            }

            if(!playerWW.equals(event.getPlayerWW())){
                return;
            }

            main.getWereWolfAPI().getWereWolfAPI().resurrection(playerWW);

            event.setCancelled(true);

            playerWW.sendMessageWithKey("elections.election.regime.doctor.resurrection");

            Bukkit.getPluginManager().callEvent(new MayorResurrectionEvent(event.getPlayerWW()));


            electionManager.unSetPower();
        }));
    }

    @EventHandler
    private void onMayorBlackSmith(EntityDamageByEntityEvent event) {

        if (!(event.getEntity() instanceof Player)) return;

        WereWolfAPI game = main.getWereWolfAPI().getWereWolfAPI();

        Player player = (Player) event.getEntity();

        this.main.getElectionManager().ifPresent(electionManager -> electionManager.getMayor().ifPresent(playerWW -> {

            if (electionManager.getMayorState() != MayorState.BLACK_SMITH) {
                return;
            }

            if (!playerWW.equals(game.getPlayerWW(player.getUniqueId()).orElse(null))) {
                return;
            }

            event.setDamage(event.getDamage() * 90 / 100f);
        }));
    }


    @EventHandler
    public void onVote(VoteEvent event){
        this.main.getElectionManager().flatMap(ElectionManager::getMayor).ifPresent(playerWW -> {
            if (playerWW.equals(event.getPlayerWW())) {
                Map<IPlayerWW,Integer> votes = this.main.getWereWolfAPI().getWereWolfAPI().getVoteManager().getVotes();
                votes.put(event.getTargetWW(),votes.getOrDefault(event.getTargetWW(),0)+1);
            }
        });
    }
}
