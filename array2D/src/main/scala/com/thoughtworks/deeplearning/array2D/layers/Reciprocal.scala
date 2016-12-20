package com.thoughtworks.deeplearning
package array2D.layers

import cats._
import cats.implicits._
import com.thoughtworks.deeplearning.Layer._
import com.thoughtworks.deeplearning.Layer.Batch._
import org.nd4j.linalg.api.ndarray.INDArray
import org.nd4s.Implicits._
import com.thoughtworks.deeplearning.array2D.utilities._

/**
  * @author 杨博 (Yang Bo) &lt;pop.atry@gmail.com&gt;
  */
final case class Reciprocal[Input0 <: Batch](operand: Layer.Aux[Input0, Array2D#Batch]) extends BufferedLayer.Unary {
  type BufferedBatch = Array2DSemigroupBatch with SemigroupBatch with UnaryBatch

  type Input = Input0

  override protected def rawForward(input0: Input): BufferedBatch = {
    new {
      override val input = input0
    } with Array2DSemigroupBatch with SemigroupBatch with UnaryBatch {

      val value = upstream.value.map(_ rdiv 1.0).memoize

      override protected def rawBackward(outputDelta: Eval[INDArray]): Unit = {
        val upstreamValue = upstream.value
        upstream.backward(
          outputDelta
            .map2(upstream.value) { (outputDeltaValue, aValue) =>
              -outputDeltaValue / (aValue * aValue)
            }
            .memoize
        )
      }
    }
  }
}
