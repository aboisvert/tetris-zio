package tetris

object Helpers {
  implicit class IntWithTimes(n: Int) {
    def *[A](f: => A): Seq[A] = for (_ <- 1 to n) yield f
  }
}
