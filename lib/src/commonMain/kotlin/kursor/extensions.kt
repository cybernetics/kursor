package kursor

import kursor.domain.*
import kursor.util.*

public fun String.toKursor(): Kursor = Kursor(this)

public fun String.toJsonElement(): JsonElement? = parse(this).takeIf { it.type != JsonElement.Type.NULL }
public fun String.toJsonObject(): JsonElement.JsonObject? = toJsonElement() as? JsonElement.JsonObject
public fun String.toJsonArray(): JsonElement.JsonArray? = toJsonElement() as? JsonElement.JsonArray
public fun String.toJsonPrimitive(): JsonElement.JsonPrimitive<*>? = toJsonElement() as? JsonElement.JsonPrimitive<*>
public fun String.toJsonBoolean(): JsonElement.JsonPrimitive.JsonBoolean? =
  toJsonPrimitive() as? JsonElement.JsonPrimitive.JsonBoolean

public fun String.toJsonNumber(): JsonElement.JsonPrimitive.JsonNumber? =
  toJsonPrimitive() as? JsonElement.JsonPrimitive.JsonNumber

public fun String.toJsonString(): JsonElement.JsonPrimitive.JsonString? =
  toJsonPrimitive() as? JsonElement.JsonPrimitive.JsonString
