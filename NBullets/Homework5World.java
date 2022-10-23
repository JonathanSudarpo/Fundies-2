import tester.*;
import javalib.worldimages.*;
import javalib.funworld.*;
import java.awt.Color;
import java.util.Random;

// represents my game
class MyGame extends World {
  int width;
  int height;
  int currentTick;
  ILoShip currentShips;
  ILoBullet currentBullets;
  int bulletsInMag;
  int score;

  // convenience constructor
  MyGame(int startingBullets) {
    this(IGamePiece.WIDTH, IGamePiece.HEIGHT, 1, new MtLoShip(), new MtLoBullet(), startingBullets,
        0);
  }

  // constructor
  MyGame(int width, int height, int currentTick, ILoShip currentShips, ILoBullet currentBullets,
      int bulletsInMag, int score) {
    if (width < 0 || height < 0 || currentTick < 0 || bulletsInMag < 0) {
      throw new IllegalArgumentException("Illegal Argument");
    }
    else {
      this.width = width;
      this.height = height;
      this.currentTick = currentTick;
      this.currentShips = currentShips;
      this.currentBullets = currentBullets;
      this.bulletsInMag = bulletsInMag;
      this.score = score;
    }
  }

  // returns a scene that displays the remaining bullets and score
  public WorldScene displayInfo(WorldScene scene) {
    return scene.placeImageXY(new TextImage("Bullets left: " + Integer.toString(bulletsInMag)
        + "      " + "Score: " + Integer.toString(this.score), Color.red), 80, 10);
  }

  @Override
  // displays the info and draws the game pieces
  public WorldScene makeScene() {
    WorldScene scene = new WorldScene(this.width, this.height);
    scene = this.displayInfo(scene);
    scene = this.currentBullets.drawWorldScene(scene);
    scene = this.currentShips.drawWorldScene(scene);
    return scene;

  }

  ////////////////////////////////////// WORLDSTATE CHANGES
  /////////////////////////////////////////////////////////////////////////////////////

  // moves the bullets in the world
  public MyGame moveBulletsWorld() {
    return new MyGame(this.width, this.height, this.currentTick, this.currentShips,
        this.currentBullets.moveAllBulletsUp(), this.bulletsInMag, this.score);
  }

  // removes the bullets that are out of bounds
  public MyGame removeBulletsWorld() {
    return new MyGame(this.width, this.height, this.currentTick, this.currentShips,
        this.currentBullets.removeBullets(), this.bulletsInMag, this.score);
  }

  // increases the tick of the game by 1
  public MyGame increaseTick() {
    return new MyGame(this.width, this.height, this.currentTick + 1, this.currentShips,
        this.currentBullets, this.bulletsInMag, this.score);
  }

  // moves the ships
  public MyGame moveShipsWorld() {
    return new MyGame(this.width, this.height, this.currentTick, this.currentShips.moveAllShips(),
        this.currentBullets, this.bulletsInMag, this.score);
  }

  // adds a random amount of ships to the world
  public MyGame addShipsWorld() {
    return new MyGame(this.width, this.height, this.currentTick,
        this.currentShips.addLeftShipsToList(new Random().nextInt(3) + 1).addRightShipsToList(
            new Random().nextInt(3) + 1),
        this.currentBullets, this.bulletsInMag, this.score);
  }

  // removes any out of bounds ships from the world
  public MyGame removeShipsWorld() {
    return new MyGame(this.width, this.height, this.currentTick, this.currentShips.removeShips(),
        this.currentBullets, this.bulletsInMag, this.score);
  }

  // increases the score of the game by 1 for every ship destroyed
  public MyGame scoreWorld() {
    return new MyGame(this.width, this.height, this.currentTick, this.currentShips,
        this.currentBullets, this.bulletsInMag, this.score + (this.currentShips.length()
            - (this.currentShips.collision(currentBullets)).length()));
  }

  // checks for collision and explodes bullets that collides with ships
  // and deletes ships that are hit
  public MyGame collisionWorld() {
    return new MyGame(this.width, this.height, this.currentTick,
        this.currentShips.collision(this.currentBullets),
        this.currentBullets.collision(this.currentShips), this.bulletsInMag, this.score);
  }

  // spawns ships into the game at a certain tick interval
  public MyGame spawnShips() {
    if (this.currentTick % 60 == 0) {
      return this.addShipsWorld();
    }
    else {
      return this;
    }
  }
  /////////////////////////////////////////////////////////////////////////////////////

  @Override
  // updates the world with the world methods every tick
  public World onTick() {
    return this.removeBulletsWorld().moveBulletsWorld().spawnShips().moveShipsWorld()
        .removeShipsWorld().scoreWorld().collisionWorld().increaseTick();
  }

  // if spacebar is pressed, fires a bullet and removes one from the magazine
  // otherwise does nothing
  @Override
  public World onKeyEvent(String key) {
    if (key.equals(" ") && this.bulletsInMag > 0) {
      return new MyGame(this.width, this.height, this.currentTick, this.currentShips,
          new ConsLoBullet(new Bullet(), this.currentBullets), this.bulletsInMag - 1, this.score);
    }
    else {
      return this;
    }
  }

  // returns a scene that just says "Gameover"
  WorldScene makeEndScene() {
    WorldScene scene = new WorldScene(this.width, this.height);
    return scene.placeImageXY(new TextImage("Gameover", Color.red), (int) (this.width / 2.0),
        (int) (this.height / 2.0));
  }

  @Override
  // ends the game if there are no bullets left to fire and none left on screen
  public WorldEnd worldEnds() {
    if (this.currentBullets.length() + this.bulletsInMag == 0) {
      return new WorldEnd(true, makeEndScene());
    }
    else {
      return new WorldEnd(false, makeEndScene());
    }
  }
}

// examples class for world methods, class tests are in other file
class ExamplesMyWorldProgram {

  // scene examples
  WorldScene basicScene = new WorldScene(IGamePiece.WIDTH, IGamePiece.HEIGHT);
  WorldScene infoDisplayed = basicScene.placeImageXY(
      new TextImage("Bullets left: " + "10" + "      " + "Score: " + "0", Color.red), 80, 10);
  WorldScene fivePoints = basicScene.placeImageXY(
      new TextImage("Bullets left: " + "10" + "      " + "Score: " + "5", Color.red), 80, 10);
  WorldScene endScene = basicScene.placeImageXY(new TextImage("Gameover", Color.red),
      (int) (IGamePiece.WIDTH / 2.0), (int) (IGamePiece.HEIGHT / 2.0));

  // Bullet and ILoBullet examples
  Bullet defaultBullet = new Bullet();
  Bullet defaultBulletMoved = new Bullet(250, 292, 90, 2);
  Bullet offscreenBullet = new Bullet(250, 800, 90, 2);
  Bullet collidingBullet = new Bullet(0, 0, 90, 2);
  ILoBullet emptyLoBullet = new MtLoBullet();
  ILoBullet simpleILoBullet = new ConsLoBullet(this.defaultBullet, this.emptyLoBullet);
  ILoBullet simpleILoBulletMoved = new ConsLoBullet(this.defaultBulletMoved, this.emptyLoBullet);
  ILoBullet offscreenBulletList = new ConsLoBullet(this.offscreenBullet, this.emptyLoBullet);
  ILoBullet collidingBulletList = new ConsLoBullet(this.collidingBullet, this.emptyLoBullet);
  ILoBullet explodedBullet = new ConsLoBullet(new Bullet(0, 0, 360, 3),
      new ConsLoBullet(new Bullet(0, 0, 180, 3), this.emptyLoBullet));
  ILoBullet twoBullets = new ConsLoBullet(this.defaultBullet, this.simpleILoBullet);
  ILoBullet twoBulletsMoved = new ConsLoBullet(this.defaultBulletMoved, this.simpleILoBulletMoved);
  ILoBullet twoBulletsColliding = new ConsLoBullet(this.collidingBullet, this.collidingBulletList);
  ILoBullet twoBulletsExploding = new ConsLoBullet(new Bullet(0, 0, 360, 3),
      new ConsLoBullet(new Bullet(0, 0, 180, 3), new ConsLoBullet(new Bullet(0, 0, 360, 3),
          new ConsLoBullet(new Bullet(0, 0, 180, 3), this.emptyLoBullet))));

  // Ship and ILoShip examples
  Ship defaultShip = new Ship(0, 100);
  Ship defaultShipMoved = new Ship(4, 100);
  Ship offscreenShip = new Ship(-40, 100);
  Ship collidingShip = new Ship(0, 0);
  ILoShip emptyILoShip = new MtLoShip();
  ILoShip simpleILoShip = new ConsLoShip(this.defaultShip, this.emptyILoShip);
  ILoShip simpleILoShipMoved = new ConsLoShip(this.defaultShipMoved, this.emptyILoShip);
  ILoShip offscreenShipList = new ConsLoShip(this.offscreenShip, this.emptyILoShip);
  ILoShip collidingShipList = new ConsLoShip(this.collidingShip, this.emptyILoShip);
  ILoShip twoShips = new ConsLoShip(this.defaultShip, this.simpleILoShip);
  ILoShip twoShipsMoved = new ConsLoShip(this.defaultShipMoved, this.simpleILoShipMoved);
  ILoShip twoShipsOffscreen = new ConsLoShip(this.offscreenShip, this.offscreenShipList);
  ILoShip twoShipsColliding = new ConsLoShip(this.collidingShip, this.collidingShipList);

  // Game Examples
  MyGame game = new MyGame(IGamePiece.WIDTH, IGamePiece.HEIGHT, 1, new MtLoShip(), new MtLoBullet(),
      10, 0);
  MyGame game2Tick = new MyGame(IGamePiece.WIDTH, IGamePiece.HEIGHT, 2, new MtLoShip(),
      new MtLoBullet(), 10, 0);
  MyGame game3Tick = new MyGame(IGamePiece.WIDTH, IGamePiece.HEIGHT, 3, new MtLoShip(),
      new MtLoBullet(), 10, 0);
  MyGame game4Tick = new MyGame(IGamePiece.WIDTH, IGamePiece.HEIGHT, 4, new MtLoShip(),
      new MtLoBullet(), 10, 0);
  MyGame gameWithBullets = new MyGame(IGamePiece.WIDTH, IGamePiece.HEIGHT, 1, new MtLoShip(),
      this.simpleILoBullet, 10, 0);
  MyGame gameWithShips = new MyGame(IGamePiece.WIDTH, IGamePiece.HEIGHT, 1, this.simpleILoShip,
      new MtLoBullet(), 10, 0);
  MyGame gameWithShipsOut = new MyGame(IGamePiece.WIDTH, IGamePiece.HEIGHT, 1,
      this.offscreenShipList, new MtLoBullet(), 10, 0);
  MyGame gameWithBulletsOut = new MyGame(IGamePiece.WIDTH, IGamePiece.HEIGHT, 1, new MtLoShip(),
      this.offscreenBulletList, 10, 0);
  MyGame gameWithCollision = new MyGame(IGamePiece.WIDTH, IGamePiece.HEIGHT, 1,
      this.collidingShipList, this.collidingBulletList, 10, 0);
  MyGame gameOver = new MyGame(IGamePiece.WIDTH, IGamePiece.HEIGHT, 1, new MtLoShip(),
      new MtLoBullet(), 0, 0);
  MyGame gameWithTwoBullets = new MyGame(IGamePiece.WIDTH, IGamePiece.HEIGHT, 1, new MtLoShip(),
      this.twoBullets, 10, 0);
  MyGame gameWithTwoBulletsMoved = new MyGame(IGamePiece.WIDTH, IGamePiece.HEIGHT, 1,
      new MtLoShip(), this.twoBulletsMoved, 10, 0);
  MyGame gameWithTwoShips = new MyGame(IGamePiece.WIDTH, IGamePiece.HEIGHT, 1, this.twoShips,
      new MtLoBullet(), 10, 0);
  MyGame gameWithTwoShipsMoved = new MyGame(IGamePiece.WIDTH, IGamePiece.HEIGHT, 1,
      this.twoShipsMoved, new MtLoBullet(), 10, 0);
  MyGame gameWithTwoShipsOut = new MyGame(IGamePiece.WIDTH, IGamePiece.HEIGHT, 1,
      this.twoShipsOffscreen, new MtLoBullet(), 10, 0);
  MyGame gameWithTwoCollisions = new MyGame(IGamePiece.WIDTH, IGamePiece.HEIGHT, 1,
      this.twoShipsColliding, this.twoBulletsColliding, 10, 0);
  MyGame gameWithTwoExplosions = new MyGame(IGamePiece.WIDTH, IGamePiece.HEIGHT, 1,
      this.emptyILoShip, this.twoBulletsExploding, 10, 0);
  MyGame gameWithFivePoints = new MyGame(IGamePiece.WIDTH, IGamePiece.HEIGHT, 1, new MtLoShip(),
      new MtLoBullet(), 10, 5);

  // tests the gameworld with BigBang
  boolean testBigBang(Tester t) {
    return game.bigBang(IGamePiece.WIDTH, IGamePiece.HEIGHT, .025);
  }

  // tests increaseTick
  boolean testIncreaseTick(Tester t) {
    return t.checkExpect(game.increaseTick(), this.game2Tick)
        && t.checkExpect(game2Tick.increaseTick(), this.game3Tick)
        && t.checkExpect(game3Tick.increaseTick(), this.game4Tick);
  }

  // tests moveBulletsWorld
  boolean testMoveBulletsWorld(Tester t) {
    return t.checkExpect(this.gameWithBullets.moveBulletsWorld(),
        new MyGame(IGamePiece.WIDTH, IGamePiece.HEIGHT, 1, new MtLoShip(),
            this.simpleILoBulletMoved, 10, 0))
        && t.checkExpect(this.gameWithTwoBullets.moveBulletsWorld(), new MyGame(IGamePiece.WIDTH,
            IGamePiece.HEIGHT, 1, new MtLoShip(), this.twoBulletsMoved, 10, 0))
        && t.checkExpect(this.game.moveBulletsWorld(), this.game);
  }

  // tests removeBulletsWorld
  boolean testRemoveBulletsWorld(Tester t) {
    return t.checkExpect(this.gameWithBulletsOut.removeBulletsWorld(),
        new MyGame(IGamePiece.WIDTH, IGamePiece.HEIGHT, 1, new MtLoShip(), this.emptyLoBullet, 10,
            0))
        && t.checkExpect(gameWithTwoBullets.removeBulletsWorld(), this.gameWithTwoBullets)
        && t.checkExpect(this.game.removeBulletsWorld(), this.game);
  }

  // tests moveShipsWorld
  boolean testMoveShipsWorld(Tester t) {
    return t.checkExpect(this.gameWithShips.moveShipsWorld(),
        new MyGame(IGamePiece.WIDTH, IGamePiece.HEIGHT, 1, this.simpleILoShipMoved,
            new MtLoBullet(), 10, 0))
        && t.checkExpect(this.game.moveShipsWorld(), this.game)
        && t.checkExpect(this.gameWithTwoShips.moveShipsWorld(), this.gameWithTwoShipsMoved);
  }

  // tests removeShipsWorld
  boolean testRemoveShipsWorld(Tester t) {
    return t.checkExpect(this.gameWithShipsOut.removeShipsWorld(),
        new MyGame(IGamePiece.WIDTH, IGamePiece.HEIGHT, 1, new MtLoShip(), new MtLoBullet(), 10, 0))
        && t.checkExpect(this.game.removeShipsWorld(), this.game)
        && t.checkExpect(this.gameWithTwoShips.removeShipsWorld(), this.gameWithTwoShips)
        && t.checkExpect(this.gameWithTwoShipsOut.removeShipsWorld(), this.game);
  }

  // tests collisionWorld
  boolean testCollision(Tester t) {
    return t.checkExpect(this.gameWithCollision.collisionWorld(),
        new MyGame(IGamePiece.WIDTH, IGamePiece.HEIGHT, 1, new MtLoShip(), this.explodedBullet, 10,
            0))
        && t.checkExpect(this.game.collisionWorld(), this.game)
        && t.checkExpect(this.gameWithTwoCollisions.collisionWorld(), this.gameWithTwoExplosions);
  }

  // tests scoreWorld
  boolean testScoreWorld(Tester t) {
    return t.checkExpect(this.gameWithCollision.scoreWorld(),
        new MyGame(IGamePiece.WIDTH, IGamePiece.HEIGHT, 1, this.collidingShipList,
            this.collidingBulletList, 10, 1))
        && t.checkExpect(this.game.scoreWorld(), this.game)
        && t.checkExpect(this.gameWithTwoShips.scoreWorld(), this.gameWithTwoShips);
  }

  // tests displayInfo
  boolean testDisplayInfo(Tester t) {
    return t.checkExpect(this.game.displayInfo(this.basicScene), this.infoDisplayed)
        && t.checkExpect(this.gameWithFivePoints.displayInfo(this.basicScene), this.fivePoints);
  }

  // test makeScene
  boolean testMakeScene(Tester t) {
    return t.checkExpect(this.game.makeScene(), this.infoDisplayed)
        && t.checkExpect(this.gameWithFivePoints.makeScene(), this.fivePoints);
  }

  // tests makeEndScene
  boolean testMakeEndScene(Tester t) {
    return t.checkExpect(this.game.makeEndScene(), this.endScene);
  }

  // tests endWorld
  boolean testEndWorld(Tester t) {
    return t.checkExpect(this.game.worldEnds(), new WorldEnd(false, this.endScene))
        && t.checkExpect(this.gameOver.worldEnds(), new WorldEnd(true, this.endScene));
  }

  // tests onTick
  boolean testOnTick(Tester t) {
    return t.checkExpect(this.game.onTick(), new MyGame(IGamePiece.WIDTH, IGamePiece.HEIGHT, 2,
        new MtLoShip(), new MtLoBullet(), 10, 0));
  }

  // tests onKeyEvent
  boolean testOnKeyEvent(Tester t) {
    return t.checkExpect(this.game.onKeyEvent(" "),
        new MyGame(IGamePiece.WIDTH, IGamePiece.HEIGHT, 1, new MtLoShip(),
            new ConsLoBullet(new Bullet(), this.emptyLoBullet), 9, 0))
        && t.checkExpect(this.gameOver.onKeyEvent(" "), new MyGame(IGamePiece.WIDTH,
            IGamePiece.HEIGHT, 1, new MtLoShip(), new MtLoBullet(), 0, 0));
  }

  // tests for constructor exceptions
  boolean testConstructor(Tester t) {
    return t.checkConstructorException(new IllegalArgumentException("Illegal Argument"), "MyGame",
        IGamePiece.WIDTH, IGamePiece.HEIGHT, 1, new MtLoShip(), new MtLoBullet(), -1, 0)
        && t.checkConstructorException(new IllegalArgumentException("Illegal Argument"), "MyGame",
            -10, IGamePiece.HEIGHT, 1, new MtLoShip(), new MtLoBullet(), 1, 0)
        && t.checkConstructorException(new IllegalArgumentException("Illegal Argument"), "MyGame",
            IGamePiece.WIDTH, -10, 1, new MtLoShip(), new MtLoBullet(), 1, 0)
        && t.checkConstructorException(new IllegalArgumentException("Illegal Argument"), "MyGame",
            IGamePiece.WIDTH, IGamePiece.HEIGHT, -4, new MtLoShip(), new MtLoBullet(), -1, 0);
  }

}
