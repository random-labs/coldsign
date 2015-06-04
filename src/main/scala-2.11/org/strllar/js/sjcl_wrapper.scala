package org.strllar.js

import scala.scalajs.js
import scala.scalajs.js.annotation.JSName


package sjcl { //TODO: change package name to sjcl_wrapper

  object veryrandom {
    val max_paranoia :Int = 10;
    val _NOT_READY :Int = 0;
    val _READY :Int = 1;
    val _REQUIRES_RESEED :Int = 2;

    def reset() {
      js.Dynamic.global.sjcl.random.stopCollectors()
      js.Dynamic.global.sjcl.random = js.Dynamic.newInstance(js.Dynamic.global.sjcl.prng)(max_paranoia)
      js.Dynamic.global.sjcl.random.startCollectors()
    }
    def getProgress() :Double = js.Dynamic.global.sjcl.random.getProgress(max_paranoia).asInstanceOf[Double]

    import js.JSConverters._
    import js.JSNumberOps._
    def isReady() :Boolean = {
      if (js.Dynamic.global.sjcl.random.isReady(max_paranoia).asInstanceOf[Int] == _REQUIRES_RESEED) {
        js.Dynamic.global.sjcl.random.randomWords(3, max_paranoia)
      }
      ((js.Dynamic.global.sjcl.random.isReady(max_paranoia).asInstanceOf[Int] & _READY) == _READY)
    }
    def randomWords(nwords :Int) = js.Dynamic.global.sjcl.random.randomWords(nwords, max_paranoia).asInstanceOf[js.Array[Byte]]

  }

  object crypto {
    val param = js.Dynamic.literal(v=1, iter=1000, ks=128, ts=64, mode="ccm", adata="", cipher="aes")

    def enc(password :String, plaintext :String):String = {
      val jsontxt = js.Dynamic.global.sjcl.json.encrypt(password, plaintext, param)
      js.Dynamic.global.sjcl.codec.base64.fromBits(
        js.Dynamic.global.sjcl.codec.utf8String.toBits(jsontxt)
      ).asInstanceOf[String]
    }
    def dec(password :String, ciphertext :String):String = {
      val jsontxt = js.Dynamic.global.sjcl.codec.utf8String.fromBits(
        js.Dynamic.global.sjcl.codec.base64.toBits(ciphertext)
      )
      js.Dynamic.global.sjcl.json.decrypt(password, jsontxt, param).asInstanceOf[String]
    }

  }
}

