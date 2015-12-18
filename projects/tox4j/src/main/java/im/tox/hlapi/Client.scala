package im.tox.hlapi

import com.typesafe.scalalogging.Logger
import im.tox.core.network.Port
import im.tox.tox4j.av.ToxAv
import im.tox.tox4j.core.ToxCore
import im.tox.tox4j.core.data._
import im.tox.tox4j.core.options.ToxOptions
import im.tox.tox4j.impl.jni.{ToxAvImpl, ToxCoreImpl}
import org.slf4j.LoggerFactory

import scala.annotation.tailrec
import scalaz.concurrent.{Strategy, Task}
import scalaz.stream._
import scalaz.~>

@SuppressWarnings(Array("org.brianmckenna.wartremover.warts.Any"))
object Client {

  private val logger = Logger(LoggerFactory.getLogger(getClass))

  /** Evaluate a `Task` within a Tox-connected `Process`. */
  def eval[A](a: Task[A]): Process[ToxClientConnection, A] = Process.eval(lift(a))

  /** Evaluate and discard the result of a `Task` within a Tox-connected `Process`. */
  def eval_[A](a: Task[A]): Process[ToxClientConnection, Nothing] = Process.eval_(lift(a))

  /** Run a `Process[Task,A]` within a Tox-connected `Process`. */
  def lift[A](p: Process[Task, A]): Process[ToxClientConnection, A] =
    p.translate(new (Task ~> ToxClientConnection) { def apply[R](c: Task[R]) = lift(c) })

  def emit[A](value: A): Process[ToxClientConnection, A] = Process.emit(value)

  def emitAll[A](values: Seq[A]): Process[ToxClientConnection, A] = Process.emitAll(values)

  def merge[A](
    a: Process[ToxClientConnection, A],
    b: Process[ToxClientConnection, A]
  )(implicit S: Strategy): Process[ToxClientConnection, A] = {
    wye(a, b)(scalaz.stream.wye.merge)
  }

  def wye[A, B, C](
    a: Process[ToxClientConnection, A],
    b: Process[ToxClientConnection, B]
  )(
    y: Wye[A, B, C]
  )(implicit S: Strategy): Process[ToxClientConnection, C] = {
    ask.flatMap {
      case (tox, av) =>
        lift {
          val boundA = bindTo(tox, av)(a)
          val boundB = bindTo(tox, av)(b)
          boundA.wye(boundB)(y)(S)
        }
    }
  }

  /**
   * Start a Tox client.
   */
  def start[A](options: ToxOptions)(p: Process[ToxClientConnection, A]): Process[Task, A] = {
    Process.eval(Task.delay(new ToxCoreImpl(options))).flatMap { tox =>
      Process.eval(Task.delay(new ToxAvImpl(tox))).flatMap { av =>
        bindTo(tox, av)(p) onComplete Process.eval_(Task.delay(av.close()))
      } onComplete Process.eval_(Task.delay(tox.close()))
    }
  }

  /**
   * Continually receive Tox events.
   */
  def receive: Process[ToxClientConnection, Event] = {
    merge(receiveTox, receiveAv).flatMap(emitAll)
  }

  private def receiveTox: Process[ToxClientConnection, List[Event]] = {
    ask.flatMap {
      case (tox, av) =>
        eval(Task.delay(waitForEvents(tox)))
    }.repeat
  }

  private def receiveAv: Process[ToxClientConnection, List[Event]] = {
    ask.flatMap {
      case (tox, av) =>
        eval(Task.delay(waitForEvents(av)))
    }.repeat
  }

  @tailrec
  private def waitForEvents(tox: ToxCore): List[Event] = {
    Thread.sleep(tox.iterationInterval)
    tox.iterate(CollectEventListener)(Nil) match {
      case Nil    => waitForEvents(tox)
      case events => events.reverse
    }
  }

  @tailrec
  private def waitForEvents(av: ToxAv): List[Event] = {
    Thread.sleep(av.iterationInterval)
    av.iterate(CollectEventListener)(Nil) match {
      case Nil    => waitForEvents(av)
      case events => events.reverse
    }
  }

  def bootstrap(address: String, port: Port, publicKey: ToxPublicKey): Process[ToxClientConnection, Unit] = {
    ask.map {
      case (tox, av) =>
        tox.bootstrap(address, port, publicKey)
        tox.addTcpRelay(address, port, publicKey)
    }
  }

  def privateAccess[A](f: (ToxCore, ToxAv) => A): Process[ToxClientConnection, A] = {
    ask.map { case (tox, av) => f(tox, av) }
  }

  def privateAccessFlat[A](f: (ToxCore, ToxAv) => Process[ToxClientConnection, A]): Process[ToxClientConnection, A] = {
    ask.flatMap { case (tox, av) => f(tox, av) }
  }

  private[hlapi] def ask: Process[ToxClientConnection, (ToxCore, ToxAv)] = {
    Process.eval {
      new ToxClientConnection[(ToxCore, ToxAv)] {
        override def apply(tox: ToxCore, av: ToxAv): Task[(ToxCore, ToxAv)] = Task.now((tox, av))
      }
    }
  }

  private def bindTo[A](tox: ToxCore, av: ToxAv)(p: Process[ToxClientConnection, A]): Process[Task, A] = {
    p.translate(new (ToxClientConnection ~> Task) { def apply[R](c: ToxClientConnection[R]) = c(tox, av) })
  }

  trait ToxClientConnection[+A] extends ((ToxCore, ToxAv) => Task[A]) {
    def apply(tox: ToxCore, av: ToxAv): Task[A]
  }

  private def lift[A](t: Task[A]): ToxClientConnection[A] = new ToxClientConnection[A] {
    def apply(tox: ToxCore, av: ToxAv): Task[A] = t
  }

}
