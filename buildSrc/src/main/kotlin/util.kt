import org.gradle.api.provider.*

infix fun <T> Property<T>.by(value: T) {
  set(value)
}
