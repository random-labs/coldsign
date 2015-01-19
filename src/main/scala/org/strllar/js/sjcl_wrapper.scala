package org.strllar.js

import scala.scalajs.js
import scala.scalajs.js.annotation.JSName


package sjcl {

  object veryrandom {
    val max_paranoia :js.prim.Number = 10;
    val _NOT_READY :js.prim.Number = 0;
    val _READY :js.prim.Number = 1;
    val _REQUIRES_RESEED :js.prim.Number = 2;

    def reset() {
      js.Dynamic.global.stellar.sjcl.random.stopCollectors()
      js.Dynamic.global.stellar.sjcl.random = js.Dynamic.newInstance(js.Dynamic.global.stellar.sjcl.prng)(max_paranoia)
      js.Dynamic.global.stellar.sjcl.random.startCollectors()
    }
    def getProgress() :Double = js.Dynamic.global.stellar.sjcl.random.getProgress(max_paranoia).asInstanceOf[js.prim.Number]
    def isReady() :Boolean = {
      if (js.Dynamic.global.stellar.sjcl.random.isReady(max_paranoia) == _REQUIRES_RESEED) {
        js.Dynamic.global.stellar.sjcl.random.randomWords(3, max_paranoia)
      }
      ((js.Dynamic.global.stellar.sjcl.random.isReady(max_paranoia).asInstanceOf[js.prim.Number] & _READY) == _READY)
    }
    def randomWords(nwords :Int) = js.Dynamic.global.stellar.sjcl.random.randomWords(nwords, max_paranoia).asInstanceOf[js.Array[js.prim.Number]]

  }
}

