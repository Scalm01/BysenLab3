import java.awt.*;
import java.awt.geom.Path2D;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

/**
 * GameGraphics handles the graphics in the game.
 */
public class GameGraphics {
    private Game game;

    public GameGraphics(Game game) {
        this.game = game;
    }

    /**
     * Calculates the player's position in the room.
     * @param room The room the player is in.
     * @param roomSize The size of the room.
     * @param playerSize The size of the player.
     * @return The player's position.
     */
    public Point calculatePlayerPosition(Room room, int roomSize, int playerSize) {
        int x = room.x + (roomSize - playerSize) / 2;
        int y = room.y + (roomSize - playerSize) - 2;
        return new Point(x, y);
    }

    /**
     * Creates the player's shape.
     * @param x The x-coordinate of the player.
     * @param y The y-coordinate of the player.
     * @param playerSize The size of the player.
     * @return The player's shape.
     */
    public Path2D createPlayerShape(int x, int y, int playerSize) {
        Path2D player = new Path2D.Double();
        player.moveTo(x, y);
        player.lineTo(x + playerSize, y);
        player.lineTo(x + playerSize / 2, y - playerSize);
        player.closePath();
        return player;
    }

    /**
     * Draws the player.
     * @param g The graphics object.
     */
    public void drawPlayer(Graphics2D g) {
        Room room = game.rooms[game.currRoom];
        Point position = calculatePlayerPosition(room, game.roomSize, game.playerSize);
        Path2D player = createPlayerShape(position.x, position.y, game.playerSize);

        g.setColor(Color.white);
        g.fill(player);
        g.setStroke(new BasicStroke(1));
        g.setColor(Color.black);
        g.draw(player);
    }

    /**
     * Draws the game over screen.
     * @param g The graphics object.
     */
    private void drawBackground(Graphics2D g, int width, int height) {
        g.setColor(new Color(0xDDFFFFFF, true));
        g.fillRect(0, 0, width, height - 60);
    }

    /**
     * Draws the title of the game.
     * @param g The graphics object.
     * @param title The title of the game.
     * @param width The width of the game.
     */
    private void drawTitle(Graphics2D g, String title, int width) {
        g.setColor(Color.darkGray);
        g.setFont(new Font("SansSerif", Font.BOLD, 48));
        FontMetrics titleMetrics = g.getFontMetrics();
        int titleX = (width - titleMetrics.stringWidth(title)) / 2;
        g.drawString(title, titleX, 240);
    }

    /**
     * Draws a string centered on the screen.
     * @param g The graphics object.
     * @param text The text to draw.
     * @param width The width of the game.
     * @param y The y-coordinate of the text.
     */
    private void drawCenteredString(Graphics2D g, String text, int width, int y) {
        g.setFont(game.getFont());
        FontMetrics textMetrics = g.getFontMetrics();
        int textX = (width - textMetrics.stringWidth(text)) / 2;
        g.drawString(text, textX, y);
    }

    /**
     * Draws the start screen.
     * @param g The graphics object.
     */
    public void drawStartScreen(Graphics2D g) {
        int width = game.getWidth();
        int height = game.getHeight();

        drawBackground(g, width, height);

        drawTitle(g, "Fånga Bysen!", width);

        drawCenteredString(g, "Vänsterklicka för att flytta, Högerklicka för att skjuta", width, 310);
        drawCenteredString(g, "Var försiktig väsen kan befinna sig i samma rum som du", width, 345);
        drawCenteredString(g, "Klicka för att starta", width, 380);
    }

    /**
     * Sets the drawing style.
     * @param g The graphics object.
     * @param color The color to draw with.
     * @param strokeSize The size of the stroke.
     */
    private void setDrawingStyle(Graphics2D g, Color color, int strokeSize) {
        g.setColor(color);
        g.setStroke(new BasicStroke(strokeSize));
    }

    /**
     * Draws a room link.
     * @param g The graphics object.
     * @param room1 The first room.
     * @param room2 The second room.
     */
    private void drawRoomLink(Graphics2D g, Room room1, Room room2) {
        int x1 = room1.x + game.roomSize / 2;
        int y1 = room1.y + game.roomSize / 2;
        int x2 = room2.x + game.roomSize / 2;
        int y2 = room2.y + game.roomSize / 2;
        g.drawLine(x1, y1, x2, y2);
    }

    /**
     * Draws a room.
     * @param g The graphics object.
     * @param room The room to draw.
     * @param color The color to draw the room with.
     */
    private void drawRoom(Graphics2D g, Room room, Color color) {
        g.setColor(color);
        g.fillOval(room.x, room.y, game.roomSize, game.roomSize);
    }

    /**
     * Draws the links of the current room.
     * @param g The graphics object.
     */
    private void drawCurrentRoomLinks(Graphics2D g) {
        if (!game.gameOver) {
            g.setColor(Color.magenta);
            for (int link : game.links[game.currRoom])
                drawRoom(g, game.rooms[link], Color.magenta);
        }
    }

    /**
     * Draws the rooms.
     * @param g The graphics object.
     */
    public void drawRooms(Graphics2D g) {
        setDrawingStyle(g, Color.darkGray, 2);

        for (int i = 0; i < game.links.length; i++) {
            for (int link : game.links[i]) {
                Room room1 = game.rooms[i];
                Room room2 = game.rooms[link];
                drawRoomLink(g, room1, room2);
            }
        }

        setDrawingStyle(g, Color.orange, 0);
        for (Room room : game.rooms)
            drawRoom(g, room, Color.orange);

        drawCurrentRoomLinks(g);

        setDrawingStyle(g, Color.darkGray, 0);
        for (Room room : game.rooms)
            g.drawOval(room.x, room.y, game.roomSize, game.roomSize);
    }

    /**
     * Draws the number of nets remaining
     * @param g The graphics object.
     * @param numNets The number of nets remaining.
     */
    private void drawNetsRemaining(Graphics2D g, int numNets) {
        g.drawString("Nät kvar:  " + numNets, 610, 30);
    }

    /**
     * Deletes duplicate messages.
     */
    private void deleteDuplicateMessages(){
        game.setMessages(game.getMessages().stream().distinct().collect(toList()));
    }

    /**
     * Combines the first three messages into one string.
     * @param g The graphics object.
     */
    private void combineMaxThreeMessages(Graphics2D g){
        String msg = game.getMessages().stream().limit(3).collect(Collectors.joining(" & "));
        g.drawString(msg, 20, game.getHeight() - 40);
        if (game.getMessages().size() > 3) {
            g.drawString("& " + game.getMessages().get(3), 20, game.getHeight() - 17);
        }
    }

    /**
     * Draws the messages.
     * @param g The graphics object.
     */
    public void drawMessage(Graphics2D g) {
        if (!game.gameOver)
            drawNetsRemaining(g, game.numNets);

        if (game.getMessages() != null) {
            g.setColor(Color.black);

            deleteDuplicateMessages();
            combineMaxThreeMessages(g);

            game.getMessages().clear();
        }
    }
}