package com.sindicato.aplicacionaccesible.ui.comunicacion

import com.sindicato.aplicacionaccesible.data.PhraseEntity
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DuplicatePhraseTest {

    private val saved = listOf(PhraseEntity(id = 1, text = "Hola, ¿cómo estás?"))

    @Test
    fun ignoresCaseAndSurroundingSpaces() {
        assertTrue(isDuplicatePhrase("  hola, ¿CÓMO estás?  ", saved))
    }

    @Test
    fun differentTextIsNotDuplicate() {
        assertFalse(isDuplicatePhrase("Hola", saved))
    }

    @Test
    fun emptyListHasNoDuplicates() {
        assertFalse(isDuplicatePhrase("Hola", emptyList()))
    }
}
