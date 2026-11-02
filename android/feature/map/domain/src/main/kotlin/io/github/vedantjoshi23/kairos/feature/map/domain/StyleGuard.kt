package io.github.vedantjoshi23.kairos.feature.map.domain

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

/**
 * The runtime boundary guard (Law 12, ADR-0003): strips every layer that draws the
 * `boundary` source-layer before the style reaches the renderer, and substitutes the
 * remote-configurable source endpoints. Styles themselves are bundled and linted.
 */
object StyleGuard {
    const val BOUNDARY_SOURCE_LAYER = "boundary"
    private val json = Json { prettyPrint = false }

    data class Result(val styleJson: String, val removedLayers: Int)

    fun prepare(styleJson: String, tileUrl: String, glyphUrl: String?): Result {
        val style = json.parseToJsonElement(styleJson).jsonObject
        val layers = style["layers"]?.jsonArray ?: JsonArray(emptyList())
        val kept = layers.filterNot { it.jsonObject["source-layer"]?.jsonPrimitive?.content == BOUNDARY_SOURCE_LAYER }
        val sources = style["sources"]?.jsonObject?.mapValues { (_, src) -> withTiles(src, tileUrl) } ?: emptyMap()
        val out = style.toMutableMap()
        out["layers"] = JsonArray(kept)
        out["sources"] = JsonObject(sources)
        if (glyphUrl != null) out["glyphs"] = JsonPrimitive(glyphUrl)
        return Result(json.encodeToString(JsonObject.serializer(), JsonObject(out)), layers.size - kept.size)
    }

    private fun withTiles(source: JsonElement, tileUrl: String): JsonElement {
        val obj = source.jsonObject
        if (obj["type"]?.jsonPrimitive?.content != "vector") return source
        val m = obj.toMutableMap()
        m.remove("url")
        m["tiles"] = JsonArray(listOf(JsonPrimitive(tileUrl)))
        return JsonObject(m)
    }
}
