package io.github.vedantjoshi23.kairos.feature.map.domain

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class StyleGuardTest {
    private val style = """
        {"version":8,"sources":{"openmaptiles":{"type":"vector","url":"https://example/tiles.json"}},
         "layers":[
          {"id":"bg","type":"background"},
          {"id":"water","type":"fill","source":"openmaptiles","source-layer":"water"},
          {"id":"country","type":"line","source":"openmaptiles","source-layer":"boundary","filter":["==","admin_level",2]},
          {"id":"maritime","type":"line","source":"openmaptiles","source-layer":"boundary","filter":["==","maritime",1]},
          {"id":"state","type":"line","source":"openmaptiles","source-layer":"boundary","filter":["==","admin_level",4]}
         ]}
    """.trimIndent()

    @Test fun stripsEveryBoundaryLayerAtEveryLevelIncludingMaritime() {
        val result = StyleGuard.prepare(style, "https://t/{z}/{x}/{y}.pbf", null)
        assertEquals(3, result.removedLayers)
        val layers = Json.parseToJsonElement(result.styleJson).jsonObject["layers"]!!.jsonArray
        assertEquals(listOf("bg", "water"), layers.map { it.jsonObject["id"]!!.jsonPrimitive.content })
        assertFalse(result.styleJson.contains("\"boundary\""))
    }

    @Test fun substitutesSourceEndpointsOnly() {
        val result = StyleGuard.prepare(style, "https://t/{z}/{x}/{y}.pbf", "asset://glyphs/{fontstack}/{range}.pbf")
        val obj = Json.parseToJsonElement(result.styleJson).jsonObject
        val src = obj["sources"]!!.jsonObject["openmaptiles"]!!.jsonObject
        assertEquals("https://t/{z}/{x}/{y}.pbf", src["tiles"]!!.jsonArray.single().jsonPrimitive.content)
        assertEquals(null, src["url"])
        assertEquals("asset://glyphs/{fontstack}/{range}.pbf", obj["glyphs"]!!.jsonPrimitive.content)
    }
}
