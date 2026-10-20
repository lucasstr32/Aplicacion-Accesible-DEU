package com.sindicato.aplicacionaccesible.ui

import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assertHeightIsAtLeast
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertWidthIsAtLeast
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sindicato.aplicacionaccesible.data.PhraseEntity
import com.sindicato.aplicacionaccesible.ui.components.EmptyCellPlaceholder
import com.sindicato.aplicacionaccesible.ui.components.PitchSelector
import com.sindicato.aplicacionaccesible.ui.comunicacion.QuickPhraseCard
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TouchTargetTest {

    @get:Rule
    val composeRule = createComposeRule()

    private fun SemanticsNodeInteraction.assertMinTouchTarget() {
        assertWidthIsAtLeast(48.dp)
        assertHeightIsAtLeast(48.dp)
    }

    @Test
    fun deletePhraseButtonIsAtLeast48dp() {
        composeRule.setContent {
            QuickPhraseCard(
                phrase = PhraseEntity(id = 1, text = "Hola"),
                onClick = {},
                onDelete = {}
            )
        }
        composeRule.onNodeWithContentDescription("Eliminar frase: Hola").assertMinTouchTarget()
    }

    @Test
    fun pitchOptionsAreAtLeast48dpAndLabelled() {
        composeRule.setContent {
            PitchSelector(currentPitch = 1.0f, onPitchSelected = {})
        }
        listOf("Tono muy grave", "Tono grave", "Tono normal", "Tono agudo", "Tono muy agudo")
            .forEach { label ->
                composeRule.onNodeWithContentDescription(label).assertMinTouchTarget()
            }
        composeRule.onNodeWithContentDescription("Tono normal").assertIsSelected()
    }

    @Test
    fun emptyCellIsAtLeast48dp() {
        composeRule.setContent {
            EmptyCellPlaceholder(onClick = {})
        }
        composeRule.onAllNodesWithContentDescription("Añadir Botón")[0].assertMinTouchTarget()
    }
}
