package fr.ph1lou.elections.configurations;

import fr.ph1lou.elections.events.MayorElectionApplicationBeginEvent;
import fr.ph1lou.elections.events.MayorElectionVoteBeginEvent;
import fr.ph1lou.elections.events.MayorElectionVoteEndEvent;
import fr.ph1lou.werewolfapi.annotations.Configuration;
import fr.ph1lou.werewolfapi.annotations.Timer;


@Configuration(key = "elections.election.name",
        timers = {
                @Timer(key = "elections.election.timer", defaultValue = 1800,
                        meetUpValue = 1800,
                        decrementAfterRole = true,
                        onZero = MayorElectionApplicationBeginEvent.class),
                @Timer(key = "elections.election.timer_application",
                        defaultValue = 120,
                        meetUpValue = 120,
                        decrementAfterTimer = "elections.election.timer",
                        onZero = MayorElectionVoteBeginEvent.class),
                @Timer(key = "elections.election.timer_vote_mayor",
                        defaultValue = 90,
                        meetUpValue = 90,
                        onZero = MayorElectionVoteEndEvent.class,
                        decrementAfterTimer = "elections.election.timer_application")})
public class Election {
}
