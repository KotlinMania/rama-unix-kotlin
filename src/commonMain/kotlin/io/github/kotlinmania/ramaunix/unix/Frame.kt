// port-lint: source unix/frame.rs
package io.github.kotlinmania.ramaunix.unix

interface Decoder<Item> {
    fun decodeEof(buffer: ByteArray): Item?
}

interface Encoder<Item> {
    fun encode(item: Item): ByteArray
}

interface UnixDatagramSocket {
    suspend fun receiveFrom(capacity: Int): Pair<ByteArray, UnixSocketAddress>

    suspend fun send(bytes: ByteArray): Int

    suspend fun sendTo(bytes: ByteArray, address: UnixSocketAddress): Int
}

open class UnixDatagram : UnixDatagramSocket {
    override suspend fun receiveFrom(capacity: Int): Pair<ByteArray, UnixSocketAddress> =
        ByteArray(0) to UnixSocketAddress.unnamed()

    override suspend fun send(bytes: ByteArray): Int = bytes.size

    override suspend fun sendTo(bytes: ByteArray, address: UnixSocketAddress): Int = bytes.size
}

/**
 * A unified stream and sink interface to an underlying Unix datagram, using
 * encoder and decoder contracts to encode and decode frames.
 *
 * Raw Unix datagram sockets work with datagrams, but higher-level code usually wants to
 * batch these into meaningful chunks, called frames. This method layers
 * framing on top of this socket by using the encoder and decoder contracts to
 * handle encoding and decoding of message frames. Note that the incoming and
 * outgoing frame types may be distinct.
 *
 * This function returns a single object that is both stream-like and sink-like;
 * grouping this into a single object is often useful for layering things which
 * require both read and write access to the underlying object.
 *
 * If you want to work more directly with the streams and sink, consider
 * splitting the `UnixDatagramFramed` returned by this method into separate objects,
 * allowing them to interact more easily.
 */
class UnixDatagramFramed<Item, Codec, Socket>(
    private var socket: Socket,
    private var codec: Codec,
) where Codec : Decoder<Item>, Codec : Encoder<Item>, Socket : UnixDatagramSocket {
    private var rd: ByteArray = ByteArray(0)
    private var wr: ByteArray = ByteArray(0)
    private var outAddress: UnixSocketAddress? = null
    private var flushed: Boolean = true
    private var currentAddress: UnixSocketAddress? = null

    companion object {
        const val INITIAL_RD_CAPACITY: Int = 64 * 1024
        const val INITIAL_WR_CAPACITY: Int = 8 * 1024

        fun <Item, Codec, Socket> new(
            socket: Socket,
            codec: Codec,
        ): UnixDatagramFramed<Item, Codec, Socket>
            where Codec : Decoder<Item>, Codec : Encoder<Item>, Socket : UnixDatagramSocket =
            UnixDatagramFramed(socket = socket, codec = codec)
    }

    suspend fun pollNext(): Result<Pair<Item, UnixSocketAddress>>? {
        currentAddress?.let { address ->
            codec.decodeEof(rd)?.let { frame ->
                return Result.success(frame to address)
            }
            currentAddress = null
            rd = ByteArray(0)
        }

        val (bytes, address) = socket.receiveFrom(INITIAL_RD_CAPACITY)
        rd = bytes
        currentAddress = address

        val frame = codec.decodeEof(rd) ?: return null
        return Result.success(frame to address)
    }

    suspend fun pollReady(): Result<Unit> {
        if (!flushed) {
            pollFlush().getOrElse { return Result.failure(it) }
        }
        return Result.success(Unit)
    }

    fun startSend(item: Pair<Item, UnixSocketAddress>): Result<Unit> {
        val (frame, outAddress) = item
        wr = codec.encode(frame)
        this.outAddress = outAddress
        flushed = false
        return Result.success(Unit)
    }

    suspend fun pollFlush(): Result<Unit> {
        if (flushed) {
            return Result.success(Unit)
        }

        val address = outAddress
        val written =
            if (address?.asPathname() != null) {
                socket.sendTo(wr, address)
            } else {
                socket.send(wr)
            }
        val wroteAll = written == wr.size
        wr = ByteArray(0)
        flushed = true

        return if (wroteAll) {
            Result.success(Unit)
        } else {
            Result.failure(IllegalStateException("failed to write entire datagram to socket"))
        }
    }

    suspend fun pollClose(): Result<Unit> =
        pollFlush()

    /**
     * Returns a reference to the underlying I/O stream wrapped by Framed.
     *
     * Note: Care should be taken to not tamper with the underlying stream of data
     * coming in as it may corrupt the stream of frames otherwise being worked with.
     */
    fun getRef(): Socket = socket

    /**
     * Returns a mutable reference to the underlying I/O stream wrapped by Framed.
     *
     * Note: Care should be taken to not tamper with the underlying stream of data
     * coming in as it may corrupt the stream of frames otherwise being worked with.
     */
    fun getMut(): Socket = socket

    /**
     * Returns a reference to the underlying codec wrapped by Framed.
     *
     * Note that care should be taken to not tamper with the underlying codec
     * as it may corrupt the stream of frames otherwise being worked with.
     */
    fun codec(): Codec = codec

    /**
     * Returns a mutable reference to the underlying codec wrapped by UnixDatagramFramed.
     *
     * Note that care should be taken to not tamper with the underlying codec
     * as it may corrupt the stream of frames otherwise being worked with.
     */
    fun codecMut(): Codec = codec

    /**
     * Returns a reference to the read buffer.
     */
    fun readBuffer(): ByteArray = rd

    /**
     * Returns a mutable reference to the read buffer.
     */
    fun readBufferMut(): ByteArray = rd

    /**
     * Consumes the Framed, returning its underlying I/O stream.
     */
    fun intoInner(): Socket = socket
}
