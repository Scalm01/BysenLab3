/**
 * Represents a room in the game.
 */
public class Room {
    int x, y;

    public Room(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int[] getCoordinates() {
        return new int[]{x, y};
    }
}
