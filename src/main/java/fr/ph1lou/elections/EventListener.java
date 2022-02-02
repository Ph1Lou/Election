package fr.ph1lou.elections;

import com.google.common.collect.Sets;
import fr.ph1lou.elections.events.MayorDeathEvent;
import fr.ph1lou.elections.events.MayorExtraGoldenAppleEvent;
import fr.ph1lou.elections.events.MayorGoldenAppleEvent;
import fr.ph1lou.elections.events.MayorResurrectionEvent;
import fr.ph1lou.elections.events.MayorSelectionEvent;
import fr.ph1lou.elections.events.MayorVoteEvent;
import fr.ph1lou.werewolfapi.events.CustomEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class EventListener implements Listener {

    @EventHandler
    public void onMayorDesign(MayorSelectionEvent event){
        Bukkit.getPluginManager().callEvent(new CustomEvent(event.getPlayerWW(),event.getNumberVotes(),event.getState().getKey(),"elections.mayor_selection"));
    }

    @EventHandler
    public void onMayorDeath(MayorDeathEvent event){
        Bukkit.getPluginManager().callEvent(new CustomEvent(event.getPlayerWW(),0,"elections.mayor_death"));
    }

    @EventHandler
    public void onMayorDeath(MayorVoteEvent event){
        Bukkit.getPluginManager().callEvent(new CustomEvent(event.getPlayerWW(), Sets.newHashSet(event.getTargetWW()) ,"elections.mayor_vote"));
    }

    @EventHandler
    public void onMayorDeath(MayorGoldenAppleEvent event){
        Bukkit.getPluginManager().callEvent(new CustomEvent(event.getPlayerWW(), Sets.newHashSet(event.getTargetWW()) ,"elections.mayor_golden_apple"));
    }

    @EventHandler
    public void onMayorResurrection(MayorResurrectionEvent event){
        Bukkit.getPluginManager().callEvent(new CustomEvent(event.getPlayerWW(),0 ,"elections.mayor_resurrection"));
    }

    @EventHandler
    public void onMayorExtraGoldenApple(MayorExtraGoldenAppleEvent event){
        Bukkit.getPluginManager().callEvent(new CustomEvent(event.getPlayerWW(),0 ,"elections.mayor_extra_golden_apple"));
    }
}
