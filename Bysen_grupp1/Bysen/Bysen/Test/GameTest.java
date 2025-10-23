import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {
    private Game game;

    @BeforeEach
    void setUp() {
        game = new Game();
        List<String> messages = new ArrayList<>();
        messages.add("Test message");
        game.setMessages(messages);
    }

    //positive test
    @Test
    void getMessages() {
        assertNotNull(game.getMessages());
        assertEquals(1, game.getMessages().size());
    }

    //negative test
    @Test
    void testGetMessagesWhenEmpty() {
        game.setMessages(List.of());
        assertNull(game.getMessages());
    }

    //positive test
    @Test
    void setMessages() {
        List<String> messages = new ArrayList<>();
        messages.add("Test message 2");
        game.setMessages(messages);
        assertEquals(messages, game.getMessages());
    }

    //negative test
    @Test
    void testSetMessagesWithNull() {
        Game game = new Game();
        assertThrows(NullPointerException.class, () -> game.setMessages(null));
    }

    //positive test
    @Test
    public void startNewGame() {
        game.startNewGame();
        assertFalse(game.gameOver);
        assertNotNull(game.getPlayerName());
    }

    //negative test, edge-case
    @Test
    void testStartNewGameWhenGameIsNotOver() {
        assertDoesNotThrow(() -> game.startNewGame());
    }
}