package fr.ph1lou.elections;

import fr.ph1lou.elections.commands.ElectionCommand;
import fr.ph1lou.elections.elections.ElectionManager;
import fr.ph1lou.elections.elections.ElectionState;
import io.github.ph1lou.werewolfapi.Formatter;
import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.StateGame;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.enums.TimerBase;
import io.github.ph1lou.werewolfapi.enums.UniversalMaterial;
import io.github.ph1lou.werewolfapi.registers.AddonRegister;
import io.github.ph1lou.werewolfapi.registers.CommandRegister;
import io.github.ph1lou.werewolfapi.registers.ConfigRegister;
import io.github.ph1lou.werewolfapi.registers.IRegisterManager;
import io.github.ph1lou.werewolfapi.registers.TimerRegister;
import io.github.ph1lou.werewolfapi.utils.BukkitUtils;
import io.github.ph1lou.werewolfapi.utils.ItemBuilder;
import io.github.ph1lou.werewolfapi.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Optional;
import java.util.UUID;

public class Main extends JavaPlugin {

    private GetWereWolfAPI ww;
    private ElectionManager electionManager;


    public void onEnable() {

        this.ww = getServer().getServicesManager().load(GetWereWolfAPI.class);

        if(this.ww == null){
            return;
        }

        IRegisterManager registerManager = this.ww.getRegisterManager();
        String addons = "werewolf.addons_elections";

        registerManager.registerAddon((new AddonRegister(addons, "fr", this))
                .setItem(new ItemBuilder(UniversalMaterial.SIGN.getStack()).build())
                .addAuthors("Ph1Lou",UUID.fromString("056be797-2a0b-4807-9af5-37faf5384396")));


        registerManager.registerCommands(new CommandRegister(addons,"werewolf.election.command", new ElectionCommand(this))
            .addStateWW(StateGame.GAME)
            .addStateAccess(StatePlayer.ALIVE));

        registerManager.registerTimer(new TimerRegister(addons,"werewolf.election.timer_application")
                .setDefaultValue(120)
                .addPredicate(api -> api.getConfig().getTimerValue("werewolf.election.timer") < 0 && api.getConfig().isConfigActive("werewolf.election.name"))
                .onZero(wereWolfAPI -> {
                    this.getElectionManager().ifPresent(electionManager1 -> electionManager1.setState(ElectionState.ELECTION));
                    Bukkit.broadcastMessage(wereWolfAPI.translate("werewolf.election.vote", Formatter.format("&timer&", Utils.conversion(wereWolfAPI.getConfig().getTimerValue("werewolf.election.timer_vote_mayor")))));
                }));

        registerManager.registerTimer(new TimerRegister(addons,"werewolf.election.timer_vote_mayor")
                .setDefaultValue(90)
                .addPredicate(api -> api.getConfig().getTimerValue("werewolf.election.timer_application") < 0 && api.getConfig().isConfigActive("werewolf.election.name"))
                .onZero(api -> this.getElectionManager().ifPresent(ElectionManager::getResult)));

        registerManager.registerTimer(new TimerRegister(addons,"werewolf.election.timer").setDefaultValue(1800)
                .onZero(wereWolfAPI -> {

            if(!wereWolfAPI.getConfig().isConfigActive("werewolf.election.name")){
                return;
            }

            Bukkit.broadcastMessage(wereWolfAPI.translate("werewolf.election.begin",Formatter.format("&timer&", Utils.conversion(wereWolfAPI.getConfig().getTimerValue("werewolf.election.timer_application")))));
            this.getElectionManager().ifPresent(electionManager1 -> electionManager1.setState(ElectionState.MESSAGE));

        }).addPredicate(wereWolfAPI -> wereWolfAPI.getConfig().getTimerValue(TimerBase.ROLE_DURATION.getKey()) < 0
                && !wereWolfAPI.getConfig().isTrollSV() && wereWolfAPI.getConfig().isConfigActive("werewolf.election.name")));



        registerManager.registerConfig(new ConfigRegister(addons,"werewolf.election.name"));

        BukkitUtils.registerEvents(new ElectionListener(this));

        BukkitUtils.registerEvents(new EventListener());

    }


    public GetWereWolfAPI getWereWolfAPI() {
        return this.ww;
    }

    public Optional<ElectionManager> getElectionManager() {
        return Optional.ofNullable(this.electionManager);
    }

    public void createGame(WereWolfAPI api){
        this.electionManager = new ElectionManager(api);
    }

}
