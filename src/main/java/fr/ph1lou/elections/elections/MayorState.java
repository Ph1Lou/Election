package fr.ph1lou.elections.elections;

public enum MayorState {
    DOCTOR("elections.election.regime.doctor.name",
            "elections.election.regime.doctor.description"),
    FARMER("elections.election.regime.farmer.name",
            "elections.election.regime.farmer.description"),
    UNDERTAKER("elections.election.regime.undertaker.name",
            "elections.election.regime.undertaker.description"),
    BLACK_SMITH("elections.election.regime.black_smith.name",
            "elections.election.regime.black_smith.description");

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
