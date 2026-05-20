// port-lint: source unix/server/listener.rs
package io.github.kotlinmania.ramaunix.unix.server

import io.github.kotlinmania.ramaunix.unix.TokioUnixStream
import io.github.kotlinmania.ramaunix.unix.UnixSocketAddress
import io.github.kotlinmania.ramaunix.unix.UnixSocketInfo
import io.github.kotlinmania.ramaunix.unix.UnixStream

expect class UnixSocket

expect class TokioUnixListener {
    companion object {
        suspend fun bindPath(path: String): TokioUnixListener

        fun fromSocket(socket: UnixSocket): TokioUnixListener
    }

    fun localAddr(): UnixSocketAddress

    suspend fun accept(): Pair<TokioUnixStream, UnixSocketAddress>

    fun rawFileDescriptor(): Int
}

expect class UnixSocketCleanup(path: String) {
    val path: String

    fun cleanup()
}

/**
 * Builder for `UnixListener`.
 */
class UnixListenerBuilder {
    companion object {
        /**
         * Create a new `UnixListenerBuilder`.
         */
        fun new(): UnixListenerBuilder = UnixListenerBuilder()

        fun default(): UnixListenerBuilder = new()
    }

    /**
     * Creates a new [UnixListener], which will be bound to the specified path.
     *
     * The returned listener is ready for accepting connections.
     */
    suspend fun bindPath(path: String): UnixListener {
        val inner = TokioUnixListener.bindPath(path)
        val cleanup = UnixSocketCleanup(path)
        return UnixListener(inner = inner, cleanup = cleanup)
    }

    /**
     * Creates a new [UnixListener], which will be bound to the specified socket.
     *
     * The returned listener is ready for accepting connections.
     */
    fun bindSocket(socket: UnixSocket): UnixListener {
        val inner = TokioUnixListener.fromSocket(socket)
        return UnixListener(inner = inner, cleanup = null)
    }

    /**
     * Creates a new TcpListener, which will be bound to the specified interface.
     *
     * The returned listener is ready for accepting connections.
     */
    suspend fun bindSocketOpts(options: UnixSocketOptions): UnixListener =
        bindSocket(options.tryBuildSocket())
}

expect class UnixSocketOptions {
    fun tryBuildSocket(): UnixSocket
}

/**
 * A Unix domain socket server, listening for incoming connections once served
 * using one of the `serve` methods such as [UnixListener.serve].
 *
 * Note that the underlying socket file is only cleaned up
 * by this listener's cleanup implementation if the listener
 * was created using the `bindPath` constructor. Otherwise
 * it is assumed that the creator of this listener is in charge
 * of that cleanup.
 */
class UnixListener internal constructor(
    private val inner: TokioUnixListener,
    private val cleanup: UnixSocketCleanup?,
) {
    companion object {
        /**
         * Create a new [UnixListenerBuilder] without a state,
         * which can be used to configure a [UnixListener].
         */
        fun build(): UnixListenerBuilder = UnixListenerBuilder.new()

        /**
         * Creates a new [UnixListener], which will be bound to the specified path.
         *
         * The returned listener is ready for accepting connections.
         */
        suspend fun bindPath(path: String): UnixListener =
            UnixListenerBuilder.default().bindPath(path)

        /**
         * Creates a new [UnixListener], which will be bound to the specified socket.
         *
         * The returned listener is ready for accepting connections.
         */
        fun bindSocket(socket: UnixSocket): UnixListener =
            UnixListenerBuilder.default().bindSocket(socket)

        /**
         * Creates a new TcpListener, which will be bound to the specified interface device name.
         *
         * The returned listener is ready for accepting connections.
         */
        suspend fun bindSocketOpts(options: UnixSocketOptions): UnixListener =
            UnixListenerBuilder.default().bindSocketOpts(options)
    }

    /**
     * Returns the local address that this listener is bound to.
     *
     * This can be useful, for example, when binding to port 0 to figure out
     * which port was actually bound.
     */
    fun localAddr(): UnixSocketAddress = inner.localAddr()

    fun asRawFd(): Int = inner.rawFileDescriptor()

    fun asFd(): Int = inner.rawFileDescriptor()

    /**
     * Accept a single connection from this listener,
     * what you can do with whatever you want.
     */
    suspend fun accept(): Pair<UnixStream, UnixSocketAddress> {
        val (stream, address) = inner.accept()
        return UnixStream.from(stream) to address
    }

    /**
     * Serve connections from this listener with the given service.
     *
     * This method will block the current listener for each incoming connection,
     * the underlying service can choose to spawn a task to handle the accepted stream.
     */
    suspend fun serve(service: suspend (UnixStream) -> Unit): Nothing {
        while (true) {
            val (socket, peerAddress) =
                try {
                    inner.accept()
                } catch (error: Throwable) {
                    handleAcceptErr(error)
                    continue
                }

            val localAddress = socket.localAddr()
            val stream = UnixStream.new(socket)
            stream.extensionsMut()["UnixSocketInfo"] =
                UnixSocketInfo.new(localAddress = localAddress, peerAddress = peerAddress)
            service(stream)
        }
    }

    /**
     * Serve gracefully connections from this listener with the given service.
     *
     * This method does the same as [serve] but it
     * will respect the given shutdown guard, and also pass
     * it to the service.
     */
    suspend fun serveGraceful(
        guard: ShutdownGuard,
        service: suspend (UnixStream) -> Unit,
    ) {
        while (!guard.isCancelled()) {
            val (socket, peerAddress) =
                try {
                    inner.accept()
                } catch (error: Throwable) {
                    handleAcceptErr(error)
                    continue
                }

            val localAddress = socket.localAddr()
            val stream = UnixStream.new(socket)
            stream.extensionsMut()["UnixSocketInfo"] =
                UnixSocketInfo.new(localAddress = localAddress, peerAddress = peerAddress)
            guard.spawnTask {
                service(stream)
            }
        }
    }

    fun close() {
        cleanup?.cleanup()
    }
}

interface ShutdownGuard {
    fun isCancelled(): Boolean

    suspend fun spawnTask(block: suspend () -> Unit)
}

suspend fun handleAcceptErr(error: Throwable) {
    throw error
}
