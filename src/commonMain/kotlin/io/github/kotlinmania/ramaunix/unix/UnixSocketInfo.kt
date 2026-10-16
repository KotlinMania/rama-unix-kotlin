package io.github.kotlinmania.ramaunix.unix

/**
 * Information about the socket on the egress end.
 */
data class ClientUnixSocketInfo(
    val value: UnixSocketInfo,
) {
    fun asUnixSocketInfo(): UnixSocketInfo = value

    fun asRef(): UnixSocketInfo = value

    fun asMut(): UnixSocketInfo = value

    fun deref(): UnixSocketInfo = value

    fun derefMut(): UnixSocketInfo = value
}

/**
 * Connected Unix socket information.
 */
@ConsistentCopyVisibility
data class UnixSocketInfo private constructor(
    private val localAddress: UnixSocketAddress?,
    private val peerAddress: UnixSocketAddress,
) {
    companion object {
        /**
         * Create a new [UnixSocketInfo].
         */
        fun new(
            localAddress: UnixSocketAddress?,
            peerAddress: UnixSocketAddress,
        ): UnixSocketInfo =
            UnixSocketInfo(
                localAddress = localAddress,
                peerAddress = peerAddress,
            )
    }

    /**
     * Try to get the address of the local Unix domain socket.
     */
    fun localAddr(): UnixSocketAddress? = localAddress

    /**
     * Get the address of the peer Unix domain socket.
     */
    fun peerAddr(): UnixSocketAddress = peerAddress
}
