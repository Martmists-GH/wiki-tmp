package team.exr.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import java.io.File
import java.io.InputStream

object ConfigLoader {
    private val mapper = ObjectMapper(YAMLFactory()).also {
        it.registerModule(
            KotlinModule.Builder()
                .withReflectionCacheSize(512)
                .configure(KotlinFeature.NullToEmptyCollection, false)
                .configure(KotlinFeature.NullToEmptyMap, false)
                .configure(KotlinFeature.NullIsSameAsDefault, false)
                .configure(KotlinFeature.SingletonSupport, false)
                .configure(KotlinFeature.StrictNullChecks, false)
                .build()
        )
    }

    fun loadDefault() = load<SiteConfig>("site.yaml", "config/site.yaml")

    inline fun <reified T> load(path: String, resource: InputStream) = load<T>(path, resource.readAllBytes().decodeToString())
    inline fun <reified T> load(path: String, default: String) = load<T>(File(path), default)
    inline fun <reified T> load(file: File, resource: InputStream) = load(file, T::class.java, resource)
    inline fun <reified T> load(file: File, default: String) = load(file, T::class.java, T::class.java.getResourceAsStream("/$default")!!)
    fun <T> load(file: File, type: Class<T>, default: InputStream): T {
        if (!file.exists()) {
            file.createNewFile()
            file.outputStream().use { out ->
                default.copyTo(out)
            }
        }
        return mapper.readValue(file, type) as T
    }

    @Suppress("NOTHING_TO_INLINE")
    inline fun <T> save(path: String, obj: T) = save(File(path), obj)
    fun <T> save(file: File, obj: T) {
        mapper.writeValue(file, obj)
    }
}
