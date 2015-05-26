package org.strllar.js

import scala.scalajs.js
import scala.scalajs.js.annotation.JSName

package stellar {
  @JSName("stellar.UInt160")
  class UInt160 extends js.Object {
    def to_json() :js.Object = js.native
  }

  @JSName("stellar.KeyPair")
  class KeyPair extends js.Object{
    def get_address() :UInt160 = js.native
  }

  @JSName("stellar.Seed")
  class Seed extends js.Object{
    def random() :Seed = js.native
    def get_key() :KeyPair = js.native
    def to_json() :js.Object = js.native
    def parse_json(s :String) :Seed = js.native
  }


}

