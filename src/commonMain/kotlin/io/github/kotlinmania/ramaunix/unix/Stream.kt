// port-lint: source unix/stream.rs
package io.github.kotlinmania.ramaunix.unix

import io.github.kotlinmania.ramacore.Extensions
import io.github.kotlinmania.ramacore.ExtensionsMut
import io.github.kotlinmania.ramacore.ExtensionsRef
import io.github.kotlinmania.tokio.io.AsyncRead
import io.github.kotlinmania.tokio.io.AsyncWrite
import io.github.kotlinmania.tokio.io.Context
import io.github.kotlinmania.tokio.io.IoSlice
import io.github.kotlinmania.tokio.io.Poll
import io.github.kotlinmania.tokio.io.ReadBuf

open class TokioUnixStream(
    private val localAddress: UnixSocketAddress? = null,
    private val peerAddress: UnixSocketAddress = UnixSocketAddress.unnamed(),
) : AsyncRead,
    AsyncWrite {
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

    override fun pollRead(cx: Context, buf: ReadBuf): Poll<Result<Unit>> =
        Poll.ready(Result.success(Unit))

    override fun pollWrite(cx: Context, buf: UByteArray): Poll<Result<Int>> =
        Poll.ready(Result.success(buf.size))

    override fun pollWriteVectored(cx: Context, bufs: List<IoSlice>): Poll<Result<Int>> =
        Poll.ready(Result.success(bufs.sumOf { it.size }))

    override fun pollFlush(cx: Context): Poll<Result<Unit>> =
        Poll.ready(Result.success(Unit))

    override fun pollShutdown(cx: Context): Poll<Result<Unit>> =
        Poll.ready(Result.success(Unit))

    override fun isWriteVectored(): Boolean = false
}

/**
 * A stream which can be either a secure or a plain stream.
 */
class UnixStream(
    val stream: TokioUnixStream,
) : ExtensionsRef,
    ExtensionsMut,
    AsyncRead,
    AsyncWrite {
    private val extensionsStore: Extensions = Extensions.new()

    companion object {
        fun new(stream: TokioUnixStream): UnixStream =
            UnixStream(stream = stream)

        fun from(value: TokioUnixStream): UnixStream =
            new(value)
    }

    override fun extensions(): Extensions = extensionsStore

    override fun extensionsMut(): Extensions = extensionsStore

    fun intoTokioUnixStream(): TokioUnixStream = stream

    override fun pollRead(cx: Context, buf: ReadBuf): Poll<Result<Unit>> =
        stream.pollRead(cx, buf)

    override fun pollWrite(cx: Context, buf: UByteArray): Poll<Result<Int>> =
        stream.pollWrite(cx, buf)

    override fun pollWriteVectored(cx: Context, bufs: List<IoSlice>): Poll<Result<Int>> =
        stream.pollWriteVectored(cx, bufs)

    override fun pollFlush(cx: Context): Poll<Result<Unit>> =
        stream.pollFlush(cx)

    override fun pollShutdown(cx: Context): Poll<Result<Unit>> =
        stream.pollShutdown(cx)

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

    override fun isWriteVectored(): Boolean =
        stream.isWriteVectored()
}
