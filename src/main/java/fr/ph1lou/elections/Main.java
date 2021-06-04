package fr.ph1lou.elections;

import fr.ph1lou.elections.commands.ElectionCommand;
import fr.ph1lou.elections.elections.ElectionManager;
import fr.ph1lou.elections.elections.ElectionState;
import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.StateGame;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.enums.UniversalMaterial;
import io.github.ph1lou.werewolfapi.registers.AddonRegister;
import io.github.ph1lou.werewolfapi.registers.CommandRegister;
import io.github.ph1lou.werewolfapi.registers.ConfigRegister;
import io.github.ph1lou.werewolfapi.registers.IRegisterManager;
import io.github.ph1lou.werewolfapi.registers.TimerRegister;
import io.github.ph1lou.werewolfapi.utils.BukkitUtils;
import io.github.ph1lou.werewolfapi.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Optional;
import java.util.UUID;

public class Main extends JavaPlugin {

    private GetWereWolfAPI ww;
    private ElectionManager electionManager;


    public void onEnable() {

        this.ww = getServer().getServicesManager().load(GetWereWolfAPI.class);
        IRegisterManager registerManager = this.ww.getRegisterManager();
        String addons = "werewolf.addons_elections";

        registerManager.registerAddon((new AddonRegister(addons, "fr", this))
                .setItem(new ItemBuilder(UniversalMaterial.SIGN.getStack()).build())
                .addAuthors("Ph1Lou",UUID.fromString("056be797-2a0b-4807-9af5-37faf5384396")));


        registerManager.registerCommands(new CommandRegister(addons,"werewolf.election.command", new ElectionCommand(this))
            .addStateWW(StateGame.GAME)
            .addStateAccess(StatePlayer.ALIVE));

        registerManager.registerTimer(new TimerRegister(addons,"werewolf.election.timer").setDefaultValue(36000).onZero(wereWolfAPI -> {
            Bukkit.broadcastMessage(wereWolfAPI.translate("werewolf.election.begin"));
            this.getElectionManager().ifPresent(electionManager1 -> electionManager1.setState(ElectionState.MESSAGE));
            BukkitUtils.scheduleSyncDelayedTask(() -> {
                if(wereWolfAPI.isState(StateGame.GAME)){
                    this.getElectionManager().ifPresent(electionManager1 -> electionManager1.setState(ElectionState.ELECTION));
                    Bukkit.broadcastMessage(wereWolfAPI.translate("werewolf.election.vote"));
                    BukkitUtils.scheduleSyncDelayedTask(() -> {
                        if(wereWolfAPI.isState(StateGame.GAME)){
                            this.getElectionManager().ifPresent(ElectionManager::getResult);

                        }
                    },1200);
                }
            },2400);
        }));



        registerManager.registerConfig(new ConfigRegister(addons,"werewolf.election.name"));

        BukkitUtils.registerEvents(new ElectionListener(this));
    }


    public GetWereWolfAPI getWw() {
        return ww;
    }

    public Optional<ElectionManager> getElectionManager() {
        return Optional.ofNullable(this.electionManager);
    }

    public void createGame(WereWolfAPI api){
        this.electionManager = new ElectionManager(api);
    }

}
