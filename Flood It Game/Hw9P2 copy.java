
// libraries used
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import javalib.impworld.*;
import javalib.worldimages.*;
import tester.Tester;

// represents a FloodItWorld game
class FloodItWorld extends World {
  // represents the board of the game
  ArrayList<Cell> board;
  ArrayList<Cell> stack;
  int guesses;
  int win;

  // constructor
  FloodItWorld(ArrayList<Cell> board) {
    this.board = board;
    this.stack = new ArrayList<Cell>();
    this.guesses = (int) Math.ceil((BOARD_SIZE * COLORS) / 3.5);
    this.win = 0;
  }

  // the size of the board (how wide or long it is)
  public static int BOARD_SIZE = 8;
  // the number of colors we want the board to have
  public static int COLORS = 6;
  // the list of all possible colors
  public static ArrayList<Color> LIST_OF_COLORS = new ArrayList<Color>(Arrays.asList(Color.BLACK,
      Color.BLUE, Color.CYAN, Color.DARK_GRAY, Color.GRAY, Color.GREEN, Color.LIGHT_GRAY,
      Color.MAGENTA, Color.ORANGE, Color.PINK, Color.RED, Color.WHITE, Color.YELLOW));

  @Override
  public void onTick() {
    floodFunction();
    changeColor();
    endGame();

  }

  // checks if the player has won the game
  public void endGame() {
    if (new ArrayUtils().doWin(board)) {
      this.win = 1;
    }
  }

  // checks if the cell given is not null, and flooded, and a different
  // color from the top left cell of the board
  public boolean surroundingFlood(Cell c) {
    return (c != null) && c.flooded && (c.color != this.board.get(0).color);
  }

  // If a tile on the board is flooded, not null, but is a 
  // different color from the top left cell, change the cell's
  // color to the top-left cell's color.
  public void changeColor() {
    ArrayList<Cell> stackv2 = new ArrayList<Cell>(this.stack);
    for (Cell c : stackv2) {
      if (this.surroundingFlood(c.top)) {
        c.top.color = this.board.get(0).color;
        this.stack.add(c.top);

      }
      if (this.surroundingFlood(c.bottom)) {
        c.bottom.color = this.board.get(0).color;
        this.stack.add(c.bottom);

      }
      if (this.surroundingFlood(c.right)) {
        c.right.color = this.board.get(0).color;
        this.stack.add(c.right);
      }
      if (this.surroundingFlood(c.left)) {
        c.left.color = this.board.get(0).color;
        this.stack.add(c.left);
      }

    }

  }

  // If a cell on the board is not flooded, but is adjacent to a flooded cell and is the same
  // color as the first cell (top left cell), then make the cell flooded.
  public void floodFunction() {
    for (Cell c : this.board) {
      if (!c.flooded) {
        if ((c.top != null && c.top.flooded) || (c.bottom != null && c.bottom.flooded)
            || (c.right != null && c.right.flooded) || (c.left != null && c.left.flooded)) {
          if (c.color == this.board.get(0).color) {
            c.flooded = true;

          }
        }
      }
    }

  }

  // When the mouse clicks a cell, the first cell on the board (top left)
  // gets turned to the cell's color, the number of guesses is reduced by one 
  // (if the color is different), and a new stack is initiated with only 
  // having the top left cell.
  @Override
  public void onMouseClicked(Posn pos) {

    this.stack = new ArrayList<Cell>(Arrays.asList(this.board.get(0)));

    if (pos.x <= FloodItWorld.BOARD_SIZE * Cell.CELL_SIZE
        && pos.y <= FloodItWorld.BOARD_SIZE * Cell.CELL_SIZE && pos.x >= 0 && pos.y >= 0) {

      int xLocation = (int) (Cell.CELL_SIZE * Math.floor(pos.x / Cell.CELL_SIZE));
      int yLocation = (int) (Cell.CELL_SIZE * Math.floor(pos.y / Cell.CELL_SIZE));

      for (Cell c : this.board) {
        if (c.x == xLocation && c.y == yLocation) {

          this.board.get(0).color = c.color;
          this.guesses -= 1;

        }
      }
    }
  }

  @Override
  // once they player runs out of guesses, once they click, they lose
  public void onMousePressed(Posn posn) {
    if (this.guesses == 0) {
      this.win = 2;
    }
  }

  // creates a worldScene based on the given board
  @Override
  public WorldScene makeScene() {
    WorldScene background = new WorldScene(FloodItWorld.BOARD_SIZE, FloodItWorld.BOARD_SIZE);
    new ArrayUtils().drawWorldScene(this.board, background);
    WorldImage text = new OverlayImage(
        new TextImage(Integer.toString(this.guesses), 20, Color.WHITE),
        new CircleImage(15, OutlineMode.SOLID, Color.BLACK));
    background.placeImageXY(text, 25, 25);
    return background;
  }

  //If the player presses the "r" key, then it resets the board
  // to a newly randomized board.
  public void onKeyEvent(String key) {
    if (key.equals("r")) {
      this.guesses = (int) Math.ceil((BOARD_SIZE * COLORS) / 3.5);
      this.board = new ArrayUtils().connectCells(new ArrayUtils().generateBoard(), BOARD_SIZE);
    }
  }

  // a remade onKeyEvent method just with a seeded random board.
  public void onKeyEventForTest(String key) {
    if (key.equals("r")) {
      this.guesses = (int) Math.ceil((BOARD_SIZE * COLORS) / 3.5);
      this.board = new ArrayUtils()
          .connectCells(new ArrayUtils().generateBoardForTest(new Random(3)), 2);
    }
  }

  // Produces a new scene which notifies the player that they have lost.
  public WorldScene loseScene() {
    WorldScene background = new WorldScene(FloodItWorld.BOARD_SIZE * Cell.CELL_SIZE,
        FloodItWorld.BOARD_SIZE * Cell.CELL_SIZE);
    background.placeImageXY(new TextImage("YOU LOSE", 20, Color.BLACK),
        FloodItWorld.BOARD_SIZE * Cell.CELL_SIZE / 2, FloodItWorld.BOARD_SIZE * Cell.CELL_SIZE / 2);
    return background;

  }

  // Produces a new scene which notifies the player that they have won.
  public WorldScene winScene() {
    WorldScene background = new WorldScene(FloodItWorld.BOARD_SIZE * Cell.CELL_SIZE,
        FloodItWorld.BOARD_SIZE * Cell.CELL_SIZE);
    background.placeImageXY(new TextImage("YOU WIN", 20, Color.BLACK),
        FloodItWorld.BOARD_SIZE * Cell.CELL_SIZE / 2, FloodItWorld.BOARD_SIZE * Cell.CELL_SIZE / 2);
    return background;

  }

  // Determines which end scene to produce
  @Override
  public WorldEnd worldEnds() {
    if (this.win == 1) {
      return new WorldEnd(true, winScene());
    }
    else if (this.win == 2) {
      return new WorldEnd(true, loseScene());
    }
    else {
      return new WorldEnd(false, winScene());
    }
  }

}

// Examples of world creation, the tests for our methods
// are in the other file!
class ExamplesGames {
  // empty constructor
  ExamplesGames() {
  }

  // runs the FloodItWorld game
  void testGame(Tester t) {
    FloodItWorld game = new FloodItWorld(
        new ArrayUtils().connectCells(new ArrayUtils().generateBoard(), FloodItWorld.BOARD_SIZE));
    game.bigBang(FloodItWorld.BOARD_SIZE * Cell.CELL_SIZE, FloodItWorld.BOARD_SIZE * Cell.CELL_SIZE,
        0.1);
  }
}