package fr.ph1lou.elections;

import fr.ph1lou.elections.commands.ElectionCommand;
import fr.ph1lou.elections.elections.ElectionManager;
import fr.ph1lou.elections.elections.ElectionState;
import fr.ph1lou.werewolfapi.GetWereWolfAPI;
import fr.ph1lou.werewolfapi.enums.StateGame;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.enums.TimerBase;
import fr.ph1lou.werewolfapi.enums.UniversalMaterial;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.registers.impl.AddonRegister;
import fr.ph1lou.werewolfapi.registers.impl.CommandRegister;
import fr.ph1lou.werewolfapi.registers.impl.ConfigRegister;
import fr.ph1lou.werewolfapi.registers.impl.TimerRegister;
import fr.ph1lou.werewolfapi.registers.interfaces.IRegisterManager;
import fr.ph1lou.werewolfapi.utils.BukkitUtils;
import fr.ph1lou.werewolfapi.utils.ItemBuilder;
import fr.ph1lou.werewolfapi.utils.Utils;
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
        String addons = "elections.name";

        registerManager.registerAddon((new AddonRegister(addons, "fr", this))
                .setItem(new ItemBuilder(UniversalMaterial.SIGN.getStack()).build())
                .addAuthors("Ph1Lou",UUID.fromString("056be797-2a0b-4807-9af5-37faf5384396")));


        registerManager.registerCommands(new CommandRegister(addons,"elections.election.command", new ElectionCommand(this))
            .addStateWW(StateGame.GAME)
            .addStateAccess(StatePlayer.ALIVE));

        registerManager.registerTimer(new TimerRegister(addons,"elections.election.timer_application")
                .setDefaultValue(120)
                .addPredicate(api -> api.getConfig().getTimerValue("elections.election.timer") < 0 && api.getConfig().isConfigActive("elections.election.name"))
                .onZero(wereWolfAPI -> {
                    this.getElectionManager().ifPresent(electionManager1 -> electionManager1.setState(ElectionState.ELECTION));
                    Bukkit.broadcastMessage(wereWolfAPI.translate("elections.election.vote", Formatter.format("&timer&", Utils.conversion(wereWolfAPI.getConfig().getTimerValue("elections.election.timer_vote_mayor")))));
                }));

        registerManager.registerTimer(new TimerRegister(addons,"elections.election.timer_vote_mayor")
                .setDefaultValue(90)
                .addPredicate(api -> api.getConfig().getTimerValue("elections.election.timer_application") < 0 && api.getConfig().isConfigActive("elections.election.name"))
                .onZero(api -> this.getElectionManager().ifPresent(ElectionManager::getResult)));

        registerManager.registerTimer(new TimerRegister(addons,"elections.election.timer").setDefaultValue(1800)
                .onZero(wereWolfAPI -> {

            if(!wereWolfAPI.getConfig().isConfigActive("elections.election.name")){
                return;
            }

            Bukkit.broadcastMessage(wereWolfAPI.translate("elections.election.begin",Formatter.format("&timer&", Utils.conversion(wereWolfAPI.getConfig().getTimerValue("elections.election.timer_application")))));
            this.getElectionManager().ifPresent(electionManager1 -> electionManager1.setState(ElectionState.MESSAGE));

        }).addPredicate(wereWolfAPI -> wereWolfAPI.getConfig().getTimerValue(TimerBase.ROLE_DURATION.getKey()) < 0
                && !wereWolfAPI.getConfig().isTrollSV() && wereWolfAPI.getConfig().isConfigActive("elections.election.name")));



        registerManager.registerConfig(new ConfigRegister(addons,"elections.election.name"));

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
