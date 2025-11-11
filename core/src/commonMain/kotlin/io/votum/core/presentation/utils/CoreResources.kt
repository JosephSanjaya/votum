package io.votum.core.presentation.utils

import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import votum.core.generated.resources.Res
import votum.core.generated.resources.cta_continue
import votum.core.generated.resources.cta_skip
import votum.core.generated.resources.ic_arrow_forward

object CoreResources {
    val ctaContinue: StringResource
        get() = Res.string.cta_continue

    val ctaSkip: StringResource
        get() = Res.string.cta_skip

    val icArrowForward: DrawableResource
        get() = Res.drawable.ic_arrow_forward
}
