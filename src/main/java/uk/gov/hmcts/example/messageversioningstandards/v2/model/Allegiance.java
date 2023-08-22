package uk.gov.hmcts.example.messageversioningstandards.v2.model;

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
