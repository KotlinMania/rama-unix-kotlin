// port-lint: source unix/stream.rs
package io.github.kotlinmania.ramaunix.unix

open class TokioUnixStream(
    private val localAddress: UnixSocketAddress? = null,
    private val peerAddress: UnixSocketAddress = UnixSocketAddress.unnamed(),
) {
    companion object {
        suspend fun connect(path: String): TokioUnixStream =
            TokioUnixStream(peerAddress = UnixSocketAddress.pathname(path))
    }

    fun localAddr(): UnixSocketAddress? = localAddress

    fun peerAddr(): UnixSocketAddress = peerAddress

    suspend fun read(buffer: ByteArray): Int = 0

    suspend fun write(buffer: ByteArray): Int = buffer.size

    suspend fun writeVectored(buffers: List<ByteArray>): Int = buffers.sumOf { it.size }

    suspend fun flush() {}

    suspend fun shutdown() {}

    fun isWriteVectored(): Boolean = false
}

class Extensions {
    private val map: MutableMap<String, Any> = mutableMapOf()
    operator fun get(key: String): Any? = map[key]

    operator fun set(key: String, value: Any) {
        map[key] = value
    }

    fun contains(key: String): Boolean = map.containsKey(key)

    fun remove(key: String): Any? = map.remove(key)

    fun clear() {
        map.clear()
    }

    fun size(): Int = map.size

    fun isEmpty(): Boolean = map.isEmpty()
}

/**
 * A stream which can be either a secure or a plain stream.
 */
class UnixStream(
    val stream: TokioUnixStream,
) {
    val extensions: Extensions = Extensions()

    companion object {
        fun new(stream: TokioUnixStream): UnixStream =
            UnixStream(stream = stream)

        fun from(value: TokioUnixStream): UnixStream =
            new(value)
    }

    fun extensionsMut(): Extensions = extensions

    fun intoTokioUnixStream(): TokioUnixStream = stream

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
