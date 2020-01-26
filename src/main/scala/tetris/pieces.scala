package tetris

case class Position[T](x: T, y: T)

object Position {
  def fromTuple[T](t: (T, T)): Position[T] = {
    val (x, y) = t
    Position(x, y)
  }
}

case class Block( //
  pos: Position[Int],
  kind: PieceKind)

sealed class PieceKind(val shape: Seq[(Double, Double)])
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

case class Piece(pos: Position[Double], kind: PieceKind, coords: Seq[Position[Double]]) {

  def occupiedBlocks: Seq[Block] =
    coords map {
      case Position(x, y) =>
        Block(Position(math.floor(x + pos.x).toInt, math.floor(y + pos.y).toInt), kind)
    }

  def occupiedBlockPositions = occupiedBlocks map (_.pos)

  def moveBy(delta: (Double, Double)): Piece =
    copy(pos = Position(pos.x + delta._1, pos.y + delta._2))

  def rotateBy(theta: Double): Piece = {
    val c = math.cos(theta)
    val s = math.sin(theta)
    def roundToHalf(v: Position[Double]): Position[Double] =
      Position(math.round(v.x * 2.0) * 0.5, math.round(v.y * 2.0) * 0.5)
    copy(
      coords = coords
        .map { case Position(x, y) => Position(x * c - y * s, x * s + y * c) }
        .map(roundToHalf)
    )
  }
}

object Piece {
  /** Initialize a piece with its default kind's shape */
  def apply(pos: Position[Double], kind: PieceKind): Piece =
    Piece(pos, kind, kind.shape map { Position.fromTuple })
}
