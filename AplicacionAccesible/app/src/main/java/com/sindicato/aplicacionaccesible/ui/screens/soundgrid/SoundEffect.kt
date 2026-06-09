package com.sindicato.aplicacionaccesible.ui.screens.soundgrid

import com.sindicato.aplicacionaccesible.R

enum class SoundEffect(val id:Int, val displayName: String, val resourceId: Int) {
    KISS(1, "Kiss", R.raw.kiss),
    CLAPPING(2, "Clapping", R.raw.clapping),
    BOXING_BELL(3, "Boxing Bell", R.raw.boxingbell),
    SCHOOL_BELL(4, "School Bell", R.raw.schoolbell),
    FF7_VICTORY(5, "FF7 Victory", R.raw.ff7_victory),
    POLICE_ALERT(6, "Police Alert", R.raw.alertapolicia),
    TRICK_JUMP(7, "Trick Jump", R.raw.se_objsn_trickjump_ok),
}