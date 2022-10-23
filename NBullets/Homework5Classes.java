import tester.*;
import javalib.worldimages.*;
import javalib.funworld.*;
import java.awt.Color;
import java.util.Random;

// represents a Game Piece 
interface IGamePiece {
  // CONSTANTS:

  // Scene Width
  int WIDTH = 500;
  // Scene Height
  int HEIGHT = 300;
  // Ship's velocity
  int SHIPVELOCITY = 4;
  // Bullet's velocity
  int BULLETVELOCITY = 8;
  // Ship's radius
  int SHIPRADIUS = 10;
  // Bullet's initial radius
  int BULLETRADIUS = 2;
  // Bullet size multiplier
  int SIZEMULTIPLIER = 2;
}

// represents an abstracted class from IGamePiece
abstract class AGamePiece implements IGamePiece {

  // A game piece's initial X-Position
  int xPos;
  // A game piece's initial Y-Position
  int yPos;

  // Constructor
  AGamePiece(int xPos, int yPos) {
    this.xPos = xPos;
    this.yPos = yPos;
  }
}

// represents a ship
class Ship extends AGamePiece {

  // basic constructor
  Ship(int xPos, int yPos) {
    super(xPos, yPos);
  }

  // places a ship on the given scene, in a given x and y position
  public WorldScene place(WorldScene scene) {
    return scene.placeImageXY(this.drawShip(), this.xPos, this.yPos);
  }

  // draws a ship based on the given parameters
  public WorldImage drawShip() {
    return new CircleImage(Ship.SHIPRADIUS, OutlineMode.SOLID, Color.blue);
  }

  // moves the given ship
  public Ship moveShip() {
    return new Ship(this.xPos + Ship.SHIPVELOCITY, this.yPos);
  }

  // checks whether or not a ship has collided with a bullet
  public boolean collision(Bullet other) {
    return Math.hypot(this.xPos - other.xPos,
        this.yPos - other.yPos) < (Ship.SHIPRADIUS + other.bulletRadius());
  }
}

// represents a ship that spawns on the right
class RightShip extends Ship {

  // basic constructor
  RightShip(int xPos, int yPos) {
    super(xPos, yPos);
  }

  // convenience constructor
  RightShip(int yPos) {
    this(500, yPos);
  }

  // Moves the given RightShip to the left
  @Override
  public RightShip moveShip() {
    return new RightShip(this.xPos - Ship.SHIPVELOCITY, this.yPos);
  }
}

// represents a ship that spawns on the left
class LeftShip extends Ship {

  // basic constructor
  LeftShip(int xPos, int yPos) {
    super(xPos, yPos);
  }

  // convenience constructor
  LeftShip(int yPos) {
    this(0, yPos);
  }

  // Moves the given LeftShip to the right
  @Override
  public LeftShip moveShip() {
    return new LeftShip(this.xPos + Ship.SHIPVELOCITY, this.yPos);
  }
}

// represents a list of ships
interface ILoShip {

  // Draws the list of ships on the given scene
  WorldScene drawWorldScene(WorldScene scene);

  // Moves all ships that are on screen (i.e. in the given list of ships)
  ILoShip moveAllShips();

  // Adds a given amount (based on "num" argument) of right-spawning ships
  // to a given list of ships
  ILoShip addRightShipsToList(int num);

  // Adds a given amount (based on "num" argument) of right-spawning ships
  // to a given list of ships
  ILoShip addLeftShipsToList(int num);

  // Removes a ship from a list of ships
  ILoShip removeShips();

  // If any ships in a list of ships is outside the scene borders,
  // removes the ships else keeps it in the list
  ILoShip removeShipsHelper(Ship first);

  // If any ships in a list of ships collides with any bullets in the
  // given list of bullets, removes the ship from the list.
  ILoShip collision(ILoBullet bullet);

  // checks whether any ships in a list of ships collides with the
  // given bullet
  boolean collides(Bullet bullet);

  // Calculates the length of the list of ships.
  int length();

}

// represents an empty list of ships
class MtLoShip implements ILoShip {

  // empty constructor
  MtLoShip() {
  }

  // Returns the given scene as an empty list of ships doesn't add more
  // or less ships to the scene.
  public WorldScene drawWorldScene(WorldScene scene) {
    return scene;
  }

  // returns the given condition as no ships need to move in am
  // empty list of ships.
  public ILoShip moveAllShips() {
    return this;
  }

  // Adds a given amount (based on "num" argument) of right-spawning ships
  // to a given list of ships
  public ILoShip addRightShipsToList(int num) {
    if (num > 0) {
      return new ConsLoShip(new RightShip(
          new Random().nextInt(Ship.HEIGHT - (2 * Ship.SHIPRADIUS)) + Ship.SHIPRADIUS), this)
              .addRightShipsToList(num - 1);
    }
    else {
      return this;
    }
  }

  // Adds a given amount (based on "num" argument) of left-spawning ships
  // to a given list of ships
  public ILoShip addLeftShipsToList(int num) {
    if (num > 0) {
      return new ConsLoShip(
          new LeftShip(new Random().nextInt(Ship.HEIGHT - (2 * Ship.SHIPRADIUS)) + Ship.SHIPRADIUS),
          this).addLeftShipsToList(num - 1);
    }
    else {
      return this;
    }
  }

  // No ships exist in an empty list of ships, so just returns whatever is given
  public ILoShip removeShips() {
    return this;
  }

  // If any ships in a list of ships is outside the scene borders,
  // removes the ships else keeps it in the list
  public ILoShip removeShipsHelper(Ship first) {
    if ((first.xPos < (-1 * (Ship.SHIPRADIUS))) || (first.xPos > (Ship.WIDTH + Ship.SHIPRADIUS))) {
      return this;
    }
    else {
      return new ConsLoShip(first, this);
    }
  }

  // Empty list of ships has no ships so just returns the current list of ships
  // as we don't have to check for the collision of a non-existent ship.
  public ILoShip collision(ILoBullet bullet) {
    return this;
  }

  // A non-existent ship will never collide with a bullet
  public boolean collides(Bullet bullet) {
    return false;
  }

  // No ships in an empty list of ship, so returns 0.
  public int length() {
    return 0;
  }
}

// Represents a list of ships
class ConsLoShip implements ILoShip {
  // The first ship in a list of ships
  Ship first;
  // the rest of the ships in a list of ships.
  ILoShip rest;

  // constructor
  ConsLoShip(Ship first, ILoShip rest) {
    this.first = first;
    this.rest = rest;
  }

  // Draws the current list of ships on the given scene
  public WorldScene drawWorldScene(WorldScene scene) {
    return this.first.place(rest.drawWorldScene(scene));
  }

  // moves all the ships in the current list of ships.
  public ILoShip moveAllShips() {
    return new ConsLoShip(this.first.moveShip(), this.rest.moveAllShips());
  }

  // Adds a given amount (based on "num" argument) of right-spawning ships
  // to a given list of ships
  public ILoShip addRightShipsToList(int num) {
    if (num > 0) {
      return new ConsLoShip(new RightShip(
          new Random().nextInt(Ship.HEIGHT - (2 * Ship.SHIPRADIUS)) + Ship.SHIPRADIUS), this)
              .addRightShipsToList(num - 1);
    }
    else {
      return this;
    }
  }

  // Adds a given amount (based on "num" argument) of left-spawning ships
  // to a given list of ships
  public ILoShip addLeftShipsToList(int num) {
    if (num > 0) {
      return new ConsLoShip(
          new LeftShip(new Random().nextInt(Ship.HEIGHT - (2 * Ship.SHIPRADIUS)) + Ship.SHIPRADIUS),
          this).addLeftShipsToList(num - 1);
    }
    else {
      return this;
    }
  }

  // removes a ship from the current list of ships.
  public ILoShip removeShips() {
    return this.rest.removeShipsHelper(this.first);
  }

  // If any ship in the current list of ships leaves the border of the world
  // scene,
  // remove it from the current list of ships. Otherwise, leave it in the list of
  // ships.
  public ILoShip removeShipsHelper(Ship first) {
    if ((first.xPos < (-1 * (Ship.SHIPRADIUS))) || (first.xPos > (Ship.WIDTH + Ship.SHIPRADIUS))) {
      return this.rest.removeShipsHelper(this.first);
    }
    else {
      return new ConsLoShip(first, this.removeShips());
    }
  }

  // If any of the ships in the current list of ships
  // collides with any of the bullets in a given list of bullets
  // If it does, removes the ship from the current list of ships
  public ILoShip collision(ILoBullet other) {
    if (other.collides(this.first)) {
      return this.rest.collision(other);
    }
    else {
      return new ConsLoShip(this.first, this.rest.collision(other));
    }
  }

  // checks whether any of the ships in the current list of ships
  // collides with the given bullet
  public boolean collides(Bullet bullet) {
    return this.first.collision(bullet) || this.rest.collides(bullet);
  }

  // calculates the length of the current list of ships
  public int length() {
    return 1 + this.rest.length();
  }
}

// represents a bullet
class Bullet extends AGamePiece {
  // represents the angle a bullet splits
  int theta;
  // The number of bullets a bullet should split into
  // (i.e. an accumulator of the "depth" of bullet collision)
  int payload;

  // basic constructor
  Bullet(int xPos, int yPos, int theta, int payload) {
    super(xPos, yPos);
    this.theta = theta;
    this.payload = payload;
  }

  // spawn middle bottom
  Bullet() {
    this(IGamePiece.WIDTH / 2, IGamePiece.HEIGHT, 90, 2);
  }

  // calculates radius of a bullet
  public int bulletRadius() {
    int newRadius = Bullet.BULLETRADIUS + (this.payload * Bullet.SIZEMULTIPLIER);
    if (newRadius <= 10) {
      return newRadius;
    }
    else {
      return 10;
    }
  }

  // draws a bullet
  public WorldImage drawBullet() {
    return new CircleImage(this.bulletRadius(), OutlineMode.SOLID, Color.red);
  }

  // moves a bullet up by 10 units
  public Bullet moveBulletUp() {
    return new Bullet(
        (int) (this.xPos + (Bullet.BULLETVELOCITY * Math.cos(this.theta * (Math.PI / 180)))),
        (int) (this.yPos - (Bullet.BULLETVELOCITY * Math.sin(this.theta * (Math.PI / 180)))),
        this.theta, this.payload);
  }

  // places a drawn bullet into the given scene with it's x and y position
  public WorldScene place(WorldScene scene) {
    return scene.placeImageXY(this.drawBullet(), this.xPos, this.yPos);
  }

  // checks whether or not a bullet has collided with the given ship
  public boolean collision(Ship other) {
    return Math.hypot(this.xPos - other.xPos,
        this.yPos - other.yPos) < (Ship.SHIPRADIUS + this.bulletRadius());
  }

  // Explodes/splits the bullets based on the "depth" of bullet collision
  // (i.e. splits it depending on how many times the original bullet has hit
  // ships).
  public ILoBullet explode(int payloadLeft) {
    if (payloadLeft == 0) {
      return new MtLoBullet();
    }
    else {
      return new ConsLoBullet(
          new Bullet(this.xPos, this.yPos, payloadLeft * (360 / this.payload), this.payload + 1),
          this.explode(payloadLeft - 1));
    }
  }
}

// represents a list of bullets
interface ILoBullet {

  // Moves all bullets in a list of bullets.
  ILoBullet moveAllBulletsUp();

  // calculates the length of a list of bullets
  int length();

  // draws a list of bullets in the given world scene.
  WorldScene drawWorldScene(WorldScene scene);

  // Removes bullets from a list of bullets through
  // invoking the remove bullets helper
  ILoBullet removeBullets();

  // Checks if any bullets in a list of bullets is outside
  // the world borders. If it is, removes the bullet.
  ILoBullet removeBulletsHelper(Bullet first);

  // Returns a list of bullets only containing the bullets
  // which did not collide with any ships in the given list of ships.
  ILoBullet collision(ILoShip other);

  // Checks whether any bullets in the current list of bullets
  // collides with the given ship.
  boolean collides(Ship other);

  // Appends the current list of bullets with the given list of bullets
  ILoBullet append(ILoBullet other);

}

// represents an empty list of bullets
class MtLoBullet implements ILoBullet {

  // empty constructor
  MtLoBullet() {
  }

  // no bullets to move in an empty list of bullets
  // so just returns the current list of bullets
  public ILoBullet moveAllBulletsUp() {
    return this;
  }

  // no bullets to remove in an empty list of bullets
  // so just returns the current list of bullets
  public ILoBullet removeBullets() {
    return this;
  }

  // Checks if any bullets in a list of bullets is outside
  // the world borders. If it is, removes the bullet.
  public ILoBullet removeBulletsHelper(Bullet first) {
    if ((first.xPos < (-1 * (Bullet.BULLETRADIUS)))
        || (first.xPos > (Bullet.WIDTH + Bullet.BULLETRADIUS))
        || (first.yPos <= (-1 * (Bullet.BULLETRADIUS)))
        || (first.yPos >= (Bullet.WIDTH + (Bullet.BULLETRADIUS)))) {
      return this;
    }
    else {
      return new ConsLoBullet(first, this);
    }
  }

  // No bullets in an empty list of bullets, so returns 0 as length of list
  public int length() {
    return 0;
  }

  // no bullets to draw in an empty list of bullets, so returns given scene
  public WorldScene drawWorldScene(WorldScene scene) {
    return scene;
  }

  // An empty list of bullets has no bullets to collide
  // with the given list of ships. So just returns the current
  // list of bullets.
  public ILoBullet collision(ILoShip other) {
    return this;
  }

  // An empty list of bullets has no bullets that will
  // ever collide with the given ship
  public boolean collides(Ship other) {
    return false;
  }

  // No bullets in the current list of bullets to append to the
  // given list of bullets, so just returns the other/given list of bullets
  public ILoBullet append(ILoBullet other) {
    return other;
  }

}

// represents a list of bullets that contains at least one bullet
class ConsLoBullet implements ILoBullet {
  // the first bullet in a list of bullets
  Bullet first;
  // the rest of the bullets in a list of bullets
  ILoBullet rest;

  // constructor
  ConsLoBullet(Bullet first, ILoBullet rest) {
    this.first = first;
    this.rest = rest;
  }

  // Moves all bullets in the current list of bullets in its *respective
  // direction*
  public ILoBullet moveAllBulletsUp() {
    return new ConsLoBullet(this.first.moveBulletUp(), this.rest.moveAllBulletsUp());
  }

  // Calculates the length of the current list of bullets
  public int length() {
    return 1 + this.rest.length();
  }

  // Draws all the bullets in the current list of bullets on the given scene
  public WorldScene drawWorldScene(WorldScene scene) {
    return this.first.place(rest.drawWorldScene(scene));
  }

  // Removes bullets from a list of bullets through invoking the
  // removeBulletsHelper method
  public ILoBullet removeBullets() {
    return this.rest.removeBulletsHelper(this.first);
  }

  // Checks if any bullets in a list of bullets is outside
  // the world borders. If it is, removes the bullet.
  public ILoBullet removeBulletsHelper(Bullet first) {
    if ((first.yPos <= first.bulletRadius())
        || (first.yPos >= (Bullet.WIDTH + first.bulletRadius()))) {
      return rest.removeBulletsHelper(this.first);
    }
    else {
      return new ConsLoBullet(first, this.removeBullets());
    }
  }

  // Returns a list of bullets only containing the bullets from the current
  // list of bullets which did not collide with any ships in the given list of
  // ships.
  public ILoBullet collision(ILoShip other) {
    if (other.collides(this.first)) {
      return this.first.explode(this.first.payload).append(this.rest.collision(other));
    }
    else {
      return new ConsLoBullet(this.first, this.rest.collision(other));
    }
  }

  // Checks whether any bullets in the current list of bullets
  // collides with the given ship.
  public boolean collides(Ship ship) {
    return this.first.collision(ship) || this.rest.collides(ship);
  }

  // Appends the current list of bullets with the given list of bullets.
  public ILoBullet append(ILoBullet other) {
    return new ConsLoBullet(this.first, this.rest.append(other));
  }
}

// Examples and tests for all classes and interfaces
class ExamplesGamePieces {
  ExamplesGamePieces() {
  }

  // Example of a scene
  WorldScene scene = new WorldScene(500, 300);

  // Bullet Examples
  Bullet baseBullet = new Bullet();
  Bullet bullet0 = new Bullet(0, 0, 90, 2);
  Bullet bullet1 = new Bullet(250, 20, 90, 2);
  Bullet bullet2 = new Bullet(300, 50, 90, 2);
  Bullet bullet3 = new Bullet(400, 120, 90, 2);
  Bullet bullet4 = new Bullet(400, 120, 90, 12);
  Bullet bullet5 = new Bullet(360, 180, 90, 10);
  Bullet bullet6 = new Bullet(210, 100, 90, 6);
  Bullet outOfBoundsBullet1 = new Bullet(250, -30, 90, 2);
  Bullet outOfBoundsBullet2 = new Bullet(250, 800, 90, 2);
  Bullet inOfBoundsBullet1 = new Bullet(250, 10, 90, 2);
  Bullet inOfBoundsBullet2 = new Bullet(250, -10, 90, 2);
  Bullet bulletHitButNotCenter = new Bullet(49, 49, 90, 2);
  Bullet bulletJustDoesntHitShipWeTestWith = new Bullet(900, 900, 90, 2);

  // Ship Examples
  Ship collidingShip = new Ship(240, 10);
  Ship ship0 = new Ship(0, 0);
  Ship ship1 = new Ship(250, 20);
  Ship ship2 = new Ship(300, 50);
  Ship ship3 = new Ship(400, 120);
  Ship shipNotOut = new Ship(-10, 30);
  Ship shipOut = new Ship(-25, 60);
  Ship shipNotHit = new Ship(500, 10);
  Ship shipHitButNotOnCenter = new Ship(50, 50);
  Ship shipDoNotHitBullet = new Ship(845, 593);

  // RightShip Examples
  RightShip r1 = new RightShip(0);
  RightShip r2 = new RightShip(100);
  RightShip r3 = new RightShip(-300);
  RightShip r4 = new RightShip(20, 40);
  RightShip r5 = new RightShip(-50, 30);
  RightShip randr1 = new RightShip(400);

  // LeftShip Examples
  LeftShip l1 = new LeftShip(0);
  LeftShip l2 = new LeftShip(100);
  LeftShip l3 = new LeftShip(-300);
  LeftShip l4 = new LeftShip(20, 40);
  LeftShip l5 = new LeftShip(-50, 30);

  // List of Bullet Examples
  ILoBullet empty = new MtLoBullet();
  ILoBullet one = new ConsLoBullet(this.baseBullet, this.empty);
  ILoBullet two = new ConsLoBullet(this.baseBullet, this.one);
  ILoBullet three = new ConsLoBullet(this.baseBullet, this.two);
  ILoBullet listOfBullets = new ConsLoBullet(this.bullet0, new ConsLoBullet(this.bullet1,
      new ConsLoBullet(this.bullet2, new ConsLoBullet(this.bullet3, this.empty))));
  ILoBullet inAndOut = new ConsLoBullet(this.inOfBoundsBullet1,
      new ConsLoBullet(this.outOfBoundsBullet1, new ConsLoBullet(this.inOfBoundsBullet2,
          new ConsLoBullet(this.outOfBoundsBullet2, this.empty))));
  ILoBullet inbounds = new ConsLoBullet(this.inOfBoundsBullet1,
      new ConsLoBullet(this.inOfBoundsBullet2, this.empty));
  ILoBullet listOfBullets2 = new ConsLoBullet(this.bullet1,
      new ConsLoBullet(this.bullet2, new ConsLoBullet(this.bullet3, this.empty)));
  ILoBullet oneUp = new ConsLoBullet(new Bullet(250, 490, 90, 2), this.empty);
  ILoBullet twoUp = new ConsLoBullet(new Bullet(250, 490, 90, 2), this.oneUp);
  ILoBullet sameBulletPos = new ConsLoBullet(new Bullet(20, 20, 90, 5), this.empty);
  ILoBullet twoBulletHit = new ConsLoBullet(new Bullet(40, 40, 90, 5), this.empty);
  ILoBullet oneBulletInListHitButNotReallyOnCenter = new ConsLoBullet(this.bulletHitButNotCenter,
      new ConsLoBullet(this.bulletJustDoesntHitShipWeTestWith, this.empty));

  // List of Ships Examples
  ILoShip emptyships = new MtLoShip();
  ILoShip shipNotHitList = new ConsLoShip(this.shipNotHit, this.emptyships);
  ILoShip listOfShips1 = new ConsLoShip(this.ship0, this.emptyships);
  ILoShip listOfShips2 = new ConsLoShip(this.ship1, this.listOfShips1);
  ILoShip listOfShips3 = new ConsLoShip(this.ship2, this.listOfShips2);
  ILoShip listOfShips4 = new ConsLoShip(this.ship3, this.listOfShips3);
  ILoShip shipList1 = new ConsLoShip(this.ship0, this.emptyships);
  ILoShip shipList2 = new ConsLoShip(this.ship0, this.shipList1);
  ILoShip shipList3 = new ConsLoShip(this.ship0, this.shipList2);
  ILoShip shipListNoneOut = new ConsLoShip(this.ship0,
      new ConsLoShip(this.shipNotOut, new ConsLoShip(this.shipNotOut,
          new ConsLoShip(this.ship0, new ConsLoShip(this.shipNotOut, this.emptyships)))));
  ILoShip shipListSomeOut = new ConsLoShip(this.ship0,
      new ConsLoShip(this.shipOut, new ConsLoShip(this.shipOut,
          new ConsLoShip(this.ship0, new ConsLoShip(this.shipOut, this.emptyships)))));
  ILoShip shipSamePos = new ConsLoShip(new Ship(20, 20),
      new ConsLoShip(new Ship(20, 20), this.emptyships));
  ILoShip oneShipHit = new ConsLoShip(new Ship(40, 40), this.emptyships);
  ILoShip oneShipInListHitButNotReallyOnCenter = new ConsLoShip(this.shipHitButNotOnCenter,
      new ConsLoShip(this.shipDoNotHitBullet, this.emptyships));

  ///////////////////// Tests for Ships /////////////////////

  // tests place method for ship

  boolean testplacemethodShip(Tester t) {
    return t.checkExpect(this.ship1.place(this.scene),
        scene.placeImageXY(new CircleImage(10, OutlineMode.SOLID, Color.blue), 250, 20))
        && t.checkExpect(this.ship2.place(this.scene),
            scene.placeImageXY(new CircleImage(10, OutlineMode.SOLID, Color.blue), 300, 50));
  }

  // tests drawShip method for Ship

  boolean testdrawShipMethod(Tester t) {
    return t.checkExpect(this.ship0.drawShip(), new CircleImage(10, OutlineMode.SOLID, Color.blue))
        && t.checkExpect(this.ship2.drawShip(), new CircleImage(10, OutlineMode.SOLID, Color.blue))
        && t.checkExpect(this.ship3.drawShip(), new CircleImage(10, OutlineMode.SOLID, Color.blue));
  }

  // tests moveShip method for Ship

  boolean testmoveShipMethod(Tester t) {
    return t.checkExpect(this.ship0.moveShip(), new Ship(4, 0))
        && t.checkExpect(this.ship1.moveShip(), new Ship(254, 20))
        && t.checkExpect(this.ship2.moveShip(), new Ship(304, 50))
        && t.checkExpect(this.ship3.moveShip(), new Ship(404, 120));
  }

  // tests moveShip for RightShip

  boolean testmoveShipRight(Tester t) {
    return t.checkExpect(this.r1.moveShip(), new RightShip(496, 0))
        && t.checkExpect(this.r2.moveShip(), new RightShip(496, 100))
        && t.checkExpect(this.r3.moveShip(), new RightShip(496, -300))
        && t.checkExpect(this.r4.moveShip(), new RightShip(16, 40))
        && t.checkExpect(this.r5.moveShip(), new RightShip(-54, 30));
  }

  // tests moveShip for LeftShip

  boolean testmoveShipLeft(Tester t) {
    return t.checkExpect(this.l1.moveShip(), new LeftShip(4, 0))
        && t.checkExpect(this.l2.moveShip(), new LeftShip(4, 100))
        && t.checkExpect(this.l3.moveShip(), new LeftShip(4, -300))
        && t.checkExpect(this.l4.moveShip(), new LeftShip(24, 40))
        && t.checkExpect(this.l5.moveShip(), new LeftShip(-46, 30));
  }

  // tests collision method for Ship

  boolean testCollisionMethodShip(Tester t) {
    return t.checkExpect(this.ship0.collision(this.bullet0), true)
        && t.checkExpect(this.ship1.collision(this.bullet0), false)
        && t.checkExpect(this.ship1.collision(this.bullet1), true)
        && t.checkExpect(this.ship1.collision(this.bullet2), false)
        && t.checkExpect(this.ship2.collision(this.bullet3), false)
        && t.checkExpect(this.ship1.collision(this.bullet3), false)
        && t.checkExpect(this.shipHitButNotOnCenter.collision(this.bulletHitButNotCenter), true);
  }

  ///////////////////// Tests for ILoShip //////////////////////

  // tests for moveAllShips method

  boolean testmoveAllShipsMethod(Tester t) {
    return t.checkExpect(this.emptyships.moveAllShips(), this.emptyships)
        && t.checkExpect(this.listOfShips1.moveAllShips(),
            new ConsLoShip(new Ship(4, 0), new MtLoShip()))
        && t.checkExpect(this.listOfShips2.moveAllShips(),
            new ConsLoShip(new Ship(254, 20), new ConsLoShip(new Ship(4, 0), new MtLoShip())))
        && t.checkExpect(this.listOfShips3.moveAllShips(), new ConsLoShip(new Ship(304, 50),
            new ConsLoShip(new Ship(254, 20), new ConsLoShip(new Ship(4, 0), new MtLoShip()))));

  }

  // tests for removeShips and removeShipsHelper method

  boolean testremoveShipsMethod(Tester t) {
    return t.checkExpect(this.emptyships.removeShips(), this.emptyships)
        && t.checkExpect(this.emptyships.removeShipsHelper(this.shipOut), this.emptyships)
        && t.checkExpect(this.emptyships.removeShipsHelper(this.shipNotOut),
            new ConsLoShip(this.shipNotOut, this.emptyships))
        && t.checkExpect(this.shipListSomeOut.removeShips(), this.shipList2)
        && t.checkExpect(this.shipList2.removeShips(), this.shipList2)
        && t.checkExpect(this.shipListNoneOut.removeShips(), this.shipListNoneOut)
        && t.checkExpect(this.listOfShips1.removeShipsHelper(this.shipOut), this.listOfShips1)
        && t.checkExpect(this.listOfShips2.removeShipsHelper(this.shipOut), this.listOfShips2)
        && t.checkExpect(this.listOfShips1.removeShipsHelper(this.shipNotOut),
            new ConsLoShip(this.shipNotOut, this.listOfShips1))
        && t.checkExpect(this.listOfShips2.removeShipsHelper(this.shipNotOut),
            new ConsLoShip(this.shipNotOut, this.listOfShips2));
  }

  // tests for Length method

  boolean testlengthsMethodShips(Tester t) {
    return t.checkExpect(this.emptyships.length(), 0)
        && t.checkExpect(this.listOfShips1.length(), 1)
        && t.checkExpect(this.listOfShips2.length(), 2)
        && t.checkExpect(this.listOfShips3.length(), 3)
        && t.checkExpect(this.listOfShips4.length(), 4);

  }

  // tests for collision Method for Ships

  boolean testCollisionMethodShips(Tester t) {
    return t.checkExpect(this.emptyships.collision(this.empty), this.emptyships)
        && t.checkExpect(this.emptyships.collision(this.one), this.emptyships)
        && t.checkExpect(this.emptyships.collision(this.two), this.emptyships)
        && t.checkExpect(this.listOfShips1.collision(this.empty),
            new ConsLoShip(this.ship0, this.emptyships))
        && t.checkExpect(this.listOfShips1.collision(this.one),
            new ConsLoShip(this.ship0, this.emptyships))
        && t.checkExpect(this.listOfShips2.collision(this.two),
            new ConsLoShip(new Ship(250, 20), new ConsLoShip(this.ship0, this.emptyships)))
        && t.checkExpect(this.listOfShips4.collision(this.inbounds),
            new ConsLoShip(new Ship(400, 120),
                new ConsLoShip(new Ship(300, 50), new ConsLoShip(this.ship0, this.emptyships))))
        && t.checkExpect(this.shipSamePos.collision(this.sameBulletPos), this.emptyships);
  }

  // tests for collides Method for Ships

  boolean testColldiesMethodShips(Tester t) {
    return t.checkExpect(this.emptyships.collides(this.bullet0), false)
        && t.checkExpect(this.emptyships.collides(this.bullet2), false)
        && t.checkExpect(this.listOfShips1.collides(this.bullet0), true)
        && t.checkExpect(this.listOfShips2.collides(this.bullet2), false)
        && t.checkExpect(this.listOfShips3.collides(this.bullet3), false)
        && t.checkExpect(this.listOfShips2.collides(this.bullet3), false)
        && t.checkExpect(this.listOfShips1.collides(this.bullet2), false)
        && t.checkExpect(this.listOfShips4.collides(this.bullet2), true) && t.checkExpect(
            this.oneShipInListHitButNotReallyOnCenter.collides(this.bulletHitButNotCenter), true);
  }

  // tests for drawWorldScene Method for Ships

  boolean testdrawWorldSceneMethodShips(Tester t) {
    return t.checkExpect(this.emptyships.drawWorldScene(this.scene), this.scene)
        && t.checkExpect(this.listOfShips1.drawWorldScene(this.scene),
            this.scene.placeImageXY(new CircleImage(10, OutlineMode.SOLID, Color.blue), 0, 0))
        && t.checkExpect(this.listOfShips2.drawWorldScene(this.scene),
            this.scene.placeImageXY(new CircleImage(10, OutlineMode.SOLID, Color.blue), 250, 20)
                .placeImageXY(new CircleImage(10, OutlineMode.SOLID, Color.blue), 0, 0));
  }

  ///////////////////// Tests for Bullets //////////////////////

  // tests place method for bullet

  boolean testplacemethodBullet(Tester t) {
    return t.checkExpect(this.bullet0.place(this.scene),
        scene.placeImageXY(new CircleImage(6, OutlineMode.SOLID, Color.red), 0, 0))
        && t.checkExpect(this.bullet1.place(this.scene),
            scene.placeImageXY(new CircleImage(6, OutlineMode.SOLID, Color.red), 250, 20));
  }

  // tests for bulletRadius Method

  boolean testbulletRadius(Tester t) {
    return t.checkExpect(this.baseBullet.bulletRadius(), 6)
        && t.checkExpect(this.bullet0.bulletRadius(), 6)
        && t.checkExpect(this.bullet1.bulletRadius(), 6)
        && t.checkExpect(this.bullet2.bulletRadius(), 6)
        && t.checkExpect(this.bullet3.bulletRadius(), 6)
        && t.checkExpect(this.bullet4.bulletRadius(), 10)
        && t.checkExpect(this.bullet5.bulletRadius(), 10)
        && t.checkExpect(this.bullet6.bulletRadius(), 10);
  }

  // tests for drawBullet Method

  boolean testdrawBulletMethod(Tester t) {
    return t.checkExpect(this.baseBullet.drawBullet(),
        new CircleImage(6, OutlineMode.SOLID, Color.red))
        && t.checkExpect(this.bullet0.drawBullet(),
            new CircleImage(6, OutlineMode.SOLID, Color.red))
        && t.checkExpect(this.bullet1.drawBullet(),
            new CircleImage(6, OutlineMode.SOLID, Color.red))
        && t.checkExpect(this.bullet2.drawBullet(),
            new CircleImage(6, OutlineMode.SOLID, Color.red))
        && t.checkExpect(this.bullet3.drawBullet(),
            new CircleImage(6, OutlineMode.SOLID, Color.red))
        && t.checkExpect(this.bullet4.drawBullet(),
            new CircleImage(10, OutlineMode.SOLID, Color.red))
        && t.checkExpect(this.bullet5.drawBullet(),
            new CircleImage(10, OutlineMode.SOLID, Color.red))
        && t.checkExpect(this.bullet6.drawBullet(),
            new CircleImage(10, OutlineMode.SOLID, Color.red));
  }

  // tests for moveBulletUpMethod

  boolean testmoveBulletUpMethod(Tester t) {
    return t.checkExpect(this.baseBullet.moveBulletUp(), new Bullet(250, 292, 90, 2))
        && t.checkExpect(this.bullet0.moveBulletUp(), new Bullet(0, -8, 90, 2))
        && t.checkExpect(this.bullet1.moveBulletUp(), new Bullet(250, 12, 90, 2))
        && t.checkExpect(this.bullet4.moveBulletUp(), new Bullet(400, 112, 90, 12))
        && t.checkExpect(this.bullet5.moveBulletUp(), new Bullet(360, 172, 90, 10))
        && t.checkExpect(this.bullet6.moveBulletUp(), new Bullet(210, 92, 90, 6));
  }

  // tests Collision method for a single Bullet

  boolean testCollisionMethodSingleBullet(Tester t) {
    return t.checkExpect(this.bullet0.collision(this.ship0), true)
        && t.checkExpect(this.bullet1.collision(this.ship0), false)
        && t.checkExpect(this.bullet2.collision(this.ship0), false)
        && t.checkExpect(this.bullet3.collision(this.ship0), false)
        && t.checkExpect(this.bulletHitButNotCenter.collision(this.shipHitButNotOnCenter), true);
  }

  // test for Explode method for Bullet

  boolean testExplodeMethodBullet(Tester t) {
    return t.checkExpect(this.bullet0.explode(0), this.empty)
        && t.checkExpect(this.bullet1.explode(1),
            new ConsLoBullet(new Bullet(250, 20, 180, 3), this.empty))
        && t.checkExpect(this.bullet2.explode(2),
            new ConsLoBullet(new Bullet(300, 50, 360, 3),
                new ConsLoBullet(new Bullet(300, 50, 180, 3), this.empty)))
        && t.checkExpect(this.bullet3.explode(3),
            new ConsLoBullet(new Bullet(400, 120, 540, 3),
                new ConsLoBullet(new Bullet(400, 120, 360, 3),
                    new ConsLoBullet(new Bullet(400, 120, 180, 3), this.empty))))
        && t.checkExpect(this.bullet4.explode(0), this.empty);
  }

  ///////////////////// Tests for ILoBullet //////////////////////

  // tests for moveAllBulletsUpmethod

  boolean testmoveAllBulletsUpMethod(Tester t) {
    return t.checkExpect(this.listOfBullets.moveAllBulletsUp(),
        new ConsLoBullet(new Bullet(0, -8, 90, 2),
            new ConsLoBullet(new Bullet(250, 12, 90, 2),
                new ConsLoBullet(new Bullet(300, 42, 90, 2),
                    new ConsLoBullet(new Bullet(400, 112, 90, 2), this.empty)))))
        && t.checkExpect(this.empty.moveAllBulletsUp(), this.empty)
        && t.checkExpect(this.inAndOut.moveAllBulletsUp(),
            new ConsLoBullet(new Bullet(250, 2, 90, 2),
                new ConsLoBullet(new Bullet(250, -38, 90, 2),
                    new ConsLoBullet(new Bullet(250, -18, 90, 2),
                        new ConsLoBullet(new Bullet(250, 792, 90, 2), this.empty)))))
        && t.checkExpect(this.inbounds.moveAllBulletsUp(),
            new ConsLoBullet(new Bullet(250, 2, 90, 2),
                new ConsLoBullet(new Bullet(250, -18, 90, 2), this.empty)))
        && t.checkExpect(this.empty.moveAllBulletsUp(), this.empty);
  }

  // tests for Length method for Bullets

  boolean testLengthMethodForBullets(Tester t) {
    return t.checkExpect(this.empty.length(), 0) && t.checkExpect(this.one.length(), 1)
        && t.checkExpect(this.two.length(), 2) && t.checkExpect(this.three.length(), 3)
        && t.checkExpect(this.listOfBullets.length(), 4)
        && t.checkExpect(this.inAndOut.length(), 4);
  }

  // tests for Append method for Bullets

  boolean testappendMethodForBullets(Tester t) {
    return t.checkExpect(this.empty.append(this.empty), this.empty)
        && t.checkExpect(this.empty.append(this.one), this.one)
        && t.checkExpect(this.empty.append(this.two), this.two)
        && t.checkExpect(this.one.append(this.empty), this.one)
        && t.checkExpect(this.one.append(this.two),
            new ConsLoBullet(new Bullet(250, 300, 90, 2),
                new ConsLoBullet(this.baseBullet, new ConsLoBullet(this.baseBullet, this.empty))))
        && t.checkExpect(this.two.append(this.one),
            new ConsLoBullet(new Bullet(250, 300, 90, 2),
                new ConsLoBullet(this.baseBullet, new ConsLoBullet(this.baseBullet, this.empty))))
        && t.checkExpect(this.two.append(this.three),
            new ConsLoBullet(new Bullet(250, 300, 90, 2),
                new ConsLoBullet(this.baseBullet,
                    new ConsLoBullet(this.baseBullet,
                        new ConsLoBullet(this.baseBullet,
                            new ConsLoBullet(this.baseBullet, this.empty))))))
        && t.checkExpect(this.one.append(this.three),
            new ConsLoBullet(new Bullet(250, 300, 90, 2), new ConsLoBullet(this.baseBullet,
                new ConsLoBullet(this.baseBullet, new ConsLoBullet(this.baseBullet, this.empty)))));
  }

  // tests for removeBullets and removeBulletsHelper Method

  boolean testremoveBulletsMethod(Tester t) {
    return t.checkExpect(this.empty.removeBullets(), this.empty)
        && t.checkExpect(this.one.removeBullets(),
            new ConsLoBullet(new Bullet(250, 300, 90, 2), this.empty))
        && t.checkExpect(this.two.removeBullets(),
            new ConsLoBullet(new Bullet(250, 300, 90, 2),
                new ConsLoBullet(this.baseBullet, this.empty)))
        && t.checkExpect(this.three.removeBullets(),
            new ConsLoBullet(new Bullet(250, 300, 90, 2),
                new ConsLoBullet(this.baseBullet, new ConsLoBullet(this.baseBullet, this.empty))))
        && t.checkExpect(this.inAndOut.removeBullets(),
            new ConsLoBullet(new Bullet(250, 10, 90, 2), this.empty))
        && t.checkExpect(this.empty.removeBulletsHelper(this.bullet0),
            new ConsLoBullet(new Bullet(0, 0, 90, 2), this.empty))
        && t.checkExpect(this.one.removeBulletsHelper(this.bullet2),
            new ConsLoBullet(new Bullet(300, 50, 90, 2),
                new ConsLoBullet(new Bullet(250, 300, 90, 2), this.empty)))
        && t.checkExpect(this.two.removeBulletsHelper(this.bullet3),
            new ConsLoBullet(new Bullet(400, 120, 90, 2),
                new ConsLoBullet(new Bullet(250, 300, 90, 2),
                    new ConsLoBullet(this.baseBullet, this.empty))))
        && t.checkExpect(this.three.removeBulletsHelper(this.bullet3),
            new ConsLoBullet(new Bullet(400, 120, 90, 2),
                new ConsLoBullet(new Bullet(250, 300, 90, 2),
                    new ConsLoBullet(this.baseBullet,
                        new ConsLoBullet(this.baseBullet, this.empty)))))
        && t.checkExpect(this.two.removeBulletsHelper(this.bullet2),
            new ConsLoBullet(new Bullet(300, 50, 90, 2), new ConsLoBullet(
                new Bullet(250, 300, 90, 2), new ConsLoBullet(this.baseBullet, this.empty))));
  }

  // test for Collision Method for Bullet

  boolean testCollisionMethodBullet(Tester t) {
    return t.checkExpect(this.empty.collision(this.emptyships), this.empty)
        && t.checkExpect(this.empty.collision(this.listOfShips1), this.empty)
        && t.checkExpect(this.one.collision(this.listOfShips2),
            new ConsLoBullet(new Bullet(250, 300, 90, 2), this.empty))
        && t.checkExpect(this.two.collision(this.listOfShips1),
            new ConsLoBullet(new Bullet(250, 300, 90, 2),
                new ConsLoBullet(this.baseBullet, this.empty)))
        && t.checkExpect(this.three.collision(this.listOfShips3),
            new ConsLoBullet(new Bullet(250, 300, 90, 2),
                new ConsLoBullet(this.baseBullet, new ConsLoBullet(this.baseBullet, this.empty))))
        && t.checkExpect(this.inAndOut.collision(this.emptyships), this.inAndOut)
        && t.checkExpect(this.inbounds.collision(this.listOfShips3),
            new ConsLoBullet(new Bullet(250, 10, 360, 3),
                new ConsLoBullet(new Bullet(250, 10, 180, 3),
                    new ConsLoBullet(new Bullet(250, -10, 90, 2), this.empty))))
        && t.checkExpect(this.twoBulletHit.collision(this.oneShipHit),
            new ConsLoBullet(new Bullet(40, 40, 360, 6),
                new ConsLoBullet(new Bullet(40, 40, 288, 6),
                    new ConsLoBullet(new Bullet(40, 40, 216, 6),
                        new ConsLoBullet(new Bullet(40, 40, 144, 6),
                            new ConsLoBullet(new Bullet(40, 40, 72, 6), this.empty))))));
  }

  // test for Collides method for Bullet

  boolean testCollidesMethodBullet(Tester t) {
    return t.checkExpect(this.empty.collides(this.ship0), false)
        && t.checkExpect(this.one.collides(this.ship1), false)
        && t.checkExpect(this.two.collides(this.ship2), false)
        && t.checkExpect(this.three.collides(this.ship3), false)
        && t.checkExpect(this.listOfBullets.collides(this.ship0), true)
        && t.checkExpect(this.inAndOut.collides(this.ship1), true)
        && t.checkExpect(this.inbounds.collides(this.ship2), false) && t.checkExpect(
            this.oneBulletInListHitButNotReallyOnCenter.collides(this.shipHitButNotOnCenter), true);
  }

  // test drawWorldScene method for Bullet

  boolean testdrawWorldSceneMethodBulet(Tester t) {
    return t.checkExpect(this.empty.drawWorldScene(this.scene), this.scene)
        && t.checkExpect(this.two.drawWorldScene(this.scene),
            this.scene.placeImageXY(new CircleImage(6, OutlineMode.SOLID, Color.red), 250, 300)
                .placeImageXY(new CircleImage(6, OutlineMode.SOLID, Color.red), 250, 300))
        && t.checkExpect(this.listOfBullets.drawWorldScene(this.scene),
            this.scene.placeImageXY(new CircleImage(6, OutlineMode.SOLID, Color.red), 400, 120)
                .placeImageXY(new CircleImage(6, OutlineMode.SOLID, Color.red), 300, 50)
                .placeImageXY(new CircleImage(6, OutlineMode.SOLID, Color.red), 250, 20)
                .placeImageXY(new CircleImage(6, OutlineMode.SOLID, Color.red), 0, 0));
  }
}
