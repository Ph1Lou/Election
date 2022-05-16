package fr.ph1lou.elections;

import fr.ph1lou.elections.elections.ElectionManager;
import fr.ph1lou.werewolfapi.GetWereWolfAPI;
import fr.ph1lou.werewolfapi.annotations.Author;
import fr.ph1lou.werewolfapi.annotations.ModuleWerewolf;
import fr.ph1lou.werewolfapi.enums.UniversalMaterial;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.utils.BukkitUtils;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Optional;

@ModuleWerewolf(key = Main.KEY, loreKeys = {}, item = UniversalMaterial.SIGN,
        defaultLanguage = "fr",
        authors = @Author(uuid = "056be797-2a0b-4807-9af5-37faf5384396", name = "Ph1Lou"))
public class Main extends JavaPlugin {

    private GetWereWolfAPI ww;
    private ElectionManager electionManager;
    public static final String KEY = "elections.name";


    public void onEnable() {

        this.ww = getServer().getServicesManager().load(GetWereWolfAPI.class);

        BukkitUtils.registerListener(new ElectionListener(this));

        BukkitUtils.registerListener(new EventListener());
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
