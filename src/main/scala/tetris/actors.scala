package tetris

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import zio._
import zio.actors._

sealed trait GameMessage[+RESPONSE]
case object MoveLeft extends GameMessage[Unit]
case object MoveRight extends GameMessage[Unit]
case object Rotate90 extends GameMessage[Unit]
case object Tick extends GameMessage[Unit]
case object Drop extends GameMessage[Unit]
case object GetView extends GameMessage[GameView]

case class GameActorState(gameState: GameState, stateUpdatedHook: Task[Unit])

object GameActor extends Actor.Stateful[GameActorState, Throwable, GameMessage] {
  import GameState._

  override def receive[T](
    state: GameActorState,
    msg: GameMessage[T],
    context: Context
  ): Task[(GameActorState, T)] = {
    msg match {
      case MoveLeft  => updateState(state) { moveLeft }
      case MoveRight => updateState(state) { moveRight }
      case Rotate90  => updateState(state) { rotate90 }
      case Tick      => updateState(state) { tick }
      case Drop      => updateState(state) { drop }
      case GetView   => IO.effectTotal { (state, state.gameState.view) }
    }
  }

  private[this] def updateState(
    state: GameActorState
  )(
    transform: GameState => GameState
  ): Task[(GameActorState, Unit)] = {
    for {
      newState <- IO.effectTotal { state.copy(gameState = transform(state.gameState)) }
      _ <- state.stateUpdatedHook
    } yield (newState, ())
  }
}

object Actors {

  def initActors(
    initialState: GameState = GameState.defaultGameState,
    stateUpdatedHook: Task[Unit]
  ): Task[ActorRef[Throwable, GameMessage]] = {
    for {
      actorSystem <- ActorSystem("mySystem", remoteConfig = None)
      gameActor <- actorSystem.make(
        "gameActor",
        Supervisor.none,
        GameActorState(initialState, stateUpdatedHook),
        GameActor
      )
    } yield gameActor
  }
}
