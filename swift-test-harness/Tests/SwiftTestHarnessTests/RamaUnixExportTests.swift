#if canImport(Testing)
import Testing
import RamaUnix

@Suite("RamaUnix Swift Export Tests")
struct RamaUnixExportTests {
    @Test("Swift module loads")
    func testSwiftModuleLoads() {
        #expect(Bool(true), "RamaUnix swift module imported cleanly")
    }
}
#elseif canImport(XCTest)
import XCTest
import RamaUnix

final class RamaUnixExportTests: XCTestCase {
    func testSwiftModuleLoads() throws {
        XCTAssertTrue(true, "RamaUnix swift module imported cleanly")
    }
}
#endif
