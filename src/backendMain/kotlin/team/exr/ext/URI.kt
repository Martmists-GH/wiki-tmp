package team.exr.ext

import java.net.URI
import java.nio.file.FileSystems
import java.nio.file.Path
import java.nio.file.Paths

fun URI.getPath(block: (Path) -> Unit) {
    if (this.toString().startsWith("file:")) {
        val path = Paths.get(this)
        block(path)
    } else {
        // assume jar
        val parts = this.toString().split("!")
        val fs = FileSystems.newFileSystem(URI.create(parts.first()), emptyMap<String, String>())
        val path = fs.getPath(parts.last())
        block(path)
        fs.close()
    }
}
