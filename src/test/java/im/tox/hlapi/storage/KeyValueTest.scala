package im.tox.hlapi.storage

import scala.collection.immutable.{ HashMap, Map }

import org.scalacheck._
import org.scalacheck.commands._
import org.scalacheck.Arbitrary._

import org.scalatest._
import org.scalatest.prop.PropertyChecks

final case class KVSpecification[Key <: KeyType, Value <: ValueType[Key], KV <: KeyValue[Key, Value]](
    genKey: Gen[Key],
    genVal: Gen[Value],
    val newSut: Iterable[Value] => KV
) extends Commands {

  type Sut = KV

  final case class State(val kv: HashMap[Key, Value]) {
    def get(k: Key): Option[Value] = kv.get(k)
    def set(value: Value): State =
      copy(kv = kv + ((value.key, value)))
    def delete(key: Key): State = copy(kv = kv - key)
    def delete(obj: Value): State = delete(obj.key)
  }

  override def newSut(s: State): Sut =
    newSut(s.kv.values)

  override def genInitialState: Gen[State] = {
    for {
      keys <- Gen.nonEmptyContainerOf[List, Key](genKey)
      values <- Gen.containerOfN[List, Value](keys.size, genVal)
    } yield State(HashMap(keys.zip(values): _*))
  }

  override def initialPreCondition(s: State): Boolean = true
  override def canCreateNewSut(newState: State, initStuts: Traversable[State],
    runningStuts: Traversable[Sut]): Boolean = true

  override def destroySut(sut: Sut): Unit = ()

  final case class Get(key: Key) extends SuccessCommand {
    type Result = Option[Value]
    def nextState(s: State): State = s

    def preCondition(s: State): Boolean = true
    def postCondition(s: State, r: Option[Value]): Prop = {
      Prop(r == s.get(key))
    }

    def run(sut: Sut): Result = {
      sut.lookup(key)
    }
  }

  final case class Set(value: Value) extends SuccessCommand {
    type Result = Boolean
    def nextState(s: State): State = {
      s.set(value)
    }

    def preCondition(s: State): Boolean = true
    def postCondition(s: State, r: Result): Prop = {
      Prop(r)
    }

    def run(sut: Sut): Result = {
      sut.add(value)
      sut.lookup(value.key) == value
    }

  }

  private def genGet(s: State): Gen[Get] = {
    genKey.map(Get(_))
  }

  private def genSet(s: State): Gen[Set] = {
    genVal.map(Set(_))
  }

  override def genCommand(s: State): Gen[Command] = {
    Gen.oneOf[Command](genGet(s), genSet(s))
  }

}

/*
 * final class KeyValueTest extends FlatSpec {
 *   "Some Implementation" should "be a proper KV implementation" in {
 *     (new FileSpecification[K, V, KV]()).property().check
 *   }
 * }
 */
