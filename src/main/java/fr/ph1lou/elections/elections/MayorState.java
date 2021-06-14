package fr.ph1lou.elections.elections;

public enum MayorState {
    DOCTOR("werewolf.election.regime.doctor.name",
            "werewolf.election.regime.doctor.description"),
    FARMER("werewolf.election.regime.name.farmer.name",
            "werewolf.election.regime.name.farmer.description"),
    RETAILER("werewolf.election.regime.retailer.name",
            "werewolf.election.regime.retailer.description"),
    BLACK_SMITH("werewolf.election.regime.black_smith.name",
            "werewolf.election.regime.black_smith.description");

    private final String key;
    private final String description;

    MayorState(String key,String description){
        this.key=key;
        this.description=description;
    }

    public String getKey() {
        return key;
    }

    public String getDescription() {
        return description;
    }
}
