package io.votum.core.presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import votum.core.generated.resources.HankenGrotesk_Black
import votum.core.generated.resources.HankenGrotesk_BlackItalic
import votum.core.generated.resources.HankenGrotesk_Bold
import votum.core.generated.resources.HankenGrotesk_BoldItalic
import votum.core.generated.resources.HankenGrotesk_ExtraBold
import votum.core.generated.resources.HankenGrotesk_ExtraBoldItalic
import votum.core.generated.resources.HankenGrotesk_ExtraLight
import votum.core.generated.resources.HankenGrotesk_ExtraLightItalic
import votum.core.generated.resources.HankenGrotesk_Italic
import votum.core.generated.resources.HankenGrotesk_Light
import votum.core.generated.resources.HankenGrotesk_LightItalic
import votum.core.generated.resources.HankenGrotesk_Medium
import votum.core.generated.resources.HankenGrotesk_MediumItalic
import votum.core.generated.resources.HankenGrotesk_Regular
import votum.core.generated.resources.HankenGrotesk_SemiBold
import votum.core.generated.resources.HankenGrotesk_SemiBoldItalic
import votum.core.generated.resources.HankenGrotesk_Thin
import votum.core.generated.resources.HankenGrotesk_ThinItalic
import votum.core.generated.resources.Outfit_Black
import votum.core.generated.resources.Outfit_Bold
import votum.core.generated.resources.Outfit_ExtraBold
import votum.core.generated.resources.Outfit_ExtraLight
import votum.core.generated.resources.Outfit_Light
import votum.core.generated.resources.Outfit_Medium
import votum.core.generated.resources.Outfit_Regular
import votum.core.generated.resources.Outfit_SemiBold
import votum.core.generated.resources.Outfit_Thin
import votum.core.generated.resources.Res

@Composable
fun votumTypography(): Typography {
    val outfitFont = FontFamily(
        org.jetbrains.compose.resources.Font(Res.font.Outfit_Thin, FontWeight.Thin),
        org.jetbrains.compose.resources.Font(Res.font.Outfit_ExtraLight, FontWeight.ExtraLight),
        org.jetbrains.compose.resources.Font(Res.font.Outfit_Light, FontWeight.Light),
        org.jetbrains.compose.resources.Font(Res.font.Outfit_Regular, FontWeight.Normal),
        org.jetbrains.compose.resources.Font(Res.font.Outfit_Medium, FontWeight.Medium),
        org.jetbrains.compose.resources.Font(Res.font.Outfit_SemiBold, FontWeight.SemiBold),
        org.jetbrains.compose.resources.Font(Res.font.Outfit_Bold, FontWeight.Bold),
        org.jetbrains.compose.resources.Font(Res.font.Outfit_ExtraBold, FontWeight.ExtraBold),
        org.jetbrains.compose.resources.Font(Res.font.Outfit_Black, FontWeight.Black)
    )
    val hankenGroteskFamily = FontFamily(
        fonts = listOf(
            org.jetbrains.compose.resources.Font(
                Res.font.HankenGrotesk_Thin,
                FontWeight.Thin,
                FontStyle.Normal
            ),
            org.jetbrains.compose.resources.Font(
                Res.font.HankenGrotesk_ThinItalic,
                FontWeight.Thin,
                FontStyle.Italic
            ),
            org.jetbrains.compose.resources.Font(
                Res.font.HankenGrotesk_ExtraLight,
                FontWeight.ExtraLight,
                FontStyle.Normal
            ),
            org.jetbrains.compose.resources.Font(
                Res.font.HankenGrotesk_ExtraLightItalic,
                FontWeight.ExtraLight,
                FontStyle.Italic
            ),
            org.jetbrains.compose.resources.Font(
                Res.font.HankenGrotesk_Light,
                FontWeight.Light,
                FontStyle.Normal
            ),
            org.jetbrains.compose.resources.Font(
                Res.font.HankenGrotesk_LightItalic,
                FontWeight.Light,
                FontStyle.Italic
            ),
            org.jetbrains.compose.resources.Font(
                Res.font.HankenGrotesk_Regular,
                FontWeight.Normal,
                FontStyle.Normal
            ),
            org.jetbrains.compose.resources.Font(
                Res.font.HankenGrotesk_Italic,
                FontWeight.Normal,
                FontStyle.Italic
            ),
            org.jetbrains.compose.resources.Font(
                Res.font.HankenGrotesk_Medium,
                FontWeight.Medium,
                FontStyle.Normal
            ),
            org.jetbrains.compose.resources.Font(
                Res.font.HankenGrotesk_MediumItalic,
                FontWeight.Medium,
                FontStyle.Italic
            ),
            org.jetbrains.compose.resources.Font(
                Res.font.HankenGrotesk_SemiBold,
                FontWeight.SemiBold,
                FontStyle.Normal
            ),
            org.jetbrains.compose.resources.Font(
                Res.font.HankenGrotesk_SemiBoldItalic,
                FontWeight.SemiBold,
                FontStyle.Italic
            ),
            org.jetbrains.compose.resources.Font(
                Res.font.HankenGrotesk_Bold,
                FontWeight.Bold,
                FontStyle.Normal
            ),
            org.jetbrains.compose.resources.Font(
                Res.font.HankenGrotesk_BoldItalic,
                FontWeight.Bold,
                FontStyle.Italic
            ),
            org.jetbrains.compose.resources.Font(
                Res.font.HankenGrotesk_ExtraBold,
                FontWeight.ExtraBold,
                FontStyle.Normal
            ),
            org.jetbrains.compose.resources.Font(
                Res.font.HankenGrotesk_ExtraBoldItalic,
                FontWeight.ExtraBold,
                FontStyle.Italic
            ),
            org.jetbrains.compose.resources.Font(
                Res.font.HankenGrotesk_Black,
                FontWeight.Black,
                FontStyle.Normal
            ),
            org.jetbrains.compose.resources.Font(
                Res.font.HankenGrotesk_BlackItalic,
                FontWeight.Black,
                FontStyle.Italic
            )
        )
    )
    val baseline = Typography()
    return Typography(
        displayLarge = baseline.displayLarge.copy(fontFamily = outfitFont),
        displayMedium = baseline.displayMedium.copy(fontFamily = outfitFont),
        displaySmall = baseline.displaySmall.copy(fontFamily = outfitFont),
        headlineLarge = baseline.headlineLarge.copy(fontFamily = outfitFont),
        headlineMedium = baseline.headlineMedium.copy(fontFamily = outfitFont),
        headlineSmall = baseline.headlineSmall.copy(fontFamily = outfitFont),
        titleLarge = baseline.titleLarge.copy(fontFamily = outfitFont),
        titleMedium = baseline.titleMedium.copy(fontFamily = outfitFont),
        titleSmall = baseline.titleSmall.copy(fontFamily = outfitFont),
        bodyLarge = baseline.bodyLarge.copy(fontFamily = hankenGroteskFamily),
        bodyMedium = baseline.bodyMedium.copy(fontFamily = hankenGroteskFamily),
        bodySmall = baseline.bodySmall.copy(fontFamily = hankenGroteskFamily),
        labelLarge = baseline.labelLarge.copy(fontFamily = hankenGroteskFamily),
        labelMedium = baseline.labelMedium.copy(fontFamily = hankenGroteskFamily),
        labelSmall = baseline.labelSmall.copy(fontFamily = hankenGroteskFamily),
    )
}
