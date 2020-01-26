package tetris

import zio.{Schedule, Task}
import zio.actors._
import zio.duration._

class Controller(val gameActor: ActorRef[Throwable, GameMessage]) {
  def movePieceLeft() = gameActor ! MoveLeft
  def movePieceRight() = gameActor ! MoveRight
  def movePieceDown() = gameActor ! Tick
  def rotatePiece() = gameActor ! Rotate90
  def dropPiece() = gameActor ! Drop
  def getView = gameActor ? GetView
  val tick = { gameActor ! Tick }.repeat(Schedule.spaced(501.milliseconds))
}

case class GameView( //
  blocks: Seq[Block],
  gridSize: GridSize,
  current: Piece,
  status: GameStatus)
