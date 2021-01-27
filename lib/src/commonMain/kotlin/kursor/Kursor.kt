package kursor

import kursor.domain.*

public data class Kursor(public val expression: String) {
  public fun <T> apply(target: JsonElement.JsonObject): JsonElement? {
    TODO()
  }
  
  public fun <T> apply(target: String): JsonElement? {
    TODO()
  }
}
