package org.calamarfederal.messyink.common.compose.material3.exposed

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.VectorConverter
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.interaction.FocusInteraction.Focus
import androidx.compose.foundation.interaction.FocusInteraction.Unfocus
import androidx.compose.foundation.interaction.HoverInteraction.Enter
import androidx.compose.foundation.interaction.HoverInteraction.Exit
import androidx.compose.foundation.interaction.HoverInteraction
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.PressInteraction.Cancel
import androidx.compose.foundation.interaction.PressInteraction.Press
import androidx.compose.foundation.interaction.PressInteraction.Release
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Dp
import org.calamarfederal.messyink.common.compose.material3.exposed.ExposedElevationAnimation.DefaultIncomingSpec
import org.calamarfederal.messyink.common.compose.material3.exposed.ExposedElevationAnimation.DefaultOutgoingSpec


/**
 * facsimile of internal Button elevation animation
 */
@Composable
fun ExposedButtonElevation.animateElevation(
    enabled: Boolean,
    interactionSource: InteractionSource,
): State<Dp> {
    val interactions = remember { mutableStateListOf<Interaction>() }
    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect {
            when (it) {
                is Enter   -> interactions.add(it)
                is Exit    -> interactions.remove(it.enter)
                is Focus   -> interactions.add(it)
                is Unfocus -> interactions.remove(it.focus)
                is Press   -> interactions.add(it)
                is Release -> interactions.remove(it.press)
                is Cancel  -> interactions.remove(it.press)
            }
        }
    }

    val interaction = interactions.lastOrNull()
    val target = if (!enabled) disabled else when (interaction) {
        is Press -> pressed
        is Enter -> hovered
        is Focus -> focused
        else     -> default
    }
    val animatable = remember { Animatable(target, Dp.VectorConverter) }

    if (!enabled) LaunchedEffect(target) { animatable.snapTo(target) }
    else LaunchedEffect(target) {
        val lastInteraction = when (animatable.targetValue) {
            pressed -> Press(Offset.Zero)
            hovered -> Enter()
            focused -> Focus()
            else    -> null
        }
        animatable.animateElevation(target = target, from = lastInteraction, to = interaction)
    }

    return animatable.asState()
}

/**
 * maps [Interaction] to an [animateTo][Animatable.animateTo] or else [snapTo][Animatable.snapTo]
 *
 * facsimile of internal implementation.
 */
internal suspend fun Animatable<Dp, *>.animateElevation(
    target: Dp,
    from: Interaction? = null,
    to: Interaction? = null,
) {
    val spec = when {
        to != null   -> when (to) {
            is Press, is DragInteraction.Start,
            is Enter, is Focus,
                 -> DefaultIncomingSpec

            else -> null
        }

        from != null -> when (from) {
            is Press, is DragInteraction.Start, is Focus,
                     -> DefaultOutgoingSpec

            is Enter -> ExposedElevationAnimation.HoveredOutgoingSpec
            else     -> null
        }

        else         -> null
    }
    if (spec != null) animateTo(target, spec) else snapTo(target)
}

/**
 * facsimile of internal default values
 */
object ExposedElevationAnimation {
    /**
     * Default outgoing easing
     */
    private val DefaultOutgoingEasing: Easing = CubicBezierEasing(0.40f, 0.00f, 0.60f, 1.00f)

    /**
     * Default incoming spec for all defined [Interaction] begin events
     */
    val DefaultIncomingSpec = TweenSpec<Dp>(
        durationMillis = 120,
        easing = FastOutSlowInEasing,
    )

    /**
     * exposed internal default outgoing spec for all defined [Interaction] end events except [HoverInteraction]
     */
    val DefaultOutgoingSpec = TweenSpec<Dp>(
        durationMillis = 150,
        easing = DefaultOutgoingEasing,
    )

    /**
     * exposed internal spec for outgoing [HoverInteraction]
     */
    val HoveredOutgoingSpec = TweenSpec<Dp>(
        durationMillis = 120,
        easing = DefaultOutgoingEasing,
    )
}
