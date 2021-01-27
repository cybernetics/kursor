package kursor.util

import kursor.domain.*

private object JsonIndicator {
  const val MINUS = '-'
  const val FLOAT_SEPARATOR = '.'
  const val OBJECT_START = '{'
  const val OBJECT_END = '}'
  const val ARRAY_START = '['
  const val ARRAY_END = ']'
  const val COMMA = ','
  const val COLON = ':'
  const val STRING_START_END = '"'
  const val NULL_START = 'n'
  const val ESCAPE = '\\'
  val WHITESPACE = Regex("[\r\n\t ]")
  val BOOLEAN_START = Regex("[tf]")
  val NUMBER_START = Regex("[0-9]")
}

internal fun parse(json: String): JsonElement {
  val buffer = StringBuffer(json)
  val result = parse(buffer)
  buffer.skip(JsonIndicator.WHITESPACE)
  if (buffer.position != buffer.length - 1) {
    println(buffer.position)
    println(buffer.length)
    throw KursorException.ParseException("Corrupted JSON end")
  } else {
    return result
  }
}

private fun parse(buffer: StringBuffer): JsonElement {
  buffer.skip(JsonIndicator.WHITESPACE)
  return when (val next = buffer.peek()) {
    JsonIndicator.OBJECT_START -> parseObject(buffer)
    JsonIndicator.ARRAY_START -> parseArray(buffer)
    JsonIndicator.STRING_START_END -> parseString(buffer)
    JsonIndicator.NULL_START -> parseNull(buffer)
    else -> when {
      JsonIndicator.BOOLEAN_START.matches("$next") -> parseBoolean(buffer)
      next == JsonIndicator.MINUS
          || JsonIndicator.NUMBER_START.matches("$next") -> parseNumber(buffer)
      else -> throw KursorException.ParseException("Unrecognized element start", buffer.position)
    }
  }
}

private fun parseNumber(buffer: StringBuffer): JsonElement.JsonPrimitive.JsonNumber {
  val start = buffer.position
  var str = ""
  var done = false
  var floating = false
  var signed = false
  do {
    val next = buffer.peek()
    when {
      next == JsonIndicator.FLOAT_SEPARATOR && !floating -> {
        floating = true
        str += buffer.next()
      }
      next == JsonIndicator.MINUS && !signed -> {
        signed = true
        str += buffer.next()
      }
      JsonIndicator.NUMBER_START.matches("$next") -> {
        str += buffer.next()
      }
      JsonIndicator.COMMA == next
          || JsonIndicator.ARRAY_END == next
          || JsonIndicator.OBJECT_END == next
          || JsonIndicator.WHITESPACE.matches("$next")
          || JsonIndicator.NUMBER_START.matches("$next") -> {
        done = true
      }
      else -> throw KursorException.ParseException("Illegal number value representation: $str${buffer.next()}", start)
    }
  } while (!done)
  val number = if (floating) {
    str.toDouble()
  } else {
    str.toLong()
  }
  return JsonElement.JsonPrimitive.JsonNumber(number)
}

private fun parseBoolean(buffer: StringBuffer): JsonElement.JsonPrimitive.JsonBoolean {
  val start = buffer.position
  val str = buffer.nextChunk(4)
  return when {
    str == "true" -> JsonElement.JsonPrimitive.JsonBoolean(true)
    "$str${buffer.next()}" == "false" -> JsonElement.JsonPrimitive.JsonBoolean(false)
    else -> throw KursorException.ParseException("Illegal boolean value representation: '$str'", start)
  }
}

private fun parseNull(buffer: StringBuffer): JsonElement.JsonNull {
  "null".forEach {
    if (it != buffer.next()) {
      throw KursorException.ParseException(it, buffer.prev(), buffer.position)
    }
  }
  return JsonElement.JsonNull
}

private fun parseObject(buffer: StringBuffer): JsonElement.JsonObject {
  if (buffer.next() != JsonIndicator.OBJECT_START) {
    throw KursorException.ParseException(JsonIndicator.OBJECT_START, buffer.prev(), buffer.position)
  }
  buffer.skip(JsonIndicator.WHITESPACE)
  if (buffer.peek() != JsonIndicator.STRING_START_END) {
    throw KursorException.ParseException("Illegal object property key start", buffer.position)
  } else {
    var done: Boolean
    val props = mutableMapOf<String, JsonElement>()
    do {
      val key = parseString(buffer).value
      buffer.skip(JsonIndicator.WHITESPACE)
      val colon = buffer.next()
      if (colon != JsonIndicator.COLON) {
        throw KursorException.ParseException(JsonIndicator.COLON, buffer.prev(), buffer.position)
      } else {
        buffer.skip(JsonIndicator.WHITESPACE)
        val value = parse(buffer)
        props[key] = value
        buffer.skip(JsonIndicator.WHITESPACE)
        done = when (buffer.next()) {
          JsonIndicator.COMMA -> false
          JsonIndicator.OBJECT_END -> true
          else -> throw KursorException.ParseException("Unexpected value end: ${buffer.prev()}", buffer.position)
        }
        buffer.skip(JsonIndicator.WHITESPACE)
      }
    } while (!done)
    return JsonElement.JsonObject(props.toMap())
  }
}

private fun parseArray(buffer: StringBuffer): JsonElement.JsonArray {
  if (buffer.next() != JsonIndicator.ARRAY_START) {
    throw KursorException.ParseException(JsonIndicator.ARRAY_START, buffer.prev(), buffer.position)
  }
  buffer.skip(JsonIndicator.WHITESPACE)
  var done: Boolean
  val items = mutableListOf<JsonElement>()
  do {
    items.add(parse(buffer))
    buffer.skip(JsonIndicator.WHITESPACE)
    done = when (buffer.next()) {
      JsonIndicator.COMMA -> false
      JsonIndicator.ARRAY_END -> true
      else -> throw KursorException.ParseException("Unexpected value end: ${buffer.prev()}", buffer.position)
    }
  } while (!done)
  return JsonElement.JsonArray(items.toList())
}

private fun parseString(buffer: StringBuffer): JsonElement.JsonPrimitive.JsonString {
  if (buffer.next() != JsonIndicator.STRING_START_END) {
    throw KursorException.ParseException("Illegal string start at position ${buffer.position}")
  }
  var str = ""
  var escaping = false
  var done = false
  do {
    val char = buffer.next()
    if (char == JsonIndicator.STRING_START_END && !escaping) {
      done = true
    } else {
      if (escaping) {
        escaping = false
      } else if (char == JsonIndicator.ESCAPE) {
        escaping = true
      }
      
      if (!escaping) {
        str += char
      }
    }
  } while (!done)
  return JsonElement.JsonPrimitive.JsonString(str)
}
