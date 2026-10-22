package com.pixvpn.gram

import org.junit.Assert.*
import org.junit.Test
import java.net.ServerSocket
import java.util.Locale
import kotlin.concurrent.thread

class BrazilConnectionTest {
    @Test fun brazilCodeIsNormalized() {
        assertEquals("BR", PsiphonRegions.normalize(" br \n"))
        assertEquals("", PsiphonRegions.normalize("Brazil"))
        assertEquals("", PsiphonRegions.normalize("B1"))
        assertEquals("", PsiphonRegions.normalize(""))
    }

    @Test fun normalizationDoesNotUseDeviceLocale() {
        val old = Locale.getDefault()
        try {
            Locale.setDefault(Locale.forLanguageTag("tr-TR"))
            assertEquals("IN", PsiphonRegions.normalize("in"))
        } finally { Locale.setDefault(old) }
    }

    @Test fun parsesActualAetherNotice() {
        assertEquals(setOf("BR", "CA", "US"), PsiphonNotice.availableRegions(
            "2026-10-22 INFO [*] psiphon can leave from: BR CA US"))
        assertEquals(setOf("BR"), PsiphonNotice.availableRegions(
            "\u001B[32m[*] psiphon can leave from: BR\u001B[0m"))
    }

    @Test fun absentBrazilIsNotConfusedWithUnknownRegions() {
        val offered = PsiphonNotice.availableRegions("[*] psiphon can leave from: DE US")!!
        assertFalse("BR" in offered)
        assertNull(PsiphonNotice.availableRegions("unrelated server log"))
        assertNull(PsiphonNotice.availableRegions("[*] psiphon can leave from:"))
        assertNull(PsiphonNotice.availableRegions("[*] psiphon can leave from: BR broken"))
    }

    @Test fun only204CountsAsSuccessfulProbe() {
        assertTrue(Socks.isSuccessfulProbe(1L to "HTTP/1.1 204 No Content\r\n\r\n"))
        assertTrue(Socks.isSuccessfulProbe(1L to "HTTP/1.0 204\r\n\r\n"))
        for (reply in listOf("HTTP/1.1 200 OK", "HTTP/1.1 302 Found", "HTTP/1.1 503 Error",
            "HTTP/1.1 2040 Invalid", "HTTP/1.1 500 HTTP/1.1 204 No Content", "garbage")) {
            assertFalse(reply, Socks.isSuccessfulProbe(1L to "$reply\r\n\r\n"))
        }
        assertFalse(Socks.isSuccessfulProbe(null))
    }

    private fun throughFakeSocks(status: String): Pair<Long, String>? = ServerSocket(0).use { server ->
        val worker = thread(isDaemon = true) {
            server.accept().use { socket ->
                socket.soTimeout = 3000
                val input = java.io.DataInputStream(socket.getInputStream())
                val output = socket.getOutputStream()
                val greeting = ByteArray(3); input.readFully(greeting)
                output.write(byteArrayOf(5, 0)); output.flush()
                val request = ByteArray(4); input.readFully(request)
                val domain = ByteArray(input.readUnsignedByte()); input.readFully(domain)
                input.readUnsignedShort()
                output.write(byteArrayOf(5, 0, 0, 1, 127, 0, 0, 1, 0, 80)); output.flush()
                val reader = socket.getInputStream().bufferedReader()
                while (!reader.readLine().isNullOrEmpty()) { }
                output.write("HTTP/1.1 $status\r\nConnection: close\r\nContent-Length: 0\r\n\r\n".toByteArray())
                output.flush()
            }
        }
        val result = Socks.httpGet(server.localPort, "probe.test", "/generate_204", 2000)
        worker.join(3000)
        assertFalse("fake server did not finish", worker.isAlive)
        result
    }

    @Test fun workingSocksTunnelPasses() {
        assertTrue(Socks.isSuccessfulProbe(throughFakeSocks("204 No Content")))
    }

    @Test fun socksErrorPageIsNotConnected() {
        assertFalse(Socks.isSuccessfulProbe(throughFakeSocks("503 Service Unavailable")))
    }

    @Test fun tricklingHandshakeCannotExtendTotalBudget() {
        ServerSocket(0).use { server ->
            val worker = thread(isDaemon = true) {
                try {
                    server.accept().use { socket ->
                        java.io.DataInputStream(socket.getInputStream()).readFully(ByteArray(3))
                        socket.getOutputStream().write(5)
                        socket.getOutputStream().flush()
                        Thread.sleep(350)
                        socket.getOutputStream().write(0)
                        socket.getOutputStream().flush()
                    }
                } catch (_: java.io.IOException) { }
            }
            val start = System.nanoTime()
            assertNull(Socks.httpGet(server.localPort, "probe.test", "/generate_204", 200))
            val elapsed = (System.nanoTime() - start) / 1_000_000
            assertTrue("trickling proxy exceeded deadline: $elapsed ms", elapsed < 1000)
            worker.join(1500)
        }
    }

    @Test fun listeningPortWithoutSocksTunnelFailsWithinBudget() {
        ServerSocket(0).use { server ->
            val start = System.nanoTime()
            assertNull(Socks.httpGet(server.localPort, "probe.test", "/generate_204", 200))
            val elapsed = (System.nanoTime() - start) / 1_000_000
            assertTrue("probe exceeded budget: $elapsed ms", elapsed < 1500)
        }
    }
}
