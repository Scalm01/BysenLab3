/**
 * Enum for the different creatures in the game.
 * Each creature has a warning that is displayed when the player is close to the creature.
 */
public enum Creatures {
    Bysen("Du hör ett mummel"),
    Troll("Du ser en trollring"),
    Vittra("Du känner ett kallt isande drag"),
    Vätte("Du känner illamående"),
    Tomte("Du hör någon som ropar ditt namn");

    Creatures(String warning) {
        this.warning = warning;
    }
    final String warning;
}
