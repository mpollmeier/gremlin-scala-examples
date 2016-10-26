// shamelessly copied from com.steelbridgelabs.oss.neo4j

import com.steelbridgelabs.oss.neo4j.structure.Neo4JElementIdProvider

import java.util.Objects
import java.util.concurrent.atomic.AtomicLong
import org.neo4j.driver.v1.types.Entity

object ElementIdProvider {
  val IdFieldName = "id"
}

class ElementIdProvider extends Neo4JElementIdProvider[Long] {
  import ElementIdProvider._

  private val atomicLong = new AtomicLong()

  override def fieldName(): String = IdFieldName

  override def generate(): Long = atomicLong.incrementAndGet()

  override def processIdentifier(id: Any): Long = {
    Objects.requireNonNull(id, "Element identifier cannot be null")
    if (id.isInstanceOf[Long])
      id.asInstanceOf[Long]
    else if (id.isInstanceOf[Number])
      id.asInstanceOf[Number].longValue()
    else if (id.isInstanceOf[String])
      id.asInstanceOf[String].toLong
    else throw new IllegalArgumentException(String.format("Expected an id that is convertible to Long but received %s", id.getClass()))
  }

  def get(entity: Entity): Long = ???

  def matchPredicateOperand(alias: String): String = s"$alias.$IdFieldName"

}
