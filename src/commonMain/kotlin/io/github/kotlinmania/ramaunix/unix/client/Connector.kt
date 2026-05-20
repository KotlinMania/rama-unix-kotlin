// port-lint: source unix/client/connector.rs
package io.github.kotlinmania.ramaunix.unix.client

import io.github.kotlinmania.ramaunix.unix.ClientUnixSocketInfo
import io.github.kotlinmania.ramaunix.unix.TokioUnixStream
import io.github.kotlinmania.ramaunix.unix.UnixSocketInfo
import io.github.kotlinmania.ramaunix.unix.UnixStream

/**
 * A connector which can be used to establish a Unix connection to a server.
 */
class UnixConnector<ConnectorFactory, Connector> private constructor(
    private val connectorFactory: ConnectorFactory,
    private val target: UnixTarget,
) where ConnectorFactory : UnixStreamConnectorFactory<Connector>, Connector : UnixStreamConnector {
    companion object {
        /**
         * Create a new [UnixConnector], which is used to establish a connection to a server
         * at a fixed path.
         *
         * You can use middleware around the [UnixConnector]
         * or add connection pools, retry logic and more.
         */
        fun fixed(path: String): UnixConnector<UnixStreamConnectorCloneFactory<UnixStreamConnector>, UnixStreamConnector> =
            UnixConnector(
                connectorFactory = UnixStreamConnectorCloneFactory(DefaultUnixStreamConnector),
                target = UnixTarget(path),
            )
    }

    /**
     * Consume this connector to attach the given connector as a new [UnixConnector].
     */
    fun <Connector> withConnector(
        connector: Connector,
    ): UnixConnector<UnixStreamConnectorCloneFactory<Connector>, Connector> where Connector : UnixStreamConnector =
        UnixConnector(
            connectorFactory = UnixStreamConnectorCloneFactory(connector),
            target = target,
        )

    /**
     * Consume this connector to attach the given factory as a new [UnixConnector].
     */
    fun <Factory, FactoryConnector> withConnectorFactory(factory: Factory): UnixConnector<Factory, FactoryConnector>
        where Factory : UnixStreamConnectorFactory<FactoryConnector>, FactoryConnector : UnixStreamConnector =
        UnixConnector(
            connectorFactory = factory,
            target = target,
        )

    suspend fun <Input> serve(input: Input): EstablishedClientConnection<UnixStream, Input> {
        val connector = connectorFactory.makeConnector()
        val conn = connector.connect(target.path)
        val info =
            ClientUnixSocketInfo(
                UnixSocketInfo.new(
                    localAddress = conn.stream.localAddr(),
                    peerAddress = conn.stream.peerAddr(),
                ),
            )
        conn.extensionsMut()["ClientUnixSocketInfo"] = info
        return EstablishedClientConnection(input = input, conn = conn)
    }
}

/**
 * Type of [UnixConnector] which connects to a fixed path.
 */
data class UnixTarget(
    val path: String,
)

/**
 * Trait used by the [UnixConnector] to actually establish the [UnixStream].
 */
interface UnixStreamConnector {
    /**
     * Connect to the path and return the established [UnixStream].
     */
    suspend fun connect(path: String): UnixStream
}

private object DefaultUnixStreamConnector : UnixStreamConnector {
    override suspend fun connect(path: String): UnixStream =
        UnixStream.from(TokioUnixStream.connect(path))
}

/**
 * Factory to create a [UnixStreamConnector]. This is used by the Unix
 * stream service to create a stream within a specific context.
 *
 * In the most simple case you use a [UnixStreamConnectorCloneFactory]
 * to use a cloneable [UnixStreamConnector], but in more
 * advanced cases you can use variants of [UnixStreamConnector] specific
 * to the given contexts.
 */
interface UnixStreamConnectorFactory<out Connector> where Connector : UnixStreamConnector {
    /**
     * Try to create a [UnixStreamConnector], and return an error or otherwise.
     */
    suspend fun makeConnector(): Connector
}

/**
 * Utility implementation of a [UnixStreamConnectorFactory] which is implemented
 * to allow one to use a cloneable [UnixStreamConnector] as a [UnixStreamConnectorFactory]
 * by cloning itself.
 *
 * This struct cannot be created by third party crates
 * and instead is to be used via other API's provided by this crate.
 */
data class UnixStreamConnectorCloneFactory<C>(
    private val connector: C,
) : UnixStreamConnectorFactory<C> where C : UnixStreamConnector {
    override suspend fun makeConnector(): C = connector
}

data class EstablishedClientConnection<Conn, Input>(
    val input: Input,
    val conn: Conn,
)
