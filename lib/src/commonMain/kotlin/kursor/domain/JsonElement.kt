package kursor.domain

public sealed class JsonElement {
  public enum class JsonElementType {
    OBJECT, ARRAY, STRING, NUMBER, BOOLEAN, NULL,
  }
  
  public abstract val type: JsonElementType
  
  public data class JsonObject(public val fields: Map<String, JsonElement>) : JsonElement() {
    override val type: JsonElementType = JsonElementType.OBJECT
    override fun toString(): String = "{${fields.entries.joinToString(",") { (key, value) -> "\"$key\":$value" }}}"
  }
  
  public data class JsonArray(public val items: List<JsonElement>) : JsonElement() {
    override val type: JsonElementType = JsonElementType.ARRAY
    override fun toString(): String = "[${items.joinToString(",")}]"
  }
  
  public sealed class JsonPrimitive<T> : JsonElement() {
    public abstract val value: T
    
    public data class JsonString(override val value: String) : JsonPrimitive<String>() {
      override val type: JsonElementType = JsonElementType.STRING
      override fun toString(): String = "\"$value\""
    }
    
    public data class JsonNumber(override val value: Number) : JsonPrimitive<Number>() {
      override val type: JsonElementType = JsonElementType.NUMBER
      override fun toString(): String = "$value"
    }
    
    public data class JsonBoolean(override val value: Boolean) : JsonPrimitive<Boolean>() {
      override val type: JsonElementType = JsonElementType.BOOLEAN
      override fun toString(): String = "$value"
    }
  }
  
  public object JsonNull : JsonElement() {
    override val type: JsonElementType = JsonElementType.NULL
    override fun toString(): String = "null"
  }
}
