package io.github.kotlinmania.ramaunix.unix

import io.github.kotlinmania.ramaunix.unix.client.UnixConnector
import io.github.kotlinmania.ramaunix.unix.client.UnixStreamConnector
import io.github.kotlinmania.ramaunix.unix.server.TokioUnixListener
import io.github.kotlinmania.ramaunix.unix.server.UnixListener
import io.github.kotlinmania.ramaunix.unix.server.UnixSocket
import io.github.kotlinmania.ramaunix.unix.server.UnixSocketCleanup
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.startCoroutine
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

class UnixSocketInfoTest {
    private fun <T> runTestSync(block: suspend () -> T): T {
        var executionResult: Result<T>? = null
        val continuation =
            object : Continuation<T> {
                override val context: CoroutineContext = EmptyCoroutineContext

                override fun resumeWith(result: Result<T>) {
                    executionResult = result
                }
            }
        block.startCoroutine(continuation)
        return executionResult?.getOrThrow() ?: error("Coroutine did not complete synchronously")
    }

    @Test
    fun unnamedAddressReportsNoPathname() {
        val address = UnixSocketAddress.unnamed()

        assertTrue(address.isUnnamed())
        assertNull(address.asPathname())
        assertEquals("UnixSocketAddress(unnamed)", address.fmt())
        assertEquals(UnixSocketAddress.unnamed(), address)
    }

    @Test
    fun pathnameAddressReportsPathname() {
        val address = UnixSocketAddress.pathname("/tmp/rama.sock")

        assertFalse(address.isUnnamed())
        assertEquals("/tmp/rama.sock", address.asPathname())
        assertEquals("UnixSocketAddress(pathname=/tmp/rama.sock)", address.fmt())
        assertEquals(UnixSocketAddress.pathname("/tmp/rama.sock"), address)
    }

    @Test
    fun socketInfoKeepsLocalAndPeerAddresses() {
        val local = UnixSocketAddress.pathname("/tmp/local.sock")
        val peer = UnixSocketAddress.pathname("/tmp/peer.sock")
        val info = UnixSocketInfo.new(local, peer)

        assertSame(local, info.localAddr())
        assertSame(peer, info.peerAddr())

        val clientInfo = ClientUnixSocketInfo(info)
        assertSame(info, clientInfo.asUnixSocketInfo())
        assertSame(info, clientInfo.asRef())
        assertSame(info, clientInfo.asMut())
        assertSame(info, clientInfo.deref())
        assertSame(info, clientInfo.derefMut())
    }

    @Test
    fun unixStreamExtensionsAndOperations() {
        runTestSync {
            val stream = TokioUnixStream.connect("/tmp/stream.sock")
            val unixStream = UnixStream.new(stream)

            assertSame(stream, unixStream.intoTokioUnixStream())
            assertNotNull(unixStream.extensions)
            assertNotNull(unixStream.extensionsMut())
            assertFalse(unixStream.isWriteVectored())

            val testBytes = byteArrayOf(1, 2, 3)
            assertEquals(3, unixStream.pollWrite(testBytes))
            assertEquals(3, unixStream.pollWriteVectored(listOf(testBytes)))
            assertEquals(0, unixStream.pollRead(testBytes))
            unixStream.pollFlush()
            unixStream.pollShutdown()

            unixStream.extensions["key"] = "value"
            assertTrue(unixStream.extensions.contains("key"))
            assertEquals("value", unixStream.extensions["key"])
            assertEquals(1, unixStream.extensions.size())
            assertFalse(unixStream.extensions.isEmpty())
            unixStream.extensions.remove("key")
            assertTrue(unixStream.extensions.isEmpty())
            unixStream.extensions.clear()
        }
    }

    @Test
    fun unixListenerConstructionAndOperations() {
        runTestSync {
            val builder = UnixListener.build()
            assertNotNull(builder)

            val listener = UnixListener.bindPath("/tmp/listener.sock")
            assertEquals("/tmp/listener.sock", listener.localAddr().asPathname())
            assertEquals(-1, listener.asRawFd())
            assertEquals(-1, listener.asFd())

            val (stream, addr) = listener.accept()
            assertNotNull(stream)
            assertNotNull(addr)

            val socket = UnixSocket()
            val socketListener = UnixListener.bindSocket(socket)
            assertNotNull(socketListener)

            val tokioListener = TokioUnixListener()
            val fromListener = UnixListener.from(tokioListener)
            assertNotNull(fromListener)

            val tryFromListener = UnixListener.tryFrom(tokioListener)
            assertNotNull(tryFromListener)

            val cleanup = UnixSocketCleanup("/tmp/cleanup.sock")
            cleanup.drop()
            listener.close()
        }
    }

    @Test
    fun unixDatagramFramedOperations() {
        runTestSync {
            class MockDecoder : Decoder<String> {
                override fun decodeEof(buffer: ByteArray): String? =
                    if (buffer.isNotEmpty()) buffer.decodeToString() else null
            }

            class MockEncoder : Encoder<String> {
                override fun encode(item: String): ByteArray = item.encodeToByteArray()
            }

            class MockCodec : Decoder<String> by MockDecoder(), Encoder<String> by MockEncoder()

            val socket = UnixDatagram()
            val codec = MockCodec()
            val framed = UnixDatagramFramed.new(socket, codec)

            assertSame(socket, framed.getRef())
            assertSame(socket, framed.getMut())
            assertSame(codec, framed.codec())
            assertSame(codec, framed.codecMut())
            assertEquals(0, framed.readBuffer().size)
            assertEquals(0, framed.readBufferMut().size)

            val sendResult = framed.startSend("hello" to UnixSocketAddress.pathname("/tmp/target.sock"))
            assertTrue(sendResult.isSuccess)

            val flushResult = framed.pollFlush()
            assertTrue(flushResult.isSuccess)

            val readyResult = framed.pollReady()
            assertTrue(readyResult.isSuccess)

            val closeResult = framed.pollClose()
            assertTrue(closeResult.isSuccess)

            assertSame(socket, framed.intoInner())
        }
    }

    @Test
    fun unixConnectorOperations() {
        runTestSync {
            val connector = UnixConnector.fixed("/tmp/conn.sock")
            val conn = connector.serve("request")
            assertEquals("request", conn.input)
            assertNotNull(conn.conn)

            val customConnector = object : UnixStreamConnector {
                override suspend fun connect(path: String): UnixStream =
                    UnixStream.from(TokioUnixStream.connect(path))
            }

            val withConn = connector.withConnector(customConnector)
            val conn2 = withConn.serve(42)
            assertEquals(42, conn2.input)
        }
    }
}
