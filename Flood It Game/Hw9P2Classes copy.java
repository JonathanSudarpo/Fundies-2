
// libraries used
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import tester.*;

import javalib.impworld.*;
import javalib.worldimages.*;
import java.util.Random;

//represents a Cell in a FloodItGames
class Cell {

  // represents the X coordinate of the corner of a cell
  int x;
  // represents the Y coordinate of the corner of a cell
  int y;
  // represents the color of the cell
  Color color;
  // is the cell flooded or no?
  boolean flooded;
  // represents the cell that's directly to the left of the current cell
  Cell left;
  // represents the cell that's directly to the right of the current cell
  Cell right;
  // represents the cell that's directly to the top of the current cell
  Cell top;
  // represents the cell that's directly to the bottom of the current cell
  Cell bottom;
  // represents a random
  Random rand;

  // constructor which we supply all the fields/parameters
  Cell(int x, int y, Color color, boolean flooded, Cell left, Cell right, Cell top, Cell bottom,
      Random rand) {

    this.x = x;
    this.y = y;
    this.color = color;
    this.flooded = flooded;
    this.left = left;
    this.right = right;
    this.top = top;
    this.bottom = bottom;
    this.rand = rand;

  }

  // constructor with seeded random for testing
  Cell(int x, int y, Random rand) {
    this(x, y, FloodItWorld.LIST_OF_COLORS.get(rand.nextInt(FloodItWorld.COLORS)), false, null,
        null, null, null, rand);
  }

  // convenience constructor that only takes in the position of the cell
  Cell(int x, int y) {
    this(x, y, FloodItWorld.LIST_OF_COLORS.get(new Random().nextInt(FloodItWorld.COLORS)), false,
        null, null, null, null, new Random());
  }

  // the size of the cell
  static int CELL_SIZE = 50;

  // draws the current cell as a rectangle image
  public WorldImage drawCell() {
    return new RectangleImage(Cell.CELL_SIZE, Cell.CELL_SIZE, OutlineMode.SOLID, this.color);
  }

}

// represents Utils for Arrays
class ArrayUtils {

  // draws a world scene
  public WorldScene drawWorldScene(ArrayList<Cell> board, WorldScene scene) {
    for (Cell c : board) {
      scene.placeImageXY(c.drawCell(), c.x + (Cell.CELL_SIZE / 2), c.y + (Cell.CELL_SIZE / 2));
    }
    return scene;
  }

  // generates a board from the current ArrayList<Cell> with a seeded random for
  // the color
  // of the cells (for testing)
  public ArrayList<Cell> generateBoardForTest(Random seededRand) {

    int testBoardSize = 2;

    ArrayList<Cell> board = new ArrayList<Cell>();
    for (int i = 0; i < (testBoardSize * testBoardSize); i += 1) {
      board.add(new Cell((i % testBoardSize) * Cell.CELL_SIZE,
          (int) ((Math.floor(i / testBoardSize)) * Cell.CELL_SIZE), seededRand));
    }
    return board;
  }

  // generates a board from the current ArrayList<Cell> where each cell has a
  // random color
  public ArrayList<Cell> generateBoard() {

    ArrayList<Cell> board = new ArrayList<Cell>();
    for (int i = 0; i < (FloodItWorld.BOARD_SIZE * FloodItWorld.BOARD_SIZE); i += 1) {
      board.add(new Cell((i % FloodItWorld.BOARD_SIZE) * Cell.CELL_SIZE,
          (int) ((Math.floor(i / FloodItWorld.BOARD_SIZE)) * Cell.CELL_SIZE)));
    }
    return board;
  }

  // Connects all of the cells in the given ArrayList<Cell> based on their
  // position
  // in the game board. We also supply it the size of the board used for testing.
  public ArrayList<Cell> connectCells(ArrayList<Cell> board, int boardSize) {
    for (Cell c : board) {
      if (c.x == 0 && c.y == 0) {
        c.flooded = true;
        c.top = null;
        c.bottom = board.get(board.indexOf(c) + boardSize);
        c.left = null;
        c.right = board.get(board.indexOf(c) + 1);
      }
      else {

        if (c.x == 0) {
          c.left = null;
        }
        else {
          c.left = board.get(board.indexOf(c) - 1);
        }

        if (c.x != 0 && c.x % ((boardSize - 1) * Cell.CELL_SIZE) == 0) {
          c.right = null;
        }
        else {
          c.right = board.get(board.indexOf(c) + 1);
        }

        if (c.y == 0) {
          c.top = null;
        }
        else {
          c.top = board.get(board.indexOf(c) - boardSize);
        }

        if (c.y != 0 && c.y % ((boardSize - 1) * Cell.CELL_SIZE) == 0) {
          c.bottom = null;
        }
        else {
          c.bottom = board.get(board.indexOf(c) + boardSize);
        }
      }

    }

    return board;
  }

  // Checks if the whole board is the same color or not
  public boolean doWin(ArrayList<Cell> board) {
    int winningTiles = 0;
    for (Cell c : board) {
      if (c.color == board.get(0).color) {
        winningTiles++;
      }
    }

    return (winningTiles == board.size());
  }

}

// Represents examples and test for the classes of this project
class ExamplesFloodItClasses {
  // Empty Constructor
  ExamplesFloodItClasses() {
  }

  // Represents examples of cells. Cells c1-c9 are for board creation.
  // Cells firstCell, secondCell, thirdCell, and fourthCell tests the other
  // constructors and is used to test the draw method.
  Cell c1;
  Cell c2;
  Cell c3;
  Cell c4;
  Cell c5;
  Cell c6;
  Cell c7;
  Cell c8;
  Cell c9;
  Cell firstCell;
  Cell secondCell;
  Cell thirdCell;
  Cell fourthCell;

  // Represents examples of ArrayList<Cell> which in this case
  // are our boards
  ArrayList<Cell> board1;
  ArrayList<Cell> board2;
  ArrayList<Cell> board3;
  // for Convenience, we create a variables which represents a new Util object
  ArrayUtils util;
  // An example of a world Scene
  WorldScene test;

  // represents a base board
  FloodItWorld testGame;

  // initial conditions for testing
  void initConditions() {

    //  A standard game board

    this.testGame = new FloodItWorld(
        new ArrayUtils().connectCells(new ArrayUtils().generateBoard(), FloodItWorld.BOARD_SIZE));

    // to create cells, we use a set Random so the colors won't keep changing.

    // standard board
    this.test = new WorldScene(FloodItWorld.BOARD_SIZE, FloodItWorld.BOARD_SIZE);
    this.util = new ArrayUtils();
    // 2x2 board cells

    // top left
    this.c1 = new Cell(0, 0, new Random(3));
    // top right
    this.c2 = new Cell(Cell.CELL_SIZE, 0, new Random(3));
    // bottom left
    this.c3 = new Cell(0, Cell.CELL_SIZE, new Random(3));
    // bottom right
    this.c4 = new Cell(Cell.CELL_SIZE, Cell.CELL_SIZE, new Random(3));

    // 3 x 3 board cells

    // c1 is the top left
    // c4 is the center
    // c2 is the center top

    // bottom right
    this.c5 = new Cell((2 * Cell.CELL_SIZE), (2 * Cell.CELL_SIZE), new Random(3));
    // top right
    this.c6 = new Cell((2 * Cell.CELL_SIZE), 0, new Random(3));
    // bottom left
    this.c7 = new Cell(0, (2 * Cell.CELL_SIZE), new Random(3));
    // middle right
    this.c8 = new Cell((2 * Cell.CELL_SIZE), Cell.CELL_SIZE, new Random(3));
    // bottom center
    this.c9 = new Cell(Cell.CELL_SIZE, (2 * Cell.CELL_SIZE), new Random(3));

    // Cells for draw method and using other constructors.

    this.firstCell = new Cell(0, 0, Color.RED, false, null, null, null, null, new Random(2));

    this.secondCell = new Cell(Cell.CELL_SIZE, 0, Color.ORANGE, false, null, null, null, null,
        new Random(2));
    this.thirdCell = new Cell(0, Cell.CELL_SIZE, Color.BLUE, false, null, null, null, null,
        new Random(2));
    this.fourthCell = new Cell(Cell.CELL_SIZE, Cell.CELL_SIZE, Color.RED, false, null, null, null,
        null, new Random(2));

    // 2x2 board
    this.board1 = new ArrayList<Cell>(Arrays.asList(this.c1, this.c2, this.c3, this.c4));
    // 3x3 board
    this.board2 = new ArrayList<Cell>(Arrays.asList(this.c1, this.c2, this.c6, this.c3, this.c4,
        this.c8, this.c7, this.c9, this.c5));
    // empty board for testing
    this.board3 = new ArrayList<Cell>();

  }

  // The result of utilizing the connectCells method on our 2x2 board
  // (this.board1)
  void twoByTwoConnectCellsResult() {

    this.c1.right = this.c2;
    this.c1.bottom = this.c3;

    this.c2.left = this.c1;
    this.c2.bottom = this.c4;

    this.c3.top = this.c1;
    this.c3.right = this.c4;

    this.c4.top = this.c2;
    this.c4.left = this.c3;

    // assigning the empty board we previously made as a 2x2 board with cells c1 -
    // c4 connected
    this.board3 = new ArrayList<Cell>(Arrays.asList(this.c1, this.c2, this.c3, this.c4));
  }

  // The resulting scene that the 2x2 board (this.board1) creates
  void twoByTwoWorldSceneCreation() {
    this.test.placeImageXY(
        new RectangleImage(Cell.CELL_SIZE, Cell.CELL_SIZE, OutlineMode.SOLID, Color.CYAN), 25, 25);
    this.test.placeImageXY(
        new RectangleImage(Cell.CELL_SIZE, Cell.CELL_SIZE, OutlineMode.SOLID, Color.CYAN), 75, 25);
    this.test.placeImageXY(
        new RectangleImage(Cell.CELL_SIZE, Cell.CELL_SIZE, OutlineMode.SOLID, Color.CYAN), 25, 25);
    this.test.placeImageXY(
        new RectangleImage(Cell.CELL_SIZE, Cell.CELL_SIZE, OutlineMode.SOLID, Color.CYAN), 25, 25);

  }

  // The result of utilizing the connectCells method on our 3x3 board
  // (this.board2)
  void threeByThreeConnectCellsResult() {

    this.c1.right = this.c2;
    this.c1.bottom = this.c3;

    this.c2.left = this.c1;
    this.c2.bottom = this.c4;
    this.c2.right = this.c6;

    this.c3.top = this.c1;
    this.c3.right = this.c4;
    this.c3.bottom = this.c7;

    this.c4.top = this.c2;
    this.c4.left = this.c3;
    this.c4.right = this.c8;
    this.c4.bottom = this.c9;

    this.c5.top = this.c8;
    this.c5.left = this.c9;

    this.c6.left = this.c2;
    this.c6.bottom = this.c8;

    this.c7.top = this.c3;
    this.c7.right = this.c9;

    this.c8.top = this.c6;
    this.c8.left = this.c4;
    this.c8.bottom = this.c5;

    this.c9.top = this.c4;
    this.c9.left = this.c7;
    this.c9.right = this.c5;

    // assigning the empty board we previously made as a 3x3 board with cells c1 -
    // c9 connected
    this.board3 = new ArrayList<Cell>(Arrays.asList(this.c1, this.c2, this.c6, this.c3, this.c4,
        this.c8, this.c7, this.c9, this.c5));

  }

  // testing the connectCells method on a 2x2 board
  void testConnectCellsMethod(Tester t) {
    // initial conditions as well as tests for it
    this.initConditions();

    t.checkExpect(this.c1, new Cell(0, 0, new Random(3)));
    t.checkExpect(this.c2, new Cell(Cell.CELL_SIZE, 0, new Random(3)));
    t.checkExpect(this.c3, new Cell(0, Cell.CELL_SIZE, new Random(3)));
    t.checkExpect(this.c4, new Cell(Cell.CELL_SIZE, Cell.CELL_SIZE, new Random(3)));

    t.checkExpect(this.board1,
        new ArrayList<Cell>(Arrays.asList(this.c1, this.c2, this.c3, this.c4)));
    t.checkExpect(this.board1.size(), 4);
    t.checkExpect(this.board3, new ArrayList<Cell>());
    t.checkExpect(this.board3.size(), 0);

    t.checkExpect(this.c1.top, null);
    t.checkExpect(this.c1.bottom, null);
    t.checkExpect(this.c1.right, null);
    t.checkExpect(this.c1.left, null);

    t.checkExpect(this.c2.top, null);
    t.checkExpect(this.c2.bottom, null);
    t.checkExpect(this.c2.right, null);
    t.checkExpect(this.c2.left, null);

    t.checkExpect(this.c3.top, null);
    t.checkExpect(this.c3.bottom, null);
    t.checkExpect(this.c3.right, null);
    t.checkExpect(this.c3.left, null);

    t.checkExpect(this.c4.top, null);
    t.checkExpect(this.c4.bottom, null);
    t.checkExpect(this.c4.right, null);
    t.checkExpect(this.c4.left, null);

    // testing the method
    this.twoByTwoConnectCellsResult();

    t.checkExpect(this.c1.right, this.c2);
    t.checkExpect(this.c1.bottom, this.c3);
    t.checkExpect(this.c1.left, null);
    t.checkExpect(this.c1.top, null);

    t.checkExpect(this.c2.right, null);
    t.checkExpect(this.c2.top, null);
    t.checkExpect(this.c2.left, this.c1);
    t.checkExpect(this.c2.bottom, this.c4);

    t.checkExpect(this.c3.top, this.c1);
    t.checkExpect(this.c3.right, this.c4);
    t.checkExpect(this.c3.left, null);
    t.checkExpect(this.c3.bottom, null);

    t.checkExpect(this.c4.top, this.c2);
    t.checkExpect(this.c4.left, this.c3);
    t.checkExpect(this.c4.bottom, null);
    t.checkExpect(this.c4.right, null);

    t.checkExpect(this.board3.size(), 4);
    t.checkExpect(this.board3,
        new ArrayList<Cell>(Arrays.asList(this.c1, this.c2, this.c3, this.c4)));
    t.checkExpect(this.util.connectCells(this.board1, 2), this.board3);

  }

  // testing the connectCells method on a 3x3 board
  void testThreeConnectCellsMethod(Tester t) {
    // initial conditions as well as tests for it
    this.initConditions();

    t.checkExpect(this.c1, new Cell(0, 0, new Random(3)));
    t.checkExpect(this.c2, new Cell(Cell.CELL_SIZE, 0, new Random(3)));
    t.checkExpect(this.c3, new Cell(0, Cell.CELL_SIZE, new Random(3)));
    t.checkExpect(this.c4, new Cell(Cell.CELL_SIZE, Cell.CELL_SIZE, new Random(3)));
    t.checkExpect(this.c5, new Cell((2 * Cell.CELL_SIZE), (2 * Cell.CELL_SIZE), new Random(3)));
    t.checkExpect(this.c6, new Cell((2 * Cell.CELL_SIZE), 0, new Random(3)));
    t.checkExpect(this.c7, new Cell(0, (2 * Cell.CELL_SIZE), new Random(3)));
    t.checkExpect(this.c8, new Cell((2 * Cell.CELL_SIZE), Cell.CELL_SIZE, new Random(3)));
    t.checkExpect(this.c9, new Cell(Cell.CELL_SIZE, (2 * Cell.CELL_SIZE), new Random(3)));

    t.checkExpect(this.board2, new ArrayList<Cell>(Arrays.asList(this.c1, this.c2, this.c6, this.c3,
        this.c4, this.c8, this.c7, this.c9, this.c5)));
    t.checkExpect(this.board2.size(), 9);
    t.checkExpect(this.board3, new ArrayList<Cell>());
    t.checkExpect(this.board3.size(), 0);

    t.checkExpect(this.c1.top, null);
    t.checkExpect(this.c1.bottom, null);
    t.checkExpect(this.c1.right, null);
    t.checkExpect(this.c1.left, null);

    t.checkExpect(this.c2.top, null);
    t.checkExpect(this.c2.bottom, null);
    t.checkExpect(this.c2.right, null);
    t.checkExpect(this.c2.left, null);

    t.checkExpect(this.c3.top, null);
    t.checkExpect(this.c3.bottom, null);
    t.checkExpect(this.c3.right, null);
    t.checkExpect(this.c3.left, null);

    t.checkExpect(this.c4.top, null);
    t.checkExpect(this.c4.bottom, null);
    t.checkExpect(this.c4.right, null);
    t.checkExpect(this.c4.left, null);

    t.checkExpect(this.c5.top, null);
    t.checkExpect(this.c5.bottom, null);
    t.checkExpect(this.c5.right, null);
    t.checkExpect(this.c5.left, null);

    t.checkExpect(this.c6.top, null);
    t.checkExpect(this.c6.bottom, null);
    t.checkExpect(this.c6.right, null);
    t.checkExpect(this.c6.left, null);

    t.checkExpect(this.c7.top, null);
    t.checkExpect(this.c7.bottom, null);
    t.checkExpect(this.c7.right, null);
    t.checkExpect(this.c7.left, null);

    t.checkExpect(this.c8.top, null);
    t.checkExpect(this.c8.bottom, null);
    t.checkExpect(this.c8.right, null);
    t.checkExpect(this.c8.left, null);

    t.checkExpect(this.c9.top, null);
    t.checkExpect(this.c9.bottom, null);
    t.checkExpect(this.c9.right, null);
    t.checkExpect(this.c9.left, null);

    this.threeByThreeConnectCellsResult();

    t.checkExpect(this.c1.top, null);
    t.checkExpect(this.c1.bottom, this.c3);
    t.checkExpect(this.c1.right, this.c2);
    t.checkExpect(this.c1.left, null);

    t.checkExpect(this.c2.top, null);
    t.checkExpect(this.c2.bottom, this.c4);
    t.checkExpect(this.c2.right, this.c6);
    t.checkExpect(this.c2.left, this.c1);

    t.checkExpect(this.c3.top, this.c1);
    t.checkExpect(this.c3.bottom, this.c7);
    t.checkExpect(this.c3.right, this.c4);
    t.checkExpect(this.c3.left, null);

    t.checkExpect(this.c4.top, this.c2);
    t.checkExpect(this.c4.bottom, this.c9);
    t.checkExpect(this.c4.right, this.c8);
    t.checkExpect(this.c4.left, this.c3);

    t.checkExpect(this.c5.top, this.c8);
    t.checkExpect(this.c5.bottom, null);
    t.checkExpect(this.c5.right, null);
    t.checkExpect(this.c5.left, this.c9);

    t.checkExpect(this.c6.top, null);
    t.checkExpect(this.c6.bottom, this.c8);
    t.checkExpect(this.c6.right, null);
    t.checkExpect(this.c6.left, this.c2);

    t.checkExpect(this.c7.top, this.c3);
    t.checkExpect(this.c7.bottom, null);
    t.checkExpect(this.c7.right, this.c9);
    t.checkExpect(this.c7.left, null);

    t.checkExpect(this.c8.top, this.c6);
    t.checkExpect(this.c8.bottom, this.c5);
    t.checkExpect(this.c8.right, null);
    t.checkExpect(this.c8.left, this.c4);

    t.checkExpect(this.c9.top, this.c4);
    t.checkExpect(this.c9.bottom, null);
    t.checkExpect(this.c9.right, this.c5);
    t.checkExpect(this.c9.left, this.c7);

    t.checkExpect(this.board3.size(), 9);
    t.checkExpect(this.board3, new ArrayList<Cell>(Arrays.asList(this.c1, this.c2, this.c6, this.c3,
        this.c4, this.c8, this.c7, this.c9, this.c5)));
    t.checkExpect(this.util.connectCells(this.board2, 3), this.board3);
  }

  // testing the drawWorldScene method
  void testDrawWorldScene(Tester t) {
    this.initConditions();
    t.checkExpect(this.c1, new Cell(0, 0, new Random(3)));
    t.checkExpect(this.c2, new Cell(Cell.CELL_SIZE, 0, new Random(3)));
    t.checkExpect(this.c3, new Cell(0, Cell.CELL_SIZE, new Random(3)));
    t.checkExpect(this.c4, new Cell(Cell.CELL_SIZE, Cell.CELL_SIZE, new Random(3)));
    t.checkExpect(this.c5, new Cell((2 * Cell.CELL_SIZE), (2 * Cell.CELL_SIZE), new Random(3)));
    t.checkExpect(this.c6, new Cell((2 * Cell.CELL_SIZE), 0, new Random(3)));
    t.checkExpect(this.c7, new Cell(0, (2 * Cell.CELL_SIZE), new Random(3)));
    t.checkExpect(this.c8, new Cell((2 * Cell.CELL_SIZE), Cell.CELL_SIZE, new Random(3)));
    t.checkExpect(this.c9, new Cell(Cell.CELL_SIZE, (2 * Cell.CELL_SIZE), new Random(3)));

    t.checkExpect(this.board3, new ArrayList<Cell>());
    t.checkExpect(this.board3.size(), 0);

    t.checkExpect(this.board1,
        new ArrayList<Cell>(Arrays.asList(this.c1, this.c2, this.c3, this.c4)));
    t.checkExpect(this.board1.size(), 4);

    t.checkExpect(this.test, new WorldScene(FloodItWorld.BOARD_SIZE, FloodItWorld.BOARD_SIZE));

    // drawing the worldScene of an empty board
    t.checkExpect(
        this.util.drawWorldScene(this.board3,
            new WorldScene(FloodItWorld.BOARD_SIZE, FloodItWorld.BOARD_SIZE)),
        new WorldScene(FloodItWorld.BOARD_SIZE, FloodItWorld.BOARD_SIZE));

    // creates the two by two scene based on this.board1
    this.twoByTwoWorldSceneCreation();

    t.checkExpect(this.util.drawWorldScene(this.board1,
        new WorldScene(FloodItWorld.BOARD_SIZE, FloodItWorld.BOARD_SIZE)), this.test);

  }

  // testing the MakeScene method
  void testMakeScene(Tester t) {
    initConditions();
    t.checkExpect(this.c1, new Cell(0, 0, new Random(3)));
    t.checkExpect(this.c2, new Cell(Cell.CELL_SIZE, 0, new Random(3)));
    t.checkExpect(this.c3, new Cell(0, Cell.CELL_SIZE, new Random(3)));
    t.checkExpect(this.c4, new Cell(Cell.CELL_SIZE, Cell.CELL_SIZE, new Random(3)));
    t.checkExpect(this.c5, new Cell((2 * Cell.CELL_SIZE), (2 * Cell.CELL_SIZE), new Random(3)));
    t.checkExpect(this.c6, new Cell((2 * Cell.CELL_SIZE), 0, new Random(3)));
    t.checkExpect(this.c7, new Cell(0, (2 * Cell.CELL_SIZE), new Random(3)));
    t.checkExpect(this.c8, new Cell((2 * Cell.CELL_SIZE), Cell.CELL_SIZE, new Random(3)));
    t.checkExpect(this.c9, new Cell(Cell.CELL_SIZE, (2 * Cell.CELL_SIZE), new Random(3)));

    t.checkExpect(this.board3, new ArrayList<Cell>());
    t.checkExpect(this.board3.size(), 0);

    t.checkExpect(this.board1,
        new ArrayList<Cell>(Arrays.asList(this.c1, this.c2, this.c3, this.c4)));
    t.checkExpect(this.board1.size(), 4);

    t.checkExpect(this.test, new WorldScene(FloodItWorld.BOARD_SIZE, FloodItWorld.BOARD_SIZE));

    // testing MakeScene on an empty array
    FloodItWorld game3 = new FloodItWorld(this.board3);
    t.checkExpect(game3.makeScene(),
        new WorldScene(FloodItWorld.BOARD_SIZE, FloodItWorld.BOARD_SIZE));

    // setting a local variable to represent a new FloodItWorld with this.board1
    // as it's board
    FloodItWorld game = new FloodItWorld(this.board1);

    // creates a 2x2 scene based on this.board1
    this.twoByTwoWorldSceneCreation();
    t.checkExpect(game.makeScene(), this.test);
  }

  // testing the drawCell method
  void testDrawCellMethod(Tester t) {
    initConditions();

    t.checkExpect(this.firstCell,
        new Cell(0, 0, Color.RED, false, null, null, null, null, new Random(2)));
    t.checkExpect(this.secondCell,
        new Cell(50, 0, Color.ORANGE, false, null, null, null, null, new Random(2)));
    t.checkExpect(this.thirdCell,
        new Cell(0, 50, Color.BLUE, false, null, null, null, null, new Random(2)));
    t.checkExpect(this.fourthCell,
        new Cell(50, 50, Color.RED, false, null, null, null, null, new Random(2)));

    t.checkExpect(this.firstCell.drawCell(),
        new RectangleImage(Cell.CELL_SIZE, Cell.CELL_SIZE, OutlineMode.SOLID, Color.RED));
    t.checkExpect(this.secondCell.drawCell(),
        new RectangleImage(Cell.CELL_SIZE, Cell.CELL_SIZE, OutlineMode.SOLID, Color.ORANGE));
    t.checkExpect(this.thirdCell.drawCell(),
        new RectangleImage(Cell.CELL_SIZE, Cell.CELL_SIZE, OutlineMode.SOLID, Color.BLUE));
    t.checkExpect(this.fourthCell.drawCell(),
        new RectangleImage(Cell.CELL_SIZE, Cell.CELL_SIZE, OutlineMode.SOLID, Color.RED));
  }

  // testing the GenerateBoard Method
  void testGenerateBoardMethod(Tester t) {
    this.initConditions();
    t.checkExpect(this.c1, new Cell(0, 0, new Random(3)));
    t.checkExpect(this.c2, new Cell(Cell.CELL_SIZE, 0, new Random(3)));
    t.checkExpect(this.c3, new Cell(0, Cell.CELL_SIZE, new Random(3)));
    t.checkExpect(this.c4, new Cell(Cell.CELL_SIZE, Cell.CELL_SIZE, new Random(3)));

    t.checkExpect(this.board1,
        new ArrayList<Cell>(Arrays.asList(this.c1, this.c2, this.c3, this.c4)));
    t.checkExpect(this.board1.size(), 4);
    t.checkExpect(this.board3, new ArrayList<Cell>());
    t.checkExpect(this.board3.size(), 0);

    // because we give it a different random, let's reset the colors
    // of Cell c3 and c4 to known colors which is set by the random.
    this.c3.color = Color.BLACK;
    this.c4.color = Color.DARK_GRAY;

    this.board1 = new ArrayList<Cell>(Arrays.asList(this.c1, this.c2, this.c3, this.c4));
    t.checkExpect(this.board1.size(), 4);
    t.checkExpect(this.board1,
        new ArrayList<Cell>(Arrays.asList(this.c1, this.c2, this.c3, this.c4)));

  }

  // tests the doWin method
  void testDoWinMethod(Tester t) {
    this.initConditions();

    // test necessary init conditions
    t.checkExpect(this.c1.color, Color.CYAN);
    t.checkExpect(this.c2.color, Color.CYAN);
    t.checkExpect(this.c3.color, Color.CYAN);
    t.checkExpect(this.c4.color, Color.CYAN);
    t.checkExpect(this.c5.color, Color.CYAN);
    t.checkExpect(this.c6.color, Color.CYAN);
    t.checkExpect(this.c7.color, Color.CYAN);

    t.checkExpect(this.c1.flooded, false);
    t.checkExpect(this.c2.flooded, false);
    t.checkExpect(this.c3.flooded, false);
    t.checkExpect(this.c4.flooded, false);
    t.checkExpect(this.c5.flooded, false);
    t.checkExpect(this.c6.flooded, false);
    t.checkExpect(this.c7.flooded, false);

    // changes some of the cell's colors
    this.c5.color = Color.WHITE;
    this.c6.color = Color.YELLOW;
    this.c7.color = Color.GREEN;

    // checks that the colors have been changed
    t.checkExpect(this.c5.color, Color.WHITE);
    t.checkExpect(this.c6.color, Color.YELLOW);
    t.checkExpect(this.c7.color, Color.GREEN);

    // makes all the cells flooded
    this.c1.flooded = true;
    this.c2.flooded = true;
    this.c3.flooded = true;
    this.c4.flooded = true;
    this.c5.flooded = true;
    this.c6.flooded = true;
    this.c7.flooded = true;

    // checks that they've been successfully mutated as flooded
    t.checkExpect(this.c1.flooded, true);
    t.checkExpect(this.c2.flooded, true);
    t.checkExpect(this.c3.flooded, true);
    t.checkExpect(this.c4.flooded, true);
    t.checkExpect(this.c5.flooded, true);
    t.checkExpect(this.c6.flooded, true);
    t.checkExpect(this.c7.flooded, true);

    // represents a board where all the cells have the same color
    ArrayList<Cell> test = new ArrayList<Cell>(Arrays.asList(this.c1, this.c2, this.c3, this.c4));
    // represents a board where all but one cells have the same color
    ArrayList<Cell> testallButOne = new ArrayList<Cell>(
        Arrays.asList(this.c1, this.c2, this.c3, this.c5));
    // represents a board where all cells have different colors.
    ArrayList<Cell> testFalse = new ArrayList<Cell>(
        Arrays.asList(this.c1, this.c5, this.c6, this.c7));

    t.checkExpect(this.util.doWin(test), true);
    t.checkExpect(this.util.doWin(testFalse), false);
    t.checkExpect(this.util.doWin(testallButOne), false);

  }

  // test onKeyEvent Method
  void testOnKeyEventMethod(Tester t) {
    this.initConditions();
    t.checkExpect(this.board1,
        new ArrayList<Cell>(Arrays.asList(this.c1, this.c2, this.c3, this.c4)));
    t.checkExpect(this.board1.size(), 4);

    FloodItWorld game = new FloodItWorld(this.board1);

    // if we press a key that isn't "r"
    game.onKeyEventForTest("y");

    // it should just return the same board (do nothing)
    t.checkExpect(game.board, this.board1);

    // if we press "r"
    game.onKeyEventForTest("r");

    // then the board changes to a new instance of the board
    t.checkExpect(game.board.equals(this.board1), false);
    t.checkExpect(game.guesses, 14);
    t.checkExpect(game.board,
        new ArrayUtils().connectCells(new ArrayUtils().generateBoardForTest(new Random(3)), 2));

  }

  //test winScene and loseScene and worldEnds and endGame
  void testWinLoseSceneWorldEnds(Tester t) {
    initConditions();
    WorldScene winExpected = new WorldScene(FloodItWorld.BOARD_SIZE * Cell.CELL_SIZE,
        FloodItWorld.BOARD_SIZE * Cell.CELL_SIZE);
    winExpected.placeImageXY(new TextImage("YOU WIN", 20, Color.BLACK),
        FloodItWorld.BOARD_SIZE * Cell.CELL_SIZE / 2, FloodItWorld.BOARD_SIZE * Cell.CELL_SIZE / 2);

    t.checkExpect(this.testGame.winScene(), winExpected);

    WorldScene loseExpected = new WorldScene(FloodItWorld.BOARD_SIZE * Cell.CELL_SIZE,
        FloodItWorld.BOARD_SIZE * Cell.CELL_SIZE);
    loseExpected.placeImageXY(new TextImage("YOU LOSE", 20, Color.BLACK),
        FloodItWorld.BOARD_SIZE * Cell.CELL_SIZE / 2, FloodItWorld.BOARD_SIZE * Cell.CELL_SIZE / 2);

    t.checkExpect(this.testGame.loseScene(), loseExpected);

    this.testGame.win = 1;
    t.checkExpect(this.testGame.worldEnds(), new WorldEnd(true, winExpected));

    this.testGame.win = 2;
    t.checkExpect(this.testGame.worldEnds(), new WorldEnd(true, loseExpected));

    this.testGame.win = 0;
    this.testGame.endGame();
    t.checkExpect(this.testGame.win, 0);

    this.testGame.board = this.board1;

    t.checkExpect(this.testGame.win, 0);
    this.testGame.endGame();
    t.checkExpect(this.testGame.win, 1);
  }

  // tests mousePressed
  void testMousePressed(Tester t) {
    this.initConditions();

    t.checkExpect(this.testGame.win, 0);
    t.checkExpect(this.testGame.guesses,
        (int) Math.ceil((FloodItWorld.BOARD_SIZE * FloodItWorld.COLORS) / 3.5));

    this.testGame.onMousePressed(new Posn(2, 3));

    t.checkExpect(this.testGame.win, 0);
    t.checkExpect(this.testGame.guesses,
        (int) Math.ceil((FloodItWorld.BOARD_SIZE * FloodItWorld.COLORS) / 3.5));

    this.testGame.guesses = 0;

    this.testGame.onMousePressed(new Posn(2, 3));

    t.checkExpect(this.testGame.win, 2);
  }

  //tests mouseClicked
  void testMouseClicked(Tester t) {
    initConditions();
    this.testGame.board = board1;
    t.checkExpect(this.testGame.guesses, 14);

    this.testGame.board.get(0).color = Color.RED;
    t.checkExpect(this.testGame.board.get(0).color, Color.RED);

    this.testGame.onMouseClicked(new Posn(52, 52));
    t.checkExpect(this.testGame.board.get(0).color, Color.CYAN);
    t.checkExpect(this.testGame.guesses, 13);

  }

  //tests onTick
  void testOnTick(Tester t) {
    FloodItWorld game = new FloodItWorld(
        new ArrayUtils().connectCells(new ArrayUtils().generateBoardForTest(new Random(3)), 2));

    FloodItWorld game2 = new FloodItWorld(
        new ArrayUtils().connectCells(new ArrayUtils().generateBoardForTest(new Random(3)), 2));

    t.checkExpect(game, game);

    game.onTick();
    game2.onTick();

    t.checkExpect(game, game2);
  }

  //test flood function
  void testFloodFunction(Tester t) {
    initConditions();
    this.testGame.board = new ArrayUtils().connectCells(board1, 2);
    this.testGame.board.get(2).color = Color.RED;

    t.checkExpect(this.testGame.board.get(1).flooded, false);
    t.checkExpect(this.testGame.board.get(2).flooded, false);

    this.testGame.floodFunction();

    t.checkExpect(this.testGame.board.get(1).flooded, true);
    t.checkExpect(this.testGame.board.get(2).flooded, false);
  }

  // test surroundingFlood Method
  void testSurroundingFloodMethod(Tester t) {
    this.initConditions();

    // test initial conditions
    t.checkExpect(this.c1.color, Color.CYAN);
    t.checkExpect(this.c2.color, Color.CYAN);
    t.checkExpect(this.c3.color, Color.CYAN);
    t.checkExpect(this.c4.color, Color.CYAN);

    t.checkExpect(this.c1.flooded, false);
    t.checkExpect(this.c2.flooded, false);
    t.checkExpect(this.c3.flooded, false);
    t.checkExpect(this.c4.flooded, false);

    this.c1.flooded = true;
    this.c2.flooded = true;
    this.c3.flooded = true;

    t.checkExpect(this.c1.flooded, true);
    t.checkExpect(this.c2.flooded, true);
    t.checkExpect(this.c3.flooded, true);
    t.checkExpect(this.c4.flooded, false);

    this.c2.color = Color.WHITE;
    this.c3.color = Color.YELLOW;

    t.checkExpect(this.c1.color, Color.CYAN);
    t.checkExpect(this.c2.color, Color.WHITE);
    t.checkExpect(this.c3.color, Color.YELLOW);
    t.checkExpect(this.c4.color, Color.CYAN);

    this.testGame.board = new ArrayList<Cell>(Arrays.asList(this.c1, this.c2, this.c3, this.c4));

    // same color as first tile and flooded
    t.checkExpect(this.testGame.surroundingFlood(this.c1), false);
    // not same color as first time, and flooded
    t.checkExpect(this.testGame.surroundingFlood(this.c2), true);
    // not same color as first time, and flooded
    t.checkExpect(this.testGame.surroundingFlood(this.c3), true);
    // same color as first tile, but not flooded
    t.checkExpect(this.testGame.surroundingFlood(this.c4), false);
    // same color as first tile, and flooded
    t.checkExpect(this.testGame.surroundingFlood(this.c5), false);
  }

  // tests the changeColor method
  void testChangeColorMethod(Tester t) {
    this.initConditions();

    // test initial conditions
    t.checkExpect(this.c1.color, Color.CYAN);
    t.checkExpect(this.c2.color, Color.CYAN);
    t.checkExpect(this.c3.color, Color.CYAN);
    t.checkExpect(this.c4.color, Color.CYAN);

    t.checkExpect(this.c1.flooded, false);
    t.checkExpect(this.c2.flooded, false);
    t.checkExpect(this.c3.flooded, false);
    t.checkExpect(this.c4.flooded, false);

    // testing when everything is same color, and not flooded
    // so the output is just the same thing
    this.testGame.stack = new ArrayList<Cell>(Arrays.asList(this.c1, this.c2));

    this.util.connectCells(this.testGame.board, 2);
    this.testGame.changeColor();

    t.checkExpect(this.testGame.stack, new ArrayList<Cell>(Arrays.asList(this.c1, this.c2)));

    // now we connect the cells, and make an actual game board

    this.twoByTwoConnectCellsResult();

    // flood some of the cells
    this.c1.flooded = true;
    this.c2.flooded = true;
    this.c3.flooded = true;

    t.checkExpect(this.c1.flooded, true);
    t.checkExpect(this.c2.flooded, true);
    t.checkExpect(this.c3.flooded, true);
    t.checkExpect(this.c4.flooded, false);

    // change the colors
    this.c2.color = Color.white;
    this.c3.color = Color.black;

    FloodItWorld game = new FloodItWorld(this.board1);

    t.checkExpect(this.c1.color, Color.CYAN);
    t.checkExpect(this.c2.color, Color.white);
    t.checkExpect(this.c3.color, Color.black);
    t.checkExpect(this.c4.color, Color.CYAN);
    t.checkExpect(game.stack, new ArrayList<Cell>());

    // new game board, we run the changeColor method
    game.stack = new ArrayList<Cell>(Arrays.asList(this.c1));
    game.changeColor();

    // it outputs the changed cells, in the order it was iterated on
    // notice how c4 is not here because it is not flooded!
    t.checkExpect(game.stack, new ArrayList<Cell>(Arrays.asList(this.c1, this.c3, this.c2)));

  }

}