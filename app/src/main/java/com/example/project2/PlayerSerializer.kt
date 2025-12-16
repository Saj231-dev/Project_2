package com.example.project2

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

object PlayerSerializer : Serializer<Player> {
    override val defaultValue: Player = Player.getDefaultInstance()
    override suspend fun readFrom(input: InputStream): Player {
        try {
            return Player.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: Player, output: OutputStream) {
        return t.writeTo(output)
    }
}