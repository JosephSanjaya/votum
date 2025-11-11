package io.votum.core.presentation.preview
import org.jetbrains.compose.ui.tooling.preview.PreviewParameterProvider

/**
 * A versatile PreviewParameterProvider that can feed any sequence of values.
 *
 * Example usage:
 *   @Preview
 *   @Composable
 *   fun MyPreview(
 *       @PreviewParameter(GenericPreviewProvider::class) isActive: Boolean
 *   ) {
 *       MyComposable(isActive = isActive)
 *   }
 */
open class GenericPreviewProvider<T>(
    private val items: Sequence<T>
) : PreviewParameterProvider<T> {
    override val values: Sequence<T> = items
}
