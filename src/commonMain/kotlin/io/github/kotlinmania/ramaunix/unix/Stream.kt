// port-lint: source unix/stream.rs
package io.github.kotlinmania.ramaunix.unix

expect class TokioUnixStream {
    companion object {
        suspend fun connect(path: String): TokioUnixStream
    }

    fun localAddr(): UnixSocketAddress?

    fun peerAddr(): UnixSocketAddress

    suspend fun read(buffer: ByteArray): Int

    suspend fun write(buffer: ByteArray): Int

    suspend fun writeVectored(buffers: List<ByteArray>): Int

    suspend fun flush()

    suspend fun shutdown()

    fun isWriteVectored(): Boolean
}

/**
 * A stream which can be either a secure or a plain stream.
 */
class UnixStream(
    val stream: TokioUnixStream,
    val extensions: MutableMap<String, Any> = mutableMapOf(),
) {
    companion object {
        fun new(stream: TokioUnixStream): UnixStream =
            UnixStream(stream = stream)

        fun from(value: TokioUnixStream): UnixStream =
            new(value)
    }

    fun intoTokioUnixStream(): TokioUnixStream = stream

    fun extensions(): Map<String, Any> = extensions

    fun extensionsMut(): MutableMap<String, Any> = extensions

    suspend fun pollRead(buffer: ByteArray): Int =
        stream.read(buffer)

    suspend fun pollWrite(buffer: ByteArray): Int =
        stream.write(buffer)

    suspend fun pollWriteVectored(buffers: List<ByteArray>): Int =
        stream.writeVectored(buffers)

    suspend fun pollFlush() {
        stream.flush()
    }

    suspend fun pollShutdown() {
        stream.shutdown()
    }

    fun isWriteVectored(): Boolean =
        stream.isWriteVectored()
}
