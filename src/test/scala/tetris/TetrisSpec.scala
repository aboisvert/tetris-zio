package tetris

import zio.test._
import zio.test.Assertion._

object Fixtures {
  import GameState._

  def ttt = Nil padTo (20, TKind)
  /*
  def s0 = newState(Nil, (10, 20), ttt)
  def s1 = newState(Block(Position(0, 0), TKind) :: Nil, (10, 20), ttt)
  def s2 = newState(Block(Position(3, 18), TKind) :: Nil, (10, 20), ttt)

  def s3 =
    newState(
      Seq((0, 0), (1, 0), (2, 0), (3, 0), (7, 0), (8, 0), (9, 0))
        .map { case (x, y) => Position(x, y) }
        .map { Block(_, TKind) },
      (10, 20),
      ttt
    )
  def s4 = newState(Nil, (10, 20), OKind :: OKind :: Nil)

  def s5 =
    newState(
      Seq((0, 0), (1, 0), (2, 0), (3, 0), (4, 0), (5, 0), (6, 0), (7, 0), (9, 0))
        .map { case (x, y) => Position(x, y) }
        map { Block(_, TKind) },
      (10, 20),
      ttt
    )
  def gameOverState = Function.chain(Nil padTo (10, drop))(s1)
 */
}

import GameState._

object TetrisSpec
    extends DefaultRunnableSpec(
      suite("tetris")(
        test("newState block positions") {
          val s = newState(Nil, GridSize(10, 20), OKind :: OKind :: Nil)
          (assert(s.currentPiece.kind, equalTo(OKind))
          && assert(
            s.blocks map { _.pos },
            equalTo(Seq(Position(4, 18), Position(5, 18), Position(4, 17), Position(5, 17)))
          ))
        },
        test("moveLeft1") {
          val ts = Seq.fill(20)(TKind)
          val s = newState(Block(Position(0, 0), TKind) :: Nil, GridSize(10, 20), ts)
          assert(
            moveLeft(s).blocks map { _.pos },
            equalTo(
              Seq(
                Position(0, 0),
                Position(3, 18),
                Position(4, 18),
                Position(5, 18),
                Position(4, 19)
              )
            )
          )
        }

        /*
        def leftWall1 =
          Function.chain(moveLeft :: moveLeft :: moveLeft ::
              moveLeft :: moveLeft :: Nil)(s1).
            blocks map {_.pos} must contain(exactly(
            (0, 0), (0, 18), (1, 18), (2, 18), (1, 19)
          )).inOrder
        def leftHit1 =
          moveLeft(s2).blocks map {_.pos} must contain(exactly(
            (3, 18), (4, 18), (5, 18), (6, 18), (5, 19)
          )).inOrder
        def right1 =
          moveRight(s1).blocks map {_.pos} must contain(exactly(
            (0, 0), (5, 18), (6, 18), (7, 18), (6, 19)
          )).inOrder
        def rotate1 =
          rotateCW(s1).blocks map {_.pos} must contain(exactly(
            (0, 0), (5, 19), (5, 18), (5, 17), (6, 18)
          )).inOrder
        def tick1 =
          tick(s1).blocks map {_.pos} must contain(exactly(
            (0, 0), (4, 17), (5, 17), (6, 17), (5, 18)
          )).inOrder
        def tick2 =
          Function.chain(Nil padTo (19, tick))(s1).
          blocks map {_.pos} must contain(exactly(
            (0, 0), (4, 0), (5, 0), (6, 0), (5, 1),
            (4, 18), (5, 18), (6, 18), (5, 19)
          )).inOrder
        def tick3 =
          Function.chain(Nil padTo (19, tick))(s3).
          blocks map {_.pos} must contain(exactly(
            (5, 0), (4, 18), (5, 18), (6, 18), (5, 19)
          )).inOrder
        def drop1 =
          drop(s1).blocks map {_.pos} must contain(exactly(
            (0, 0), (4, 0), (5, 0), (6, 0), (5, 1),
            (4, 18), (5, 18), (6, 18), (5, 19)
          )).inOrder
        def spawn1 =
          Function.chain(Nil padTo (10, drop))(s1).status must_==
          GameOver
        def line1 =
          (s3.lineCount must_== 0) and
          (Function.chain(Nil padTo (19, tick))(s3).
          lineCount must_== 1) and
          (Function.chain(Nil padTo (19, tick))(s3).
          lastDeleted must_== 1)
        def attack1 =
          notifyAttack(s1).pendingAttacks must_== 1
        def attack2 =
          Function.chain(notifyAttack :: drop :: Nil)(s1).blocks map {_.pos} must contain(
            (0, 1), (4, 1), (5, 1), (6, 1), (5, 2),
            (4, 18), (5, 18), (6, 18), (5, 19)
          )
       */
      )
    )
