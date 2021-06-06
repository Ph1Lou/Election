package fr.ph1lou.elections.elections;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.ph1lou.elections.Main;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.UniversalMaterial;
import io.github.ph1lou.werewolfapi.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class ElectionGUI implements InventoryProvider {

    public ElectionGUI(Player player){

    }

    public static SmartInventory getInventory(Player player){
        return SmartInventory.builder()
                .id("election")
                .manager(JavaPlugin.getPlugin(Main.class).getWereWolfAPI().getInvManager())
                .provider(new ElectionGUI(player))
                .size(6, 9)
                .title(JavaPlugin.getPlugin(Main.class).getWereWolfAPI().getWereWolfAPI().translate("werewolf.election.menu_title"))
                .closeable(true)
                .build();
    }


    @Override
    public void init(Player player, InventoryContents contents) {

        Main main = JavaPlugin.getPlugin(Main.class);
        WereWolfAPI game = main.getWereWolfAPI().getWereWolfAPI();

        IPlayerWW playerWW = game.getPlayerWW(player.getUniqueId()).orElse(null);


        if(playerWW==null) return;

        contents.fillBorders(ClickableItem.empty(UniversalMaterial.ORANGE_STAINED_GLASS_PANE.getStack()));

        AtomicInteger i = new AtomicInteger(10);

        main.getElectionManager().ifPresent(electionManager -> electionManager.getCandidates().forEach(candidateWW -> {

            if(!electionManager.isState(ElectionState.ELECTION)) return;

            contents.set(i.get()/9,i.get()%9,
                    ClickableItem.of(new ItemBuilder(UniversalMaterial.PLAYER_HEAD.getStack())
                            .setHead(candidateWW.getName(),
                                    Bukkit.getOfflinePlayer(candidateWW.getMojangUUID()))
                            .setLore(electionManager.getPlayerMessage(candidateWW).orElse(""))
                            .build(),event -> {
                        if(electionManager.isState(ElectionState.ELECTION)){
                            electionManager.addVote(playerWW,candidateWW);
                        }
                    }));
            i.set(i.get()+2);
        }));

    }

    @Override
    public void update(Player player, InventoryContents contents) {

        Main main = JavaPlugin.getPlugin(Main.class);
        WereWolfAPI game = main.getWereWolfAPI().getWereWolfAPI();

        IPlayerWW playerWW = game.getPlayerWW(player.getUniqueId()).orElse(null);

        AtomicInteger i = new AtomicInteger(10);

        if(playerWW==null) return;


        main.getElectionManager().ifPresent(electionManager -> electionManager.getCandidates().forEach(candidateWW -> {

            if(!electionManager.isState(ElectionState.ELECTION)) return;


            List<String> lore = electionManager.getVoters(candidateWW)
                    .stream()
                    .map(IPlayerWW::getName)
                    .collect(Collectors.toList());

            lore.add(0,game.translate("werewolf.election.application",
                    electionManager.getPlayerMessage(candidateWW).orElse("")));


            contents.get(i.get()/9,i.get()%9).ifPresent(clickableItem -> {
                ItemBuilder item = new ItemBuilder(clickableItem.getItem()).setLore(lore);
                contents.set(i.get()/9,i.get()%9,ClickableItem.of(item.build(),event -> {
                    if(electionManager.isState(ElectionState.ELECTION)){
                        electionManager.addVote(playerWW,candidateWW);
                    }
                }));
                });
            i.set(i.get()+2);
        }));





    }

}

