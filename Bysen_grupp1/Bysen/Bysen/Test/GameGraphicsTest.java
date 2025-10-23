import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

class GameGraphicsTest {
    Game mockGame;
    GameGraphics gameGraphics;
    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        mockGame = new Game();
        gameGraphics = new GameGraphics(mockGame);
    }

    @Test
    void calculatePlayerPosition() {
        Room room = new Room(100, 100);  // Fiktiv konstruktor
        int roomSize = 50;
        int playerSize = 10;

        Point result = gameGraphics.calculatePlayerPosition(room, roomSize, playerSize);

        assertEquals(120, result.x);
        assertEquals(138, result.y);
    }

}