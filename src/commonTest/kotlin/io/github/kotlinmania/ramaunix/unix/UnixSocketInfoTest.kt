// port-lint: ignore
package io.github.kotlinmania.ramaunix.unix

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

class UnixSocketInfoTest {
    @Test
    fun unnamedAddressReportsNoPathname() {
        val address = UnixSocketAddress.unnamed()

        assertTrue(address.isUnnamed())
        assertNull(address.asPathname())
    }

    @Test
    fun pathnameAddressReportsPathname() {
        val address = UnixSocketAddress.pathname("/tmp/rama.sock")

        assertFalse(address.isUnnamed())
        assertEquals("/tmp/rama.sock", address.asPathname())
    }

    @Test
    fun socketInfoKeepsLocalAndPeerAddresses() {
        val local = UnixSocketAddress.pathname("/tmp/local.sock")
        val peer = UnixSocketAddress.pathname("/tmp/peer.sock")
        val info = UnixSocketInfo.new(local, peer)

        assertSame(local, info.localAddr())
        assertSame(peer, info.peerAddr())
        assertSame(info, ClientUnixSocketInfo(info).asUnixSocketInfo())
    }
}
