package io.github.vedantjoshi23.kairos.feature.map.domain

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

/** A skin bundle's metadata (STD-SKINS rule 3). */
data class SkinMeta(val id: String, val is3D: Boolean, val grid: Boolean, val accent: String)

object SkinMetaParser {
    fun parse(metaJson: String): SkinMeta {
        val o = Json.parseToJsonElement(metaJson).jsonObject
        val id = requireNotNull(o["codename"]?.jsonPrimitive?.contentOrNull) { "meta.json has no codename" }
        val is3D = o["is3D"]?.jsonPrimitive?.booleanOrNull ?: false
        if (is3D) require(o["fallback2D"]?.jsonPrimitive?.contentOrNull != null) { "3D skin $id needs a fallback2D (C1)" }
        return SkinMeta(
            id = id,
            is3D = is3D,
            grid = o["grid"]?.jsonPrimitive?.booleanOrNull ?: false,
            accent = o["accents"]?.jsonObject?.get("primary")?.jsonPrimitive?.contentOrNull ?: "#0E6B6A",
        )
    }
}

/**
 * The derived 2D fallback for 3D skins (C1; fallback2D = "derived:flatten"): every
 * fill-extrusion layer becomes a flat fill in the same colour, so a weak device keeps
 * the skin's look without the 3D cost.
 */
object StyleFlattener {
    private val json = Json { prettyPrint = false }

    fun flatten(styleJson: String): String {
        val style = json.parseToJsonElement(styleJson).jsonObject
        val layers = style["layers"]?.jsonArray ?: return styleJson
        val flat = layers.map { layer -> if (layer.type() == "fill-extrusion") toFill(layer.jsonObject) else layer }
        val out = style.toMutableMap()
        out["layers"] = JsonArray(flat)
        return json.encodeToString(JsonObject.serializer(), JsonObject(out))
    }

    private fun JsonElement.type() = jsonObject["type"]?.jsonPrimitive?.contentOrNull

    private fun toFill(layer: JsonObject): JsonObject {
        val paint = layer["paint"]?.jsonObject ?: JsonObject(emptyMap())
        val newPaint = buildMap<String, JsonElement> {
            put("fill-color", paint["fill-extrusion-color"] ?: JsonPrimitive("#888888"))
            put("fill-opacity", paint["fill-extrusion-opacity"] ?: JsonPrimitive(1.0))
        }
        return JsonObject(layer.toMutableMap().apply {
            put("type", JsonPrimitive("fill"))
            put("paint", JsonObject(newPaint))
        })
    }
}
