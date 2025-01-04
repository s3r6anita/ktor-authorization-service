package ru.countrystats.util

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class LocalDateTimeAdapter : TypeAdapter<LocalDateTime?>() {
    override fun write(jsonWriter: JsonWriter?, value: LocalDateTime?) {
        if (value == null) {
            jsonWriter?.nullValue()
        } else {
            jsonWriter?.value(value.format(dateTime))
        }
    }

    override fun read(jsonReader: JsonReader): LocalDateTime? {
        if (jsonReader.peek() == JsonToken.NULL) {
            jsonReader.nextNull()
            return null
        } else {
            return LocalDateTime.parse(jsonReader.nextString(), dateTime)
        }
    }

    private companion object {
        val dateTime: DateTimeFormatter =
            DateTimeFormatter.ofPattern("dd'.'MM'.'yyyy HH':'mm")
    }
}