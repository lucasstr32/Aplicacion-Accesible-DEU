package com.sindicato.aplicacionaccesible.ui.screens.soundgrid

import com.sindicato.aplicacionaccesible.R

enum class SoundEffect(val id:Int, val displayName: String, val resourceId: Int) {
    AWW(1, "Aww", R.raw.aww),
    BELL(2, "Campana", R.raw.bell),
    FEMALE_SIGH(3, "Suspiro femenino", R.raw.female_sigh),
    MALE_SIGH(4, "Suspiro masculino", R.raw.male_sigh),
    GASP(5, "Sorpresa", R.raw.gasp),
    MALE_OUCH(6, "OUCH masculino", R.raw.ouch),
    MALE_THINKING(7, "Pensado masculino", R.raw.thinking_male),
    ALARM(8, "Alarma", R.raw.alarm),
    ALARM_CLOCK(9, "Despertador", R.raw.alarm_clock),
    HORN(10, "Bocina", R.raw.horn),
    DOORBELL(11, "Timbre", R.raw.doorbell),
    WHISTLE(12, "Silbato", R.raw.whistle),
    APPLAUSE(13, "Aplausos", R.raw.applause),
    KNOCK(14, "Golpes en la puerta", R.raw.knock);

    companion object {
        fun fromDisplayName(name: String?): SoundEffect {
            return entries.find { it.displayName == name } ?: SoundEffect.MALE_SIGH
        }
    }
}