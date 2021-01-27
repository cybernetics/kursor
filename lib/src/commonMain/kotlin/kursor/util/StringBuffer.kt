package kursor.util

internal class StringBuffer(val string: String) {
  val length: Int get() = string.length
  private var _position = -1
  val position: Int get() = _position
  fun peek() = string[_position + 1]
  fun hasNext() = _position + 1 < length
  fun next(): Char = string[++_position]
  fun nextChunk(length: Int): String {
    val str = string.substring(++_position, _position + length)
    _position += length-1
    return str
  }
  
  fun prev(): Char = string[--_position]
  fun skip(steps: Int = 1): Int {
    _position += steps
    return _position
  }
  
  fun skip(pattern: Regex): Int {
    var matching = true
    do {
      if (hasNext() && pattern.matches("${peek()}")) {
        skip(1)
      } else {
        matching = false
      }
    } while (matching)
    return _position
  }
}
