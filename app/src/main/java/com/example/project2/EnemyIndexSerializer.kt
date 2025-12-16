package com.example.project2

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

object EnemyIndexSerializer : Serializer<EnemyIndex> {
    override val defaultValue: EnemyIndex = EnemyIndex.getDefaultInstance()
    override suspend fun readFrom(input: InputStream): EnemyIndex {
        try {
            return EnemyIndex.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: EnemyIndex, output: OutputStream) {
        return t.writeTo(output)
    }
}