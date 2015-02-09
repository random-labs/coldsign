package org.strllar.js

import scala.scalajs.js
import scala.scalajs.js.annotation.JSName


package sjcl {

  object veryrandom {
    val max_paranoia :Int = 10;
    val _NOT_READY :Int = 0;
    val _READY :Int = 1;
    val _REQUIRES_RESEED :Int = 2;

    def reset() {
      js.Dynamic.global.stellar.sjcl.random.stopCollectors()
      js.Dynamic.global.stellar.sjcl.random = js.Dynamic.newInstance(js.Dynamic.global.stellar.sjcl.prng)(max_paranoia)
      js.Dynamic.global.stellar.sjcl.random.startCollectors()
    }
    def getProgress() :Double = js.Dynamic.global.stellar.sjcl.random.getProgress(max_paranoia).asInstanceOf[Double]

    import js.JSConverters._
    import js.JSNumberOps._
    def isReady() :Boolean = {
      if (js.Dynamic.global.stellar.sjcl.random.isReady(max_paranoia).asInstanceOf[Int] == _REQUIRES_RESEED) {
        js.Dynamic.global.stellar.sjcl.random.randomWords(3, max_paranoia)
      }
      ((js.Dynamic.global.stellar.sjcl.random.isReady(max_paranoia).asInstanceOf[Int] & _READY) == _READY)
    }
    def randomWords(nwords :Int) = js.Dynamic.global.stellar.sjcl.random.randomWords(nwords, max_paranoia).asInstanceOf[js.Array[Double]]

  }
}

