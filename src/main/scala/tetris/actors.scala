package tetris

import scala.concurrent.{Await, Future}
import zio.{IO, Task}
import zio.actors.{Actor, ActorRef, ActorSystem, Context, Supervisor}

sealed trait GameMessage[+RESPONSE]
case object MoveLeft extends GameMessage[Unit]
case object MoveRight extends GameMessage[Unit]
case object Rotate90 extends GameMessage[Unit]
case object Tick extends GameMessage[Unit]
case object Drop extends GameMessage[Unit]
case object GetView extends GameMessage[GameView]

case class GameActorState(gameState: GameState, stateUpdatedHook: Task[Unit]) {

  /** Apply `transform` to GameSate and trigger `stateUpdatedHook` */
  def update(transform: GameState => GameState): Task[(GameActorState, Unit)] = {
    for {
      newState <- IO.effectTotal { copy(gameState = transform(gameState)) }
      _ <- stateUpdatedHook
    } yield (newState, ())
  }
}

object GameActor extends Actor.Stateful[GameActorState, Throwable, GameMessage] {
  import GameState._

  override def receive[T](
    state: GameActorState,
    msg: GameMessage[T],
    context: Context
  ): Task[(GameActorState, T)] = {
    msg match {
      case MoveLeft  => state.update(moveLeft)
      case MoveRight => state.update(moveRight)
      case Rotate90  => state.update(rotate90)
      case Tick      => state.update(tick)
      case Drop      => state.update(drop)
      case GetView   => IO.effectTotal { (state, state.gameState.view) }
    }
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
