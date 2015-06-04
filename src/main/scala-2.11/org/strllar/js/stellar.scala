package org.strllar.js

import scala.scalajs.js
import scala.scalajs.js.annotation.JSName

package stellar {
  
  @JSName("StellarBase.Keypair")
  class Keypair extends js.Object{
    def address():String = js.native
    def seed():String = js.native
  }

  @JSName("StellarBase.Keypair")
  object Keypair extends js.Object{
    def fromRawSeed(rawSeed :js.Array[Byte]):Keypair = js.native
    def fromSeed(seed :String):Keypair = js.native
  }

  object Seed {
    def random() = Keypair.fromRawSeed(sjcl.veryrandom.randomWords(32))
  }


}

