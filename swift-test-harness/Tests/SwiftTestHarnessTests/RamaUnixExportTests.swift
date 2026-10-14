#if canImport(Testing)
import Testing
import RamaUnix

@Suite("RamaUnix Swift Export Tests")
struct RamaUnixExportTests {
    @Test("Swift module loads and exercises primary exported types")
    func testSwiftModuleLoads() {
        let unnamed = unix.UnixSocketAddress.Companion.shared.unnamed()
        #expect(unnamed.isUnnamed())
        #expect(unnamed.asPathname() == nil)

        let pathAddr = unix.UnixSocketAddress.Companion.shared.pathname(pathname: "/tmp/rama.sock")
        #expect(!pathAddr.isUnnamed())
        #expect(pathAddr.asPathname() == "/tmp/rama.sock")

        let info = unix.UnixSocketInfo.Companion.shared.new(localAddress: unnamed, peerAddress: pathAddr)
        #expect(info.localAddr()?.isUnnamed() == true)
        #expect(info.peerAddr().asPathname() == "/tmp/rama.sock")

        let target = unix.client.UnixTarget(path: "/tmp/rama.sock")
        #expect(target.path == "/tmp/rama.sock")
    }
}
#elseif canImport(XCTest)
import XCTest
import RamaUnix

final class RamaUnixExportTests: XCTestCase {
    func testSwiftModuleLoads() throws {
        let unnamed = unix.UnixSocketAddress.Companion.shared.unnamed()
        XCTAssertTrue(unnamed.isUnnamed())
        XCTAssertNil(unnamed.asPathname())

        let pathAddr = unix.UnixSocketAddress.Companion.shared.pathname(pathname: "/tmp/rama.sock")
        XCTAssertFalse(pathAddr.isUnnamed())
        XCTAssertEqual(pathAddr.asPathname(), "/tmp/rama.sock")
    }
}
#endif
