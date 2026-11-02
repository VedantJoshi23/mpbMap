package io.github.vedantjoshi23.kairos.feature.map.domain

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SkinsTest {
    private val skinsDir = generateSequence(File("").absoluteFile) { it.parentFile }
        .map { File(it, "skins") }.first { it.isDirectory }

    @Test fun everyBundledSkinParsesAndThreeDSkinsDeclareAFallback() {
        val metas = skinsDir.listFiles()!!.filter { it.isDirectory }.map { SkinMetaParser.parse(File(it, "meta.json").readText()) }
        assertTrue(metas.map { it.id }.containsAll(listOf("terrain-sepia", "vertical-night", "cube-3d", "tactical-grid")))
        assertTrue(metas.single { it.id == "tactical-grid" }.grid)
        assertTrue(metas.single { it.id == "cube-3d" }.is3D)
    }

    @Test fun threeDSkinWithoutFallbackIsRejected() {
        assertFailsWith<IllegalArgumentException> {
            SkinMetaParser.parse("""{"codename":"x","is3D":true,"fallback2D":null}""")
        }
    }

    @Test fun flatteningRemovesEveryExtrusionAndKeepsTheColour() {
        for (id in listOf("vertical-night", "cube-3d")) {
            val raw = File(skinsDir, "$id/style.json").readText()
            assertTrue(raw.contains("fill-extrusion"))
            val flat = StyleFlattener.flatten(raw)
            assertFalse(flat.contains("fill-extrusion"), "$id still has an extrusion")
            val building = Json.parseToJsonElement(flat).jsonObject["layers"]!!.jsonArray
                .first { it.jsonObject["id"]!!.jsonPrimitive.content == "building-3d" }.jsonObject
            assertEquals("fill", building["type"]!!.jsonPrimitive.content)
            assertTrue(building["paint"]!!.jsonObject.containsKey("fill-color"))
        }
    }

    @Test fun noBundledSkinDrawsBoundariesEvenAfterTheGuard() {
        for (dir in skinsDir.listFiles()!!.filter { it.isDirectory }) {
            val guarded = StyleGuard.prepare(File(dir, "style.json").readText(), "https://t/{z}/{x}/{y}.pbf", null)
            assertEquals(0, guarded.removedLayers, "${dir.name} shipped a boundary layer")
        }
    }
}
