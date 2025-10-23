import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;
import javax.swing.*;
import static javax.swing.SwingUtilities.isLeftMouseButton;
import static javax.swing.SwingUtilities.isRightMouseButton;

/**
 * The game class represents the main game logic and UI.
 */
public class Game extends JPanel{
    static final Random rand = new Random();

    // Constants for room and player sizes
    final int roomSize = 45;
    final int playerSize = 16;

    //Game state variables
    private String playerName;
    boolean gameOver = true;
    int currRoom, numNets, creatureRoom;
    private List<String> messages = new ArrayList<>();
    Set<Creatures>[] creatures;

    //Quiz questions and answers
    private String[] questions = {"What is the capital of France?",
            "What is the largest planet in our solar system?",
            "What is the smallest country in the world?"};
    private String[][] options = {{"Paris", "London", "Berlin", "Rome"},
            {"Jupiter", "Saturn", "Uranus", "Neptune"},
            {"Vatican City", "Monaco", "Nauru", "Tuvalu"}};
    private int[] correctAnswers = {0, 0, 0};

    //Game components
    private GameGraphics gameGraphics;
    Room[] rooms = {
            new Room(334, 20), new Room(609, 220), new Room(499, 540),
            new Room(169, 540), new Room(62, 220), new Room(169, 255),
            new Room(232, 168), new Room(334, 136), new Room(435, 168),
            new Room(499, 255), new Room(499, 361), new Room(435, 447),
            new Room(334, 480), new Room(232, 447), new Room(169, 361),
            new Room(254, 336), new Room(285, 238), new Room(387, 238),
            new Room(418, 336), new Room(334, 393)
    };

    int[][] links = {{4, 7, 1}, {0, 9, 2}, {1, 11, 3}, {4, 13, 2}, {0, 5, 3},
            {4, 6, 14}, {7, 16, 5}, {6, 0, 8}, {7, 17, 9}, {8, 1, 10}, {9, 18, 11},
            {10, 2, 12}, {13, 19, 11}, {14, 3, 12}, {5, 15, 13}, {14, 16, 19},
            {6, 17, 15}, {16, 8, 18}, {19, 10, 17}, {15, 12, 18}};

    //Constructor
    public Game() {
        initUI();
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                handleMousePress(e);
            }
        });
        gameGraphics = new GameGraphics(this);
    }

    //Getters and setters
    public List<String> getMessages(){
        if (!messages.isEmpty())
            return messages;
        return null;
    }
    public void setMessages(List<String> messages){
        if (messages == null)
            throw new NullPointerException("Messages cannot be null");
        this.messages = messages;
    }
    public String getPlayerName() {
        return playerName;
    }

    /**
     * Initializes the user interface.
     */
    private void initUI() {
        setPreferredSize(new Dimension(721, 687));
        setBackground(Color.white);
        setForeground(Color.lightGray);
        setFont(new Font("SansSerif", Font.PLAIN, 18));
        setFocusable(true);
    }

    /**
     * Handles the mouse press event.
     *
     * @param e The mouse event.
     */
    private void handleMousePress(MouseEvent e) {
        if (gameOver) {
            startNewGame();
        } else {
            int selectedRoom = getSelectedRoom(e);
            if (selectedRoom != -1) {
                handleRoomSelection(e, selectedRoom);
            }
        }
        repaint();
        showMessageDialog();
    }

    /**
     * Shows a message dialog with the messages in the list.
     */
    private void showMessageDialog() {
        if (!messages.isEmpty()) {
            StringBuilder message = new StringBuilder();
            for (String msg : messages) {
                message.append(msg).append("\n");
            }
            JOptionPane.showMessageDialog(this, message.toString(),
                    playerName, JOptionPane.INFORMATION_MESSAGE);
            messages.clear(); // Clear the messages
        }
    }

    /**
     * Returns the index of the room selected by the player.
     *
     * @param e The mouse event.
     * @return The index of the selected room.
     */
    private int getSelectedRoom(MouseEvent e) {
        int mouseX = e.getX();
        int mouseY = e.getY();
        for (int link : links[currRoom]) {
            Room room = rooms[link];
            int frameX = room.x;
            int frameY = room.y;
            if (insideRoom(mouseX, mouseY, frameX, frameY)) {
                return link;
            }
        }
        return -1;
    }

    /**
     * Handles the selection of a room.
     *
     * @param e The mouse event.
     * @param selectedRoom The index of the selected room.
     */
    private void handleRoomSelection(MouseEvent e, int selectedRoom) {
        if (isLeftMouseButton(e)) {
            currRoom = selectedRoom;
            handleRoomSituation();
        } else if (isRightMouseButton(e)) {
            throwNet(selectedRoom);
        }
    }

    /**
     * Checks if the given coordinates are inside the room.
     *
     * @param mouseX The x-coordinate of the mouse.
     * @param mouseY The y-coordinate of the mouse.
     * @param frameX The x-coordinate of the room.
     * @param frameY The y-coordinate of the room.
     * @return True if the mouse is inside the room, false otherwise.
     */
    private boolean insideRoom(int mouseX, int mouseY, int frameX, int frameY) {
        return ((mouseX > frameX && mouseX < frameX + roomSize)
                && (mouseY > frameY && mouseY < frameY + roomSize));
    }

    /**
     * Starts a new game.
     */
    public void startNewGame() {
        playerName = JOptionPane.showInputDialog(
                this, "Please enter your name:",
                "Player Name", JOptionPane.QUESTION_MESSAGE);
        resetGameVariables();
        initializeCreatures();
        distributeCreatures();
        gameOver = false;
    }

    /**
     * Resets the game variables to their initial state.
     */
    private void resetGameVariables() {
        numNets = 3;
        currRoom = rand.nextInt(rooms.length);
        messages = new ArrayList<>();
    }

    /**
     * Initializes the creatures array.
     */
    private void initializeCreatures() {
        creatures = new Set[rooms.length];
        for (int i = 0; i < rooms.length; i++)
            creatures[i] = EnumSet.noneOf(Creatures.class);
    }

    /**
     * Distributes the creatures among the rooms.
     */
    private void distributeCreatures() {
        int[] creatureDistribution = {0, 1, 1, 1, 2, 2, 3, 4, 4};
        Creatures[] values = Creatures.values();
        for (int ord : creatureDistribution) {
            int room = getAvailableRoom(ord);
            if (ord == 4) {
                creatures[room].clear(); // clear any existing creatures in the room
                creatures[room].add(values[ord]);
            } else {
                creatures[room].add(values[ord]);
                if (ord == 0)
                    creatureRoom = room;
            }
        }
    }

    /**
     * Returns a random room that is not too close to the player's current room or any of its links.
     *
     * @param ord The ordinal of the creature to be placed in the room.
     * @return The index of the available room.
     */
    private int getAvailableRoom(int ord) {
        int room;
        do {
            room = rand.nextInt(rooms.length);
        } while (tooClose(room) || creatures[room].contains(Creatures.values()[ord]));
        return room;
    }

    /**
     * Checks if the given room is too close to the player's current room or any of its links.
     *
     * @param room The index of the room to check.
     * @return True if the room is too close, false otherwise.
     */
    private boolean tooClose(int room) {
        if (currRoom == room)
            return true;
        for (int link : links[currRoom])
            if (room == link)
                return true;
        return false;
    }

    /**
     * Handles the situation when the player enters a room.
     */
    private void handleRoomSituation() {
        Set<Creatures> set = creatures[currRoom];
        if (set.contains(Creatures.Bysen)) {
            handleBysen();
        } else if (set.contains(Creatures.Troll)) {
            handleTroll();
        } else if (set.contains(Creatures.Vittra)) {
            handleVittra();
        } else if (set.contains(Creatures.Vätte)){
            handleVätte();
        } else if (set.contains(Creatures.Tomte)){
            handleTomte();
        } else {
            exploreRoom();
        }
    }

    /**
     * Handles the situation when the player encounters Bysen.
     */
    private void handleBysen() {
        messages.add("Bysen lockar dig att gå vilse");
        gameOver = true;
    }

    /**
     * Handles the situation when the player encounters a Troll.
     */
    private void handleTroll() {
        messages.add("Du faller ner i trollringen");
        gameOver = true;
    }

    /**
     * Handles the situation when the player encounters a Vittra.
     */
    private void handleVittra() {
        messages.add("Vittran kör iväg dig till ett slumpat rum");
        movePlayerAwayFromVittra();
        moveVittraToNewRoom();
        handleRoomSituation(); // re-evaluate the situation
    }

    private void movePlayerAwayFromVittra() {
        do {
            currRoom = rand.nextInt(rooms.length);
        } while (creatures[currRoom].contains(Creatures.Vittra));
    }


    private void moveVittraToNewRoom() {
        int newRoom;
        do {
            newRoom = rand.nextInt(rooms.length);
        } while (newRoom == currRoom || creatures[newRoom].contains(Creatures.Vittra));
        creatures[newRoom].add(Creatures.Vittra);
    }

    /**
     * Handles the situation when the player encounters a Vätte.
     */
    private void handleVätte(){
        messages.add("Du är sjuk och kan inte försätta spela");
        gameOver = true;
    }

    /**
     * Handles the situation when the player encounters a Tomte.
     */
    private void handleTomte(){
        int questionIndex = (int) (Math.random() * questions.length); // select a random question
        String question = questions[questionIndex];
        String[] optionsArray = options[questionIndex];
        int correctAnswer = correctAnswers[questionIndex];

        int userAnswer = JOptionPane.showOptionDialog(this, question, "Quizmaster",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, optionsArray, optionsArray[0]);

        if (userAnswer == correctAnswer) {
            messages.add("Du har rätt! Ta ett nät!");
            numNets++;
        } else {
            messages.add("Det stämmer inte... Hej då!");
        }
        creatures[currRoom].remove(Creatures.Tomte);
    }

    /**
     * Explores the current room.
     */
    private void exploreRoom() {
        for (int link : links[currRoom]) {
            for (Creatures creature : creatures[link])
                messages.add(creature.warning);
        }
    }

    /**
     * Throws a net at the given room.
     *
     * @param room The index of the room to throw the net at.
     */
    private void throwNet(int room) {
        if (creatures[room].contains(Creatures.Bysen)) {
            catchBysen();
        } else {
            handleNetThrow();
        }
    }

    /**
     * Handles the situation when the player catches Bysen.
     */
    private void catchBysen() {
        messages.add("Du vinner! Du har fångat Bysen!");
        gameOver = true;
    }

    /**
     * Handles the situation when the player throws a net.
     */
    private void handleNetThrow() {
        numNets--;
        if (numNets == 0) {
            outOfNets();
        } else if (rand.nextInt(4) != 0) { // 75 %
            moveBysen();
        }
    }

    /**
     * Handles the situation when the player runs out of nets.
     */
    private void outOfNets() {
        messages.add("Oops! Inga inga nät kvar.");
        gameOver = true;
    }

    /**
     * Moves Bysen to a new room.
     */
    private void moveBysen() {
        creatures[creatureRoom].remove(Creatures.Bysen);
        creatureRoom = links[creatureRoom][rand.nextInt(3)];

        if (creatureRoom == currRoom) {
            wakeUpBysen();
        } else {
            bysenEscapes();
        }
    }

    /**
     * Handles the situation when Bysen is woken up.
     */
    private void wakeUpBysen() {
        messages.add("Du väckte Bysen och han är inte glad!");
        gameOver = true;
    }

    /**
     * Handles the situation when Bysen escapes.
     */
    private void bysenEscapes() {
        messages.add("Du råkade se Bysen och han bara försvann");
        creatures[creatureRoom].add(Creatures.Bysen);
    }

    /**
     * Paints the game components.
     *
     * @param gg The graphics context.
     */
    @Override
    public void paintComponent(Graphics gg) {
        super.paintComponent(gg);
        Graphics2D g = (Graphics2D) gg;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        gameGraphics.drawRooms(g);
        if (gameOver) {
            gameGraphics.drawStartScreen(g);
        } else {
            gameGraphics.drawPlayer(g);
        }
        gameGraphics.drawMessage(g);
    }
}

