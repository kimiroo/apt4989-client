package cc.darak.aptanywhere.data.model.adapter

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter

class StringToNullAdapter : TypeAdapter<String?>() {
    override fun write(out: JsonWriter?, value: String?) {
        out?.value(value)
    }

    override fun read(reader: JsonReader): String? {
        if (reader.peek() == JsonToken.NULL) {
            reader.nextNull()
            return null
        }
        val stringValue = reader.nextString()
        return if (stringValue.isEmpty()) null else stringValue
    }
}