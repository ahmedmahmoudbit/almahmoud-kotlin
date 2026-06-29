import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.almahmoudApp.al_mahmoudapp.R

val AmiriFont = FontFamily(
    Font(R.font.amiri_regular, weight = FontWeight.Normal),
    Font(R.font.amiri_bold,    weight = FontWeight.Bold),
)

/** Uthmani/QCF-style Quran font used for verse bodies and the ornamental end-of-ayah marker. */
val MeQuranFont = FontFamily(
    Font(R.font.me_quran, weight = FontWeight.Normal),
)

/** Uthmani Hafs script font - authentic Quran mushaf rendering. */
val UthmanicHafsFont = FontFamily(
    Font(R.font.uthmanic_hafs, weight = FontWeight.Normal),
)

/** Common Quran glyphs font (title frame, basmala, etc). */
val QuranCommonFont = FontFamily(
    Font(R.font.quran_common, weight = FontWeight.Normal),
)

/** Surah icon font - each surah has a decorative icon glyph. */
val SurahIconFont = FontFamily(
    Font(R.font.suracon, weight = FontWeight.Normal),
)

/** QFC Surah name font - displays surah name using SurahGlyphs. */