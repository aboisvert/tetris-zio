package tetris

import scala.annotation.tailrec
import scala.util.Random
import Helpers.IntWithTimes

sealed trait GameStatus
case object ActiveStatus extends GameStatus
case object GameOver extends GameStatus

case class GridSize(columns: Int, rows: Int)

case class GameState(
  blocks: Seq[Block],
  gridSize: GridSize,
  currentPiece: Piece,
  comingPieces: Seq[PieceKind],
  status: GameStatus = ActiveStatus) {
  import gridSize.{columns, rows}

  def view = GameView(blocks, gridSize, currentPiece, status)

  def clearOccupiedBlocks(p: Piece): GameState = {
    this.copy(blocks = blocks filterNot { p.occupiedBlockPositions contains _.pos })
  }

  def clearRow(row: Int): GameState = {
    val rowsBefore = blocks.filter { _.pos.y < row }
    val rowsAfter = blocks.filter { _.pos.y > row }
    val rowsAfterShiftedByOne = rowsAfter
      .map { b =>
        b.copy(pos = Position(b.pos.x, b.pos.y - 1))
      }
    this.copy(rowsBefore ++ rowsAfterShiftedByOne)
  }

  def markOccupiedBlocks(p: Piece): GameState = this.copy(blocks = blocks ++ p.occupiedBlocks)

  def pieceFits(p: Piece): Boolean = {
    def inBounds(pos: Position) =
      (pos.x >= 0) && (pos.x < columns) && (pos.y >= 0) && (pos.y < rows)
    val occupiedPositions = p.occupiedBlockPositions
    ((occupiedPositions forall inBounds) &&
    (this.blocks.map(_.pos) intersect occupiedPositions).isEmpty)
  }
}

object GameState {

  def randomPieces(random: util.Random): LazyList[PieceKind] =
    PieceKind.kinds(random.nextInt(PieceKind.kinds.size)) #:: randomPieces(random)

  val defaultGameState = newState(Nil, GridSize(10, 23), randomPieces(new Random))

  def newState(blocks: Seq[Block], gridSize: GridSize, comingPieces: Seq[PieceKind]): GameState = {
    val dummy = Piece(PositionD(0, 0), OKind)
    val initialDummyState =
      placeNextPiece(GameState(Nil, gridSize, dummy, comingPieces)).copy(blocks = blocks)
    placeNextPiece(initialDummyState)
  }

  val moveLeft = moveCurrentPiece { _.moveBy(-1.0, 0.0) }
  val moveRight = moveCurrentPiece { _.moveBy(1.0, 0.0) }
  val rotate90 = moveCurrentPiece { _.rotateBy(math.Pi / 2.0) }
  val down = moveCurrentPiece { _.moveBy(0.0, -1.0) }
  val tick = moveCurrentPiece(_.moveBy(0.0, -1.0), (clearFullRows _) andThen (placeNextPiece _))

  def drop(prevState: GameState): GameState = {
    val downAllTheWay = prevState.gridSize.rows * down
    Function.chain(downAllTheWay :+ tick)(prevState)
  }

  def clearFullRows(prevState: GameState): GameState = {
    import prevState.gridSize.{columns, rows}

    def isFullRow(i: Int, s: GameState) = s.blocks.count(_.pos.y == i) == columns

    @tailrec def clearRow(i: Int, s: GameState): GameState =
      if (i < 0) s
      else if (isFullRow(i, s)) clearRow(i - 1, s.clearRow(i))
      else clearRow(i - 1, s)

    clearRow(rows - 1, prevState)
  }

  def placeNextPiece(prevState: GameState): GameState = {
    import prevState.gridSize.{columns, rows}
    def dropOffPos = PositionD(columns / 2.0, rows - 2.0)
    val nextPiece = Piece(dropOffPos, prevState.comingPieces.head)
    def nextState =
      prevState
        .copy(
          blocks = prevState.blocks,
          currentPiece = nextPiece,
          comingPieces = prevState.comingPieces.tail
        )
        .markOccupiedBlocks(nextPiece)
    if (prevState.pieceFits(nextPiece)) nextState
    else nextState.copy(status = GameOver)
  }

  private def moveCurrentPiece(
    trans: Piece => Piece,
    onFail: GameState => GameState = identity
  ): GameState => GameState = { (prevState: GameState) =>
    prevState.status match {
      case ActiveStatus =>
        val nextState = prevState
          .clearOccupiedBlocks(prevState.currentPiece)
          .copy(currentPiece = trans(prevState.currentPiece))
        import nextState._
        if (pieceFits(currentPiece)) markOccupiedBlocks(currentPiece)
        else onFail(prevState)
      case GameOver => prevState
    }
  }
}
