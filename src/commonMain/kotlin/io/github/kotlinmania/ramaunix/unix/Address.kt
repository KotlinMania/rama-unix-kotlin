// port-lint: source unix/address.rs
package io.github.kotlinmania.ramaunix.unix

/**
 * An address associated with a Unix socket.
 *
 * This type is a thin wrapper around a Unix socket address. You can create
 * unnamed and pathname addresses with the companion constructors.
 */
class UnixSocketAddress private constructor(
    private val pathname: String?,
    private val unnamed: Boolean,
) {
    companion object {
        fun unnamed(): UnixSocketAddress =
            UnixSocketAddress(pathname = null, unnamed = true)

        fun pathname(pathname: String): UnixSocketAddress =
            UnixSocketAddress(pathname = pathname, unnamed = false)
    }

    /**
     * Returns `true` if the address is unnamed.
     *
     * Documentation reflected in the socket address.
     */
    fun isUnnamed(): Boolean = unnamed

    /**
     * Returns the contents of this address if it is a pathname address.
     *
     * Documentation reflected in the socket address.
     */
    fun asPathname(): String? = pathname

    fun fmt(): String = toString()

    fun from(pathname: String): UnixSocketAddress =
        pathname(pathname)

    fun fromUnnamed(): UnixSocketAddress =
        unnamed()

    override fun toString(): String =
        if (unnamed) {
            "UnixSocketAddress(unnamed)"
        } else {
            "UnixSocketAddress(pathname=$pathname)"
        }

    override fun equals(other: Any?): Boolean =
        other is UnixSocketAddress &&
            pathname == other.pathname &&
            unnamed == other.unnamed

    override fun hashCode(): Int =
        31 * pathname.hashCode() + unnamed.hashCode()
}
