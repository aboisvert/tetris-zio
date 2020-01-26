package tetris

object Types {
  type X = Int
  type Y = Int

  type Xd = Double
  type Yd = Double
}

import Types._

case class Position(x: X, y: Y)

case class PositionD(x: Xd, y: Yd)

object PositionD {
  def fromTuple(t: (Xd, Yd)): PositionD = {
    val (x, y) = t
    PositionD(x, y)
  }
}

case class Block( //
  pos: Position,
  kind: PieceKind)

sealed class PieceKind(val shape: Seq[(Xd, Yd)])
case object IKind extends PieceKind(Seq((-1.5, 0.0), (-0.5, 0.0), (0.5, 0.0), (1.5, 0.0)))
case object JKind extends PieceKind(Seq((-1.0, 0.5), (0.0, 0.5), (1.0, 0.5), (1.0, -0.5)))
case object LKind extends PieceKind(Seq((-1.0, 0.5), (0.0, 0.5), (1.0, 0.5), (-1.0, -0.5)))
case object OKind extends PieceKind(Seq((-0.5, 0.5), (0.5, 0.5), (-0.5, -0.5), (0.5, -0.5)))
case object SKind extends PieceKind(Seq((0.0, 0.5), (1.0, 0.5), (-1.0, -0.5), (0.0, -0.5)))
case object TKind extends PieceKind(Seq((-1.0, 0.0), (0.0, 0.0), (1.0, 0.0), (0.0, 1.0)))
case object ZKind extends PieceKind(Seq((-1.0, 0.5), (0.0, 0.5), (0.0, -0.5), (1.0, -0.5)))

object PieceKind {
  val kinds = Seq(IKind, JKind, LKind, OKind, SKind, TKind, ZKind)
}

case class Piece(pos: PositionD, kind: PieceKind, coords: Seq[PositionD]) {

  def occupiedBlocks: Seq[Block] =
    coords map {
      case PositionD(x, y) =>
        Block(Position(math.floor(x + pos.x).toInt, math.floor(y + pos.y).toInt), kind)
    }

  def occupiedBlockPositions = occupiedBlocks map (_.pos)

  def moveBy(delta: (Xd, Yd)): Piece =
    copy(pos = PositionD(pos.x + delta._1, pos.y + delta._2))

  def rotateBy(theta: Double): Piece = {
    val c = math.cos(theta)
    val s = math.sin(theta)
    def roundToHalf(v: PositionD): PositionD =
      PositionD(math.round(v.x * 2.0) * 0.5, math.round(v.y * 2.0) * 0.5)
    copy(
      coords = coords
        .map { case PositionD(x, y) => PositionD(x * c - y * s, x * s + y * c) }
        .map(roundToHalf)
    )
  }
}

object Piece {
  /** Initialize a piece with its default kind's shape */
  def apply(pos: PositionD, kind: PieceKind): Piece =
    Piece(pos, kind, kind.shape map { PositionD.fromTuple })
}
