
// Libraries used
import java.util.*;

import tester.*;
import javalib.impworld.*;

import java.awt.Color;
import javalib.worldimages.*;

// represents a Cell in the board
class Cell {
  // represents the top left X-Pos of the Cell
  int xPos;
  // represents the top left Y-Pos of the cell
  int yPos;
  // represents the top edge of the cell
  Edge topEdge;
  // represents the bottom edge of the cell
  Edge botEdge;
  // represents the right edge of the cell
  Edge rightEdge;
  // represents the left edge of the cell
  Edge leftEdge;
  int set;

  Color color;

  // constructor
  Cell(int xPos, int yPos, Edge topEdge, Edge botEdge, Edge rightEdge, Edge leftEdge, int set,
      Color color) {

    this.xPos = xPos;
    this.yPos = yPos;
    this.topEdge = topEdge;
    this.botEdge = botEdge;
    this.rightEdge = rightEdge;
    this.leftEdge = leftEdge;
    this.set = set;
    this.color = color;
  }

  // convenience constructor
  Cell(int xPos, int yPos, int set) {
    this(xPos, yPos, null, null, null, null, set, Cell.CELL_COLOR);
  }

  // Cell Size, Cell Color, and Edge Color
  static int CELL_SIZE = 20;
  static Color CELL_COLOR = Color.GRAY;
  static Color WALL_COLOR = Color.BLACK;

  // Draws the top and right edge of a cell. If any edge is not solid, then we
  // don't draw the edge.
  WorldImage drawCell() {
    WorldImage cell = new RectangleImage(Cell.CELL_SIZE, Cell.CELL_SIZE, OutlineMode.SOLID,
        this.color);
    WorldImage horLine = new RectangleImage(Cell.CELL_SIZE, 1, OutlineMode.SOLID, Cell.WALL_COLOR);
    WorldImage vertLine = new RectangleImage(1, Cell.CELL_SIZE, OutlineMode.SOLID, Cell.WALL_COLOR);

    WorldImage result = cell;

    // this is where we determine if we draw a cell's edge or not
    if (this.topEdge != null && this.topEdge.solid) {
      result = new OverlayOffsetImage(horLine, 0, Cell.CELL_SIZE / 2, result);
    }

    if (this.rightEdge != null && this.rightEdge.solid) {
      result = new OverlayOffsetImage(vertLine, -Cell.CELL_SIZE / 2, 0, result);
    }

    return result;
  }

  ArrayList<Edge> listifyEdges() {
    ArrayList<Edge> result = new ArrayList<Edge>();

    if (this.topEdge != null && !this.topEdge.solid) {
      result.add(topEdge);
    }

    if (this.botEdge != null && !this.botEdge.solid) {
      result.add(botEdge);
    }

    if (this.rightEdge != null && !this.rightEdge.solid) {
      result.add(rightEdge);
    }

    if (this.leftEdge != null && !this.leftEdge.solid) {
      result.add(leftEdge);
    }

    return result;
  }

  boolean isANeighbor(Cell c) {
    for (Edge e : c.listifyEdges()) {
      if (this == e.startCell || this == e.endCell) {
        return true;
      }
    }

    return false;
  }
}

// represents the edge of a Cell (the walls of the Cell).
class Edge {
  // the Cell that is to the left, or above the Edge
  Cell startCell;
  // the cell that is to the right, or below the Edge.
  Cell endCell;
  // the weight of the Edge
  int weight;
  // whether or not the Edge is solid (drawn).
  boolean solid;

  // Constructor
  Edge(Cell startCell, Cell endCell, int weight, boolean solid) {

    this.startCell = startCell;
    this.endCell = endCell;
    this.weight = weight;
    this.solid = solid;
  }

}

// Represents a Utils class
class ArrayUtils {
  // Empty Constructor
  ArrayUtils() {
  }

  void reconstruct(HashMap<Cell, Cell> map, Cell last, Cell first) {

    Cell currentNode = last;

    while (currentNode != first) {
      currentNode.color = Color.BLACK;
      currentNode = map.get(last);

    }

  }

  // draws the world
  WorldScene drawWorldScene(ArrayList<Cell> board, WorldScene scene) {
    for (Cell c : board) {
      scene.placeImageXY(c.drawCell(), c.xPos + (Cell.CELL_SIZE / 2),
          c.yPos + (Cell.CELL_SIZE / 2));
    }
    return scene;
  }

  // generates the board
  ArrayList<Cell> generateBoard(int boardWidth, int boardHeight) {
    ArrayList<Cell> result = new ArrayList<Cell>();
    for (int i = 0; i < boardWidth * boardHeight; i++) {
      result.add(new Cell((i % boardWidth) * Cell.CELL_SIZE,
          (int) ((Math.floor(i / boardWidth)) * Cell.CELL_SIZE), i));
    }

    return result;
  }

  // mutates the board and returns a list of the edges connected appropriately
  ArrayList<Edge> connectBoard(ArrayList<Cell> board, int boardWidth, int boardHeight) {
    ArrayList<Edge> result = new ArrayList<Edge>();
    // we only need to make bottom and right edges, because that is how we construct
    // our board
    for (int i = 0; i < boardWidth * boardHeight; i++) {
      // bottom edge
      if (i + boardWidth < boardWidth * boardHeight) {
        Edge bot = new Edge(board.get(i), board.get(i + boardWidth), new Random().nextInt(10),
            true);
        board.get(i).botEdge = bot;
        board.get(i + boardWidth).topEdge = bot;
        result.add(bot);
      }

      // right edge
      if (i % boardWidth != boardWidth - 1) {
        Edge right = new Edge(board.get(i), board.get(i + 1), new Random().nextInt(10), true);
        board.get(i).rightEdge = right;
        board.get(i + 1).leftEdge = right;
        result.add(right);
      }

    }
    return result;
  }

  // connectBoardTest method with seeded random
  ArrayList<Edge> connectBoardTest(ArrayList<Cell> board, int boardWidth, Random rand) {
    ArrayList<Edge> result = new ArrayList<Edge>();
    // we only need to make bottom and right edges, because that is how we construct
    // our board
    for (int i = 0; i < boardWidth * boardWidth; i++) {
      // bottom edge
      if (i + boardWidth < boardWidth * boardWidth) {
        Edge bot = new Edge(board.get(i), board.get(i + boardWidth), rand.nextInt(), true);
        board.get(i).botEdge = bot;
        board.get(i + boardWidth).topEdge = bot;
        result.add(bot);
      }

      // right edge
      if (i % boardWidth != boardWidth - 1) {
        Edge right = new Edge(board.get(i), board.get(i + 1), rand.nextInt(10), true);
        board.get(i).rightEdge = right;
        board.get(i + 1).leftEdge = right;
        result.add(right);
      }

    }
    return result;
  }

  // Sorts the given ArrayList<Edge> using QuickSort
  ArrayList<Edge> sort(ArrayList<Edge> board, int min, int max) {
    if (min > max) {
      return board;

    }
    int midIndex = min + (max - min) / 2;
    int pivot = board.get(midIndex).weight;
    int i = min;
    int j = max;

    while (i <= j) {
      while (board.get(i).weight < pivot) {
        i++;
      }
      while (board.get(j).weight > pivot) {
        j--;
      }
      if (i <= j) {
        Edge temp = board.get(i);
        board.set(i, board.get(j));
        board.set(j, temp);
        i++;
        j--;

      }

    }
    if (min < j) {
      sort(board, min, j);

    }
    if (max > i) {
      sort(board, i, max);
    }
    return board;

  }

  // Kruskal's Algorithm
  void kruskal(ArrayList<Edge> sortedBoard) {
    HashMap<Integer, ArrayList<Cell>> bins = new HashMap<Integer, ArrayList<Cell>>();
    for (int i = 0; i < Maze.BOARD_WIDTH * Maze.BOARD_HEIGHT; i++) {
      bins.put(i, new ArrayList<Cell>());
    }

    for (Edge e : sortedBoard) {
      if (e.startCell.set != e.endCell.set) {
        e.solid = false;

        bins.get(e.startCell.set).add(e.startCell);
        bins.get(e.endCell.set).add(e.endCell);

        for (Cell c : bins.get(e.endCell.set)) {
          c.set = e.startCell.set;
          bins.get(e.startCell.set).add(c);
        }

      }
    }
  }

  ArrayList<Cell> dfs(ArrayList<Cell> cellList) {

    // THERE ARE EDGES ON THE WHOLE BOARD, WHETHER OR NOT THEY ARE PASSABLE IS A
    // BOOLEAN SOLID
    ArrayList<Cell> result = new ArrayList<Cell>();
    ArrayList<Cell> alreadySeenCell = new ArrayList<Cell>();
    ArrayList<Cell> workList = new ArrayList<Cell>();

    workList.add(cellList.get(0));

    Cell finalCell = cellList.get(cellList.size() - 1);

    while (workList.size() > 0) {
      Cell c = workList.remove(0);
      result.add(c);
      if (c == finalCell) {
        for (Cell cell : result) {
          cell.color = Color.WHITE;
          System.out.println(Integer.toString(cell.xPos) + "," + Integer.toString(cell.yPos));
        }
        return result;
      }

      if (alreadySeenCell.contains(c)) {
        result.remove(result.lastIndexOf(c));
      }
      else {
        for (Edge e : c.listifyEdges()) {
          workList.add(0, e.startCell);
          workList.add(0, e.endCell);
        }

        alreadySeenCell.add(c);

      }

      for (int i = result.size() - 1; i == 0; i--) {
        if (!workList.get(0).isANeighbor(result.get(i)) || workList.get(0) == result.get(i)) {
          result.remove(i);
        }
        else {
          break;
        }
      }
    }
    // for (Cell cell : result) {
    // cell.color = Color.WHITE;
    // System.out.println(Integer.toString(cell.xPos) + "," +
    // Integer.toString(cell.yPos));
    // }
    return result;
  }

  boolean depthFirstSearch(Cell from, Cell to) {
    // to make it return a list, maybe have an
    // arrayList correctPath, which objects get removed from
    // if them and all their neighbors neighbors... have been explored

    // use a boolean function like "fullyExplored" or smthn

    ArrayList<Cell> alreadySeen = new ArrayList<Cell>();
    ArrayList<Cell> workList = new ArrayList<Cell>();

    // put from onto the worklist

    workList.add(0, from);

    while (workList.size() > 0) {
      Cell c = workList.remove(0);
      if (c == to) {
        return true;
      }

      if (alreadySeen.contains(c)) {
        continue;
      }
      else {
        System.out.println(Integer.toString(c.xPos) + "," + Integer.toString(c.yPos));
        for (Edge e : c.listifyEdges()) {
          workList.add(0, e.endCell);
          workList.add(0, e.startCell);
        }
        alreadySeen.add(c);
      }

      // get the next vertex to work from
      // if that vertex == to, DONE
      // esle if that vertex has been seen, SKIP
      // else
      // put vertices that vertex poitns to onto the workList
      // we've seen vertex now

    }

    return false;
  }

}

// Represents a mutable collection of items
interface ICollection<T> {
  // Is this collection empty?
  boolean isEmpty();

  // EFFECT: adds the item to the collection
  void add(T item);

  // Returns the first item of the collection
  // EFFECT: removes that first item
  T remove();
}

class Stack<T> implements ICollection<T> {
  ArrayList<T> contents;

  Stack() {
    this.contents = new ArrayList<T>();
  }

  // Is this collection empty?
  public boolean isEmpty() {
    return this.contents.isEmpty();
  }

  // EFFECT: adds the item to the collection
  public void add(T item) {
    this.contents.add(0, item);
  }

  // Returns the first item of the collection
  // EFFECT: removes that first item
  public T remove() {
    return this.contents.remove(0);
  }

}

class Queue<T> implements ICollection<T> {
  ArrayList<T> contents;

  Queue() {
    this.contents = new ArrayList<T>();
  }

  @Override
  public boolean isEmpty() {

    return this.contents.isEmpty();
  }

  @Override
  public void add(T item) {
    this.contents.add(0, item);

  }

  @Override
  public T remove() {
    return this.contents.remove(this.contents.size() - 1);
  }
}

// Represents Examples for the Maze
class ExamplesMaze {
  ExamplesMaze() {
  }

  Cell c1;
  Cell c2;
  Cell c3;
  Cell c4;
  Cell c5;
  Cell c6;
  Cell c7;
  Cell c8;
  Cell c9;

  Edge e1;
  Edge e2;
  Edge e3;
  Edge e4;
  Edge e5;
  Edge e6;
  Edge e7;
  Edge e8;
  Edge e9;

  ArrayList<Cell> board1;
  ArrayList<Edge> edge1;

  WorldScene result;

  void initConditionEdge() {

    // 2x2 board

    this.e1 = new Edge(this.c1, this.c2, 1, false);
    this.e2 = new Edge(this.c2, this.c3, 2, false);
    this.e3 = new Edge(this.c3, this.c4, 3, false);
    this.e4 = new Edge(this.c4, this.c5, 4, false);
    this.e5 = new Edge(this.c5, this.c6, 5, false);
    this.e6 = new Edge(this.c6, this.c7, 6, false);
    this.e7 = new Edge(this.c7, this.c8, 7, false);
    this.e8 = new Edge(this.c8, this.c9, 8, false);
    this.e9 = new Edge(this.c9, this.c9, 9, false);

  }

  void initConditions2x2() {
    // represents a 2 x 2 board

    // +-------+
    // | C1 C2 |
    // | C3 C4 |
    // +-------+

    this.c1 = new Cell(0, 0, 0);
    this.c2 = new Cell(Cell.CELL_SIZE, 0, 1);
    this.c3 = new Cell(0, Cell.CELL_SIZE, 2);
    this.c4 = new Cell(Cell.CELL_SIZE, Cell.CELL_SIZE, 3);

    // represents a 3 x 3 board

    // c1 is the top left
    // c4 is the center
    // c3 is the middle left
    // c2 is the center top

  }

  void initConditions3x3() {
    // represents a 3 x 3 board

    // +----------+
    // | C1 C2 C6 |
    // | C3 C4 C8 |
    // | C7 C9 C5 |
    // +----------+

    // c1 is the top left
    this.c1 = new Cell(0, 0, 0);

    // c2 is the center top
    this.c2 = new Cell(Cell.CELL_SIZE, 0, 1);

    // c3 is the center left
    this.c3 = new Cell(0, Cell.CELL_SIZE, 3);

    // c4 is the center
    this.c4 = new Cell(Cell.CELL_SIZE, Cell.CELL_SIZE, 4);

    // bottom right
    this.c5 = new Cell((2 * Cell.CELL_SIZE), (2 * Cell.CELL_SIZE), 8);

    // top right
    this.c6 = new Cell((2 * Cell.CELL_SIZE), 0, 2);

    // bottom left
    this.c7 = new Cell(0, (2 * Cell.CELL_SIZE), 6);

    // center right
    this.c8 = new Cell((2 * Cell.CELL_SIZE), Cell.CELL_SIZE, 5);

    // bottom center
    this.c9 = new Cell(Cell.CELL_SIZE, (2 * Cell.CELL_SIZE), 7);

  }

  void testGame(Tester t) {
    ArrayList<Cell> cellBoard = new ArrayUtils().generateBoard(Maze.BOARD_WIDTH, Maze.BOARD_HEIGHT);
    ArrayList<Edge> edgeBoard = new ArrayUtils().connectBoard(cellBoard, Maze.BOARD_WIDTH,
        Maze.BOARD_HEIGHT);
    ArrayList<Edge> sortedEdgeBoard = new ArrayUtils().sort(edgeBoard, 0, edgeBoard.size() - 1);
    new ArrayUtils().kruskal(sortedEdgeBoard);

    // System.out.println(new ArrayUtils().depthFirstSearch(cellBoard.get(0), new
    // Cell (4,4,4)));
    // System.out.println(new ArrayUtils().depthFirstSearch(cellBoard.get(0),
    // cellBoard.get(99)));
    // new ArrayUtils().dfs(cellBoard);

    Maze game = new Maze(cellBoard, sortedEdgeBoard);
    game.bigBang(Cell.CELL_SIZE * Maze.BOARD_WIDTH, Cell.CELL_SIZE * Maze.BOARD_HEIGHT, 0.1);
  }

  // tests the drawCell method
  void testDrawCell(Tester t) {
    // initconditions
    this.initConditions2x2();

    // ArrayUtils util = new ArrayUtils();

    // make sure the cells are all correct
    t.checkExpect(this.c1, new Cell(0, 0, 0));
    t.checkExpect(this.c2, new Cell(Cell.CELL_SIZE, 0, 1));
    t.checkExpect(this.c3, new Cell(0, Cell.CELL_SIZE, 2));
    t.checkExpect(this.c4, new Cell(Cell.CELL_SIZE, Cell.CELL_SIZE, 3));

    // let's bring in the edges now
    this.initConditionEdge();
    // we really only need 4 edges for these tests

    // top edge
    t.checkExpect(this.e1, new Edge(this.c1, this.c2, 1, false));

    // left edge
    t.checkExpect(this.e2, new Edge(this.c2, this.c3, 2, false));

    // right edge
    t.checkExpect(this.e3, new Edge(this.c3, this.c4, 3, false));

    // bot edge
    t.checkExpect(this.e4, new Edge(this.c4, this.c5, 4, false));

    // no edge solid
    Cell cell1 = new Cell(0, 0, this.e1, this.e4, this.e3, this.e2, 0, Cell.CELL_COLOR);

    t.checkExpect(cell1.drawCell(),
        new RectangleImage(Cell.CELL_SIZE, Cell.CELL_SIZE, OutlineMode.SOLID, Cell.CELL_COLOR));

    WorldImage horLine = new RectangleImage(Cell.CELL_SIZE, 1, OutlineMode.SOLID, Cell.WALL_COLOR);
    WorldImage vertLine = new RectangleImage(1, Cell.CELL_SIZE, OutlineMode.SOLID, Cell.WALL_COLOR);

    // all edges unsolid
    Cell cell2 = new Cell(0, 0, this.e1, this.e4, this.e3, this.e2, 0, Cell.CELL_COLOR);
    this.e1.solid = false;
    this.e2.solid = false;
    this.e3.solid = false;
    this.e4.solid = false;

    // top edge solid
    WorldImage top = new OverlayOffsetImage(horLine, 0, Cell.CELL_SIZE / 2, cell2.drawCell());

    Cell cell3 = new Cell(0, 0, this.e1, this.e4, this.e3, this.e2, 0, Cell.CELL_COLOR);
    this.e1.solid = true;
    t.checkExpect(cell3.drawCell(), top);
    // bot edge solid, but we do not draw it, since our draw function only draws
    // top and right

    Cell cell4 = new Cell(0, 0, this.e1, this.e4, this.e3, this.e2, 0, Cell.CELL_COLOR);
    this.e1.solid = false;
    this.e4.solid = true;

    t.checkExpect(cell4.drawCell(),
        new RectangleImage(Cell.CELL_SIZE, Cell.CELL_SIZE, OutlineMode.SOLID, Cell.CELL_COLOR));

    // right edge solid

    this.e4.solid = false;
    this.e3.solid = true;

    WorldImage right = new OverlayOffsetImage(vertLine, -Cell.CELL_SIZE / 2, 0,
        new RectangleImage(Cell.CELL_SIZE, Cell.CELL_SIZE, OutlineMode.SOLID, Cell.CELL_COLOR));
    t.checkExpect(cell3.drawCell(), right);

    // left edge solid, but we do not draw it, since our draw function only draws
    // top and right

    this.e3.solid = false;
    this.e2.solid = true;

    t.checkExpect(cell3.drawCell(),
        new RectangleImage(Cell.CELL_SIZE, Cell.CELL_SIZE, OutlineMode.SOLID, Cell.CELL_COLOR));

  }

  // tests the sort method
  void testSort(Tester t) {
    // initconditions
    this.initConditionEdge();

    // testing the necessary initconditions
    t.checkExpect(this.e1, new Edge(this.c1, this.c2, 1, false));
    t.checkExpect(this.e2, new Edge(this.c2, this.c3, 2, false));
    t.checkExpect(this.e3, new Edge(this.c3, this.c4, 3, false));
    t.checkExpect(this.e4, new Edge(this.c4, this.c5, 4, false));
    t.checkExpect(this.e5, new Edge(this.c5, this.c6, 5, false));
    t.checkExpect(this.e6, new Edge(this.c6, this.c7, 6, false));
    t.checkExpect(this.e7, new Edge(this.c7, this.c8, 7, false));
    t.checkExpect(this.e8, new Edge(this.c8, this.c9, 8, false));
    t.checkExpect(this.e9, new Edge(this.c9, this.c9, 9, false));

    this.edge1 = new ArrayList<Edge>();
    t.checkExpect(this.edge1.size(), 0);
    // unsorted board
    this.edge1 = new ArrayList<Edge>(Arrays.asList(this.e5, this.e4, this.e3, this.e2, this.e1,
        this.e6, this.e7, this.e9, this.e8));

    // making sure it's unsorted
    ArrayList<Integer> sum = new ArrayList<Integer>();
    for (int i = 0; i < this.edge1.size(); i++) {
      sum.add(this.edge1.get(i).weight);
    }

    t.checkExpect(sum, new ArrayList<Integer>(Arrays.asList(5, 4, 3, 2, 1, 6, 7, 9, 8)));

    // let's sort it now!

    ArrayUtils util = new ArrayUtils();

    util.sort(this.edge1, 0, this.edge1.size() - 1);

    // print out all the weights to make sure it's sorted
    ArrayList<Integer> sum2 = new ArrayList<Integer>();
    for (int i = 0; i < this.edge1.size(); i++) {
      sum2.add(this.edge1.get(i).weight);
    }

    t.checkExpect(sum2, new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9)));

  }

  // test the Kruskal method
  void testKruskal(Tester t) {
    ArrayUtils util = new ArrayUtils();

    ArrayList<Cell> cellBoard = new ArrayUtils().generateBoard(3, 3);
    ArrayList<Edge> edgeBoard = new ArrayUtils().connectBoard(cellBoard, 3, 3);
    ArrayList<Edge> sortedEdgeBoard = new ArrayUtils().sort(edgeBoard, 0, edgeBoard.size() - 1);
    util.kruskal(sortedEdgeBoard);

    // let's add all the cell's set to a local variable
    // called sum so see if kruskal has worked
    ArrayList<Integer> sum = new ArrayList<Integer>();
    for (int i = 0; i < sortedEdgeBoard.size(); i++) {
      sum.add(sortedEdgeBoard.get(i).endCell.set);
      sum.add(sortedEdgeBoard.get(i).startCell.set);

    }

    // check that the set of all the cells have same set
    Set<Integer> check = new HashSet<>(sum);

    // if all elements in the array sum is same
    // then the size of the HashSet is 1
    t.checkExpect(check.size(), 1);

  }

  // tests the generateBoard method
  void testGenerateBoardMethod(Tester t) {
    // init conditions
    this.initConditions2x2();

    // make sure the cells are all correct
    t.checkExpect(this.c1, new Cell(0, 0, 0));
    t.checkExpect(this.c2, new Cell(Cell.CELL_SIZE, 0, 1));
    t.checkExpect(this.c3, new Cell(0, Cell.CELL_SIZE, 2));
    t.checkExpect(this.c4, new Cell(Cell.CELL_SIZE, Cell.CELL_SIZE, 3));

    // checks the generation of a 2 x 2 board
    ArrayList<Cell> cellBoard = new ArrayUtils().generateBoard(2, 2);
    t.checkExpect(cellBoard,
        new ArrayList<Cell>(Arrays.asList(this.c1, this.c2, this.c3, this.c4)));

    this.initConditions3x3();

    // make sure all the cells are correct
    t.checkExpect(this.c1, new Cell(0, 0, 0));
    t.checkExpect(this.c2, new Cell(Cell.CELL_SIZE, 0, 1));
    t.checkExpect(this.c3, new Cell(0, Cell.CELL_SIZE, 3));
    t.checkExpect(this.c4, new Cell(Cell.CELL_SIZE, Cell.CELL_SIZE, 4));
    t.checkExpect(this.c5, new Cell((2 * Cell.CELL_SIZE), (2 * Cell.CELL_SIZE), 8));
    t.checkExpect(this.c6, new Cell((2 * Cell.CELL_SIZE), 0, 2));
    t.checkExpect(this.c7, new Cell(0, (2 * Cell.CELL_SIZE), 6));
    t.checkExpect(this.c8, new Cell((2 * Cell.CELL_SIZE), Cell.CELL_SIZE, 5));
    t.checkExpect(this.c9, new Cell(Cell.CELL_SIZE, (2 * Cell.CELL_SIZE), 7));

    // checks the generation of a 3 x 3 board
    ArrayList<Cell> cellBoard2 = new ArrayUtils().generateBoard(3, 3);
    t.checkExpect(cellBoard2, new ArrayList<Cell>(Arrays.asList(this.c1, this.c2, this.c6, this.c3,
        this.c4, this.c8, this.c7, this.c9, this.c5)));

  }

  // tests the connectBoard method
  void testConnectBoard(Tester t) {
    this.initConditions2x2();
    // ArrayUtils util = new ArrayUtils();
    //
    // ArrayList<Cell> board1 = new ArrayList<Cell>(Arrays.asList(this.c1, this.c2,
    // this.c3, this.c4));

    // let's make edges

    // edge between c1 and c2
    this.e1 = new Edge(this.c1, this.c2, new Random(2).nextInt(), true);
    // edge between c1 and c3
    this.e2 = new Edge(this.c1, this.c3, new Random(2).nextInt(), true);
    // edge between c3 and c4
    this.e3 = new Edge(this.c3, this.c4, new Random(2).nextInt(), true);
    // edge between c2 and c4
    this.e4 = new Edge(this.c2, this.c4, new Random(2).nextInt(), true);

    this.c1 = new Cell(0, 0, null, this.e2, this.e1, null, 0, Cell.CELL_COLOR);
    this.c2 = new Cell(Cell.CELL_SIZE, 0, null, this.e4, null, this.e1, 1, Cell.CELL_COLOR);
    this.c3 = new Cell(0, Cell.CELL_SIZE, this.e1, null, this.e3, null, 2, Cell.CELL_COLOR);
    this.c4 = new Cell(Cell.CELL_SIZE, Cell.CELL_SIZE, this.e4, null, null, this.e3, 3,
        Cell.CELL_COLOR);

    this.edge1 = new ArrayList<Edge>(Arrays.asList(this.e1, this.e2, this.e3, this.e4));

  }

  // result of makeScene on a 2x2 board
  void makeScene2x2() {
    this.initConditionEdge();
    this.initConditions2x2();
    this.result = new WorldScene(Maze.BOARD_WIDTH * Cell.CELL_SIZE,
        Maze.BOARD_HEIGHT * Cell.CELL_SIZE);

    result.placeImageXY(this.c1.drawCell(), Cell.CELL_SIZE / 2, Cell.CELL_SIZE / 2);
    result.placeImageXY(this.c2.drawCell(), 3 * (Cell.CELL_SIZE / 2), Cell.CELL_SIZE / 2);
    result.placeImageXY(this.c3.drawCell(), Cell.CELL_SIZE / 2, 3 * (Cell.CELL_SIZE / 2));
    result.placeImageXY(this.c4.drawCell(), 3 * (Cell.CELL_SIZE / 2), 3 * (Cell.CELL_SIZE / 2));

  }

  // tests the makeScene method
  void testMakeScene(Tester t) {
    // set the variables as empty arrays
    this.board1 = new ArrayList<Cell>();
    this.edge1 = new ArrayList<Edge>();

    // create a base game with the empty arrays
    Maze gameBase = new Maze(this.board1, this.edge1);

    // it should just make an empty background
    t.checkExpect(gameBase.makeScene(),
        new WorldScene(Maze.BOARD_WIDTH * Cell.CELL_SIZE, Maze.BOARD_HEIGHT * Cell.CELL_SIZE));

    // now we bring in the 2x2 board variables
    this.initConditions2x2();

    // we test the used variables
    t.checkExpect(this.c1, new Cell(0, 0, 0));
    t.checkExpect(this.c2, new Cell(Cell.CELL_SIZE, 0, 1));
    t.checkExpect(this.c3, new Cell(0, Cell.CELL_SIZE, 2));
    t.checkExpect(this.c4, new Cell(Cell.CELL_SIZE, Cell.CELL_SIZE, 3));

    this.initConditionEdge();
    t.checkExpect(this.e1, new Edge(this.c1, this.c2, 1, false));
    t.checkExpect(this.e2, new Edge(this.c2, this.c3, 2, false));
    t.checkExpect(this.e3, new Edge(this.c3, this.c4, 3, false));
    t.checkExpect(this.e4, new Edge(this.c4, this.c5, 4, false));
    t.checkExpect(this.e5, new Edge(this.c5, this.c6, 5, false));
    t.checkExpect(this.e6, new Edge(this.c6, this.c7, 6, false));
    t.checkExpect(this.e7, new Edge(this.c7, this.c8, 7, false));
    t.checkExpect(this.e8, new Edge(this.c8, this.c9, 8, false));
    t.checkExpect(this.e9, new Edge(this.c9, this.c9, 9, false));

    // put the cells in the board
    this.board1 = new ArrayList<Cell>(Arrays.asList(this.c1, this.c2, this.c3, this.c4));

    // put the edges in the edge list
    this.edge1 = new ArrayList<Edge>(Arrays.asList(this.e1, this.e2, this.e3, this.e4, this.e5,
        this.e6, this.e7, this.e6, this.e9));

    // we invoke the makeScene2x2 void method
    // that places image for us in the right places
    // to make the maze
    this.makeScene2x2();
    Maze game2x2 = new Maze(this.board1, this.edge1);

    t.checkExpect(game2x2.makeScene(), this.result);

  }

  void testDrawWorldScene(Tester t) {
    initConditions2x2();

    this.board1 = new ArrayList<Cell>(Arrays.asList(this.c1, this.c2, this.c3, this.c4));

    // this.edge1 = new ArrayUtils().connectBoard(board1, 2);

    WorldScene background = new WorldScene(2 * Cell.CELL_SIZE, 2 * Cell.CELL_SIZE);

    WorldImage cell = new RectangleImage(Cell.CELL_SIZE, Cell.CELL_SIZE, OutlineMode.SOLID,
        Cell.CELL_COLOR);
    WorldScene background2 = new WorldScene(2 * Cell.CELL_SIZE, 2 * Cell.CELL_SIZE);
    background2.placeImageXY(cell, 5, 5);
    background2.placeImageXY(cell, 15, 5);
    background2.placeImageXY(cell, 5, 15);
    background2.placeImageXY(cell, 15, 15);

    t.checkExpect(new ArrayUtils().drawWorldScene(board1, background), background2);

  }

}