package kursor

import kursor.domain.*

public data class Kursor(public val jsonPathExp: String) {
  public fun apply(target: String): JsonElement? = target.toJsonObject()?.let { apply(it) }
  public fun apply(target: JsonElement.JsonObject): JsonElement? {
    TODO()
  }
}
