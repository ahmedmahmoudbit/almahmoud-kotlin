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

/** QFC Surah name font - displays surah name using SurahGlyphs. */