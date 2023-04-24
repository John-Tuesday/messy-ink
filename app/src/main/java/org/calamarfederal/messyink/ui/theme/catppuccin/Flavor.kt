package org.calamarfederal.messyink.ui.theme.catppuccin

import androidx.compose.ui.graphics.Color

typealias COLOR = Color

data class Flavor(
    val name: String,
    val rosewater: Color,
    val flamingo: Color,
    val pink: Color,
    val mauve: Color,
    val red: Color,
    val maroon: Color,
    val peach: Color,
    val yellow: Color,
    val green: Color,
    val teal: Color,
    val sky: Color,
    val sapphire: Color,
    val blue: Color,
    val lavender: Color,
    val text: Color,
    val subtext1: Color,
    val subtext0: Color,
    val overlay2: Color,
    val overlay1: Color,
    val overlay0: Color,
    val surface2: Color,
    val surface1: Color,
    val surface0: Color,
    val base: Color,
    val mantle: Color,
    val crust: Color,
) {
    companion object {}

    fun toMap() = mapOf(
        "rosewater" to rosewater,
        "flamingo" to flamingo,
        "pink" to pink,
        "mauve" to mauve,
        "red" to red,
        "maroon" to maroon,
        "peach" to peach,
        "yellow" to yellow,
        "green" to green,
        "teal" to teal,
        "sky" to sky,
        "sapphire" to sapphire,
        "blue" to blue,
        "lavender" to lavender,
        "text" to text,
        "subtext1" to subtext1,
        "subtext0" to subtext0,
        "overlay2" to overlay2,
        "overlay1" to overlay1,
        "overlay0" to overlay0,
        "surface2" to surface2,
        "surface1" to surface1,
        "surface0" to surface0,
        "base" to base,
        "mantle" to mantle,
        "crust" to crust,
    )
}