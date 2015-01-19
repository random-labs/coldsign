package org.strllar.js

import scala.scalajs.js
import scala.scalajs.js.annotation.JSName

package stellar {
  @JSName("stellar.UInt160")
  class UInt160 extends js.Object {
    def to_json() :js.Object = ???
  }

  @JSName("stellar.KeyPair")
  class KeyPair extends js.Object{
    def get_address() :UInt160 = ???
  }

  @JSName("stellar.Seed")
  class Seed extends js.Object{
    def random() :Seed = ???
    def get_key() :KeyPair = ???
    def to_json() :js.Object = ???
    def parse_json(s :js.String) :Seed = ???
  }


}

