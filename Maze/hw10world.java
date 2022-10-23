
// Libraries used
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;

import javalib.impworld.*;

// Represents a Maze
class Maze extends World {
  // The Maze's board
  ArrayList<Cell> board;
  // The Maze's edges
  ArrayList<Edge> edges;

  // 0 for not searching, 1 for dfs, 2 for bfs
  int searchAlgo;

  // Constructor
  Maze(ArrayList<Cell> board, ArrayList<Edge> edges) {
    this.board = board;
    this.edges = edges;
    this.searchAlgo = 0;
  }

  // Represents the Board's size
  public static int BOARD_WIDTH = 10;
  public static int BOARD_HEIGHT = 10;

  // makes the scene
  @Override
  public WorldScene makeScene() {
    WorldScene background = new WorldScene(Maze.BOARD_WIDTH * Cell.CELL_SIZE,
        Maze.BOARD_HEIGHT * Cell.CELL_SIZE);
    new ArrayUtils().drawWorldScene(board, background);

    return background;
  }

  @Override
  // on tick method
  public void onTick() {
    HashMap<Cell, Cell> cameFromEdge = new HashMap<Cell, Cell>();
    ICollection<Cell> workList = new Stack<Cell>();
    if (this.searchAlgo == 1) {
      workList = new Stack<Cell>();
      workList.add(this.board.get(0));
    }

    if (this.searchAlgo == 2) {
      workList = new Queue<Cell>();
      workList.add(this.board.get(0));
    }

    while (!workList.isEmpty()) {
      Cell next = workList.remove();

      if (cameFromEdge.containsValue(next)) {
        continue;

      }
      else if (next == this.board.get(this.board.size() - 1)) {
        next.color = Color.BLUE;
        // new ArrayUtils().reconstruct(cameFromEdge, next, this.board.get(0));
        break;
      }
      else {
        next.color = Color.BLUE;
        for (Edge e : next.listifyEdges()) {
          if (e.startCell == next) {
            workList.add(e.endCell);
            cameFromEdge.put(e.endCell, next);
          }

          if (e.endCell == next) {
            workList.add(e.startCell);
            cameFromEdge.put(e.startCell, next);
          }

        }
      }

    }

  }

  // onKeyEvent method
  public void onKeyEvent(String ke) {
    if (ke.equals("d")) {
      this.searchAlgo = 1;
    }

    if (ke.equals("b")) {
      this.searchAlgo = 2;
    }
  }

}
