package tetris

import javax.swing.SwingUtilities
import scala.swing._
import scala.swing.event._
import zio._

object Tetris extends SimpleSwingApplication {
  import event.Key._
  import java.awt.{Dimension, Graphics, Graphics2D, Image, Rectangle}
  import java.awt.{Color => AWTColor}
  import javax.swing.{Timer => SwingTimer, AbstractAction}

  val bluishGray = new AWTColor(48, 99, 99)
  val bluishLigherGray = new AWTColor(79, 130, 130)
  val bluishEvenLigher = new AWTColor(145, 196, 196)
  val bluishSilver = new AWTColor(210, 255, 255)

  val blockSize = 16
  val blockMargin = 1
  val mainPanelSize = new Dimension(200, 350)
  val unit = blockSize + blockMargin

  val runtime = new DefaultRuntime {}

  val actors = runtime.unsafeRun {
    Actors.initActors(stateUpdatedHook = Task.effect {
      SwingUtilities.invokeLater(() => mainPanel.repaint())
    })
  }

  val controller = new Controller(actors)

  runtime.unsafeRunAsync { controller.tick } { exit => /* no-op */ }

  def onKeyPress(keyCode: Value) = {
    val effect = keyCode match {
      case Left  => controller.movePieceLeft()
      case Right => controller.movePieceRight()
      case Up    => controller.rotatePiece()
      case Down  => controller.movePieceDown()
      case Space => controller.dropPiece()
      case other => IO.effect { /* no-op */ }
    }
    runtime.unsafeRun(effect)
  }

  def onPaint(g: Graphics2D): Unit = {
    val view = runtime.unsafeRun { controller.getView }
    val xOffset = mainPanelSize.width / 2
    drawBoard(g, Position(10, 5), GridSize(10, 20), view.blocks, view.current.occupiedBlocks)
    drawStatus(g, Position(2 * unit, 10), view)
  }

  def drawStatus(g: Graphics2D, offset: Position, view: GameView): Unit = {
    g.setColor(bluishSilver)
    g.setFont(Font.apply("monospaced", Font.Bold, 24))
    if (view.status == GameOver) {
      g.drawString("game over", offset.x, offset.y + 8 * unit)
    }
  }

  def drawBoard(
    g: Graphics2D,
    offset: Position,
    gridSize: GridSize,
    blocks: Seq[Block],
    current: Seq[Block]
  ): Unit = {
    def buildRect(pos: Position): Rectangle =
      new Rectangle(
        offset.x + pos.x * (blockSize + blockMargin),
        offset.y + (gridSize.rows - pos.y - 1) * (blockSize + blockMargin),
        blockSize,
        blockSize
      )
    def drawEmptyGrid(): Unit = {
      g setColor bluishLigherGray
      for {
        x <- 0 to gridSize.columns - 1
        y <- 0 to gridSize.rows - 1
        pos = Position(x, y)
      } g draw buildRect(pos)
    }
    def drawBlocks(): Unit = {
      g.setColor(bluishEvenLigher)
      blocks
        .filter(_.pos.y < gridSize.rows)
        .foreach { b =>
          g.fill(buildRect(b.pos))
        }
    }
    def drawCurrent(): Unit = {
      g.setColor(bluishSilver)
      current
        .filter(_.pos.y < gridSize.rows)
        .foreach { b =>
          g.fill(buildRect(b.pos))
        }
    }
    drawEmptyGrid()
    drawBlocks()
    drawCurrent()
  }

  override lazy val top = new MainFrame {
    title = "tetris"
    contents = mainPanel
  }

  lazy val mainPanel: Panel = new Panel {
    preferredSize = mainPanelSize
    focusable = true
    listenTo(keys)

    reactions += {
      case KeyPressed(_, key, _, _) => onKeyPress(key)
    }

    override def paint(g: Graphics2D): Unit = {
      g.setColor(bluishGray)
      g.fillRect(0, 0, size.width, size.height)
      onPaint(g)
    }
  }
}
