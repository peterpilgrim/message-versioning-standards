package uk.gov.hmcts.example.messageversioningstandards.model.v2;

public enum Allegiance {
    CULTURE("The Culture"),
    IDIRAN("Idiran Empire"),
    UNALIGNED("Unaligned");

    private String displayName;
    Allegiance(String displayName) {
        this.displayName = displayName;
    }
    public String toString() {
        return displayName;
    }
}
