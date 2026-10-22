package com.pixvpn.gram

/** Notices rendered by the bundled Aether 2.3 Psiphon adapter. */
object PsiphonNotice {
    private val ansi = Regex("\u001B\\[[0-9;]*[A-Za-z]")
    private const val REGIONS = "psiphon can leave from:"

    fun availableRegions(line: String): Set<String>? {
        val clean = ansi.replace(line, "")
        val start = clean.indexOf(REGIONS)
        if (start < 0) return null
        val tokens = clean.substring(start + REGIONS.length).trim().split(Regex("\\s+"))
        // Malformed/truncated notices must never be interpreted as an empty server list.
        if (tokens.isEmpty() || tokens.any { !it.matches(Regex("[A-Z]{2}")) }) return null
        return tokens.toSet()
    }
}
