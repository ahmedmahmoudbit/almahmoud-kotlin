package com.almahmoudApp.al_mahmoudapp.feature.quran.domain.usecase

import com.almahmoudApp.al_mahmoudapp.feature.quran.data.repository.QuranDatabaseRepository
import com.almahmoudApp.al_mahmoudapp.feature.quran.domain.model.JuzInfo
import com.almahmoudApp.al_mahmoudapp.feature.quran.domain.model.SurahVerseRange
import javax.inject.Inject

class GetJuzListUseCase @Inject constructor(
    private val repository: QuranDatabaseRepository,
) {
    suspend operator fun invoke(): Result<List<JuzInfo>> = runCatching {
        (1..30).map { juzNo ->
            val ranges = repository.getChapterVerseRangesInJuz(juzNo)
            val surahNames = repository.getChapterNames(ranges.map { it.first })
            JuzInfo(
                juzNumber = juzNo,
                surahRanges = ranges.map { (surahNo, verseRange) ->
                    SurahVerseRange(
                        surahNumber = surahNo,
                        surahName = surahNames[surahNo] ?: "",
                        startVerse = verseRange.first,
                        endVerse = verseRange.last,
                    )
                }
            )
        }
    }
}
