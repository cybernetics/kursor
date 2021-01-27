package kursor.domain

public sealed class KursorException : Exception() {
  public data class ParseException(override val message: String? = null) : KursorException() {
    public constructor(
      message: String,
      position: Int,
    ) : this("[Position $position] $message")
    
    public constructor(
      expected: Char,
      actual: Char,
      position: Int,
    ) : this("Unexpected character! Expected: $expected, Actual: $actual", position)
  }
}
