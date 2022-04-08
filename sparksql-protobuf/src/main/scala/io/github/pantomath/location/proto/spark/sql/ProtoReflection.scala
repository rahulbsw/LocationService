package io.github.pantomath.location.proto.spark.sql

import com.google.protobuf.AbstractMessage
import com.google.protobuf.Descriptors.{Descriptor, FieldDescriptor}
import org.apache.spark.sql.types._

import scala.collection.convert.ImplicitConversions.`list asScalaBuffer`

/**
 * Support for generating catalyst schemas for protobuf objects.
 */
object ProtoReflection {
  /** The universe we work in runtime */
  val universe = scala.reflect.runtime.universe

  /** The mirror used to access types in the universe */
  def mirror = universe.runtimeMirror(Thread.currentThread().getContextClassLoader)

  import universe._

  /** Returns a catalyst DataType and its nullability for the given Scala Type using reflection. */
  def schemaFor[T <: AbstractMessage](clazz: Class[T]): Schema = {
    schemaFor(mirror.classSymbol(clazz).toType)
  }

  /** Returns a catalyst DataType and its nullability for the given Scala Type using reflection. */
  def schemaFor[T: TypeTag]: Schema = schemaFor(localTypeOf[T])

  /** Returns a catalyst DataType and its nullability for the given Scala Type using reflection. */
  def schemaFor(tpe: `Type`): Schema = {
    tpe match {
      case t if t <:< localTypeOf[AbstractMessage] =>
        val clazz = mirror.runtimeClass(t).asInstanceOf[Class[AbstractMessage]]
        val descriptor = clazz.getMethod("getDescriptor").invoke(null).asInstanceOf[Descriptor]
        Schema(StructType(descriptor.getFields.flatMap(structFieldFor)), nullable = true)
      case other =>
        throw new UnsupportedOperationException(s"Schema for type $other is not supported")
    }
  }

  /**
   * Return the Scala Type for `T` in the current classloader mirror.
   *
   * Use this method instead of the convenience method `universe.typeOf`, which
   * assumes that all types can be found in the classloader that loaded scala-reflect classes.
   * That's not necessarily the case when running using Eclipse launchers or even
   * Sbt console or test (without `fork := true`).
   *
   * @see SPARK-5281
   */
  private def localTypeOf[T: TypeTag]: `Type` = typeTag[T].in(mirror).tpe

  private def structFieldFor(fd: FieldDescriptor): Option[StructField] = {
    import com.google.protobuf.Descriptors.FieldDescriptor.JavaType._
    val dataType = fd.getJavaType match {
      case INT => Some(IntegerType)
      case LONG => Some(LongType)
      case FLOAT => Some(FloatType)
      case DOUBLE => Some(DoubleType)
      case BOOLEAN => Some(BooleanType)
      case STRING => Some(StringType)
      case BYTE_STRING => Some(BinaryType)
      case ENUM => Some(StringType)
      case MESSAGE =>
        Option(fd.getMessageType.getFields.flatMap(structFieldFor))
          .filter(_.nonEmpty)
          .map(StructType.apply)
    }
    dataType.map(dt => StructField(
      fd.getName,
      if (fd.isRepeated) ArrayType(dt, containsNull = false) else dt,
      nullable = !fd.isRequired && !fd.isRepeated
    ))
  }

  case class Schema(dataType: DataType, nullable: Boolean)
}