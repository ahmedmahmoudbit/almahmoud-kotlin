package com.almahmoudApp.al_mahmoudapp.feature.quran.domain.usecase

import com.almahmoudApp.al_mahmoudapp.feature.quran.data.local.entity.MushafLineType
import com.almahmoudApp.al_mahmoudapp.feature.quran.data.repository.QuranDatabaseRepository
import com.almahmoudApp.al_mahmoudapp.feature.quran.domain.model.MushafLine
import com.almahmoudApp.al_mahmoudapp.feature.quran.domain.model.MushafPage
import com.almahmoudApp.al_mahmoudapp.feature.quran.domain.model.MushafWord
import com.almahmoudApp.al_mahmoudapp.feature.quran.util.QuranGlyphs
import javax.inject.Inject

class GetMushafPageUseCase @Inject constructor(
    private val repository: QuranDatabaseRepository,
) {
    suspend operator fun invoke(pageNumber: Int): Result<MushafPage> = runCatching {
        val lines = repository.getPageLines(pageNo = pageNumber)
        val surahNos = mutableSetOf<Int>()

        val mushafLines = lines.map { line ->
            when (line.lineType) {
                MushafLineType.surah_name -> {
                    line.surahNo?.let { surahNos.add(it) }
                    MushafLine(
                        lineNumber = line.lineNumber,
                        lineType = com.almahmoudApp.al_mahmoudapp.feature.quran.domain.model.MushafLineType.SURAH_NAME,
                        isCentered = line.isCentered,
                        words = emptyList(),
                        surahNo = line.surahNo,
                    )
                }
                MushafLineType.basmallah -> {
                    MushafLine(
                        lineNumber = line.lineNumber,
                        lineType = com.almahmoudApp.al_mahmoudapp.feature.quran.domain.model.MushafLineType.BASMALLAH,
                        isCentered = line.isCentered,
                        words = emptyList(),
                    )
                }
                MushafLineType.ayah -> {
                    val words = repository.resolveMushafLineWords(line)
                    words.forEach { word ->
                        val (chapterNo, _) = com.almahmoudApp.al_mahmoudapp.feature.quran.util.QuranConstants.getChapterAndVerseFromAyahId(word.ayahId)
                        surahNos.add(chapterNo)
                    }
                MushafLine(
                        lineNumber = line.lineNumber,
                        lineType = com.almahmoudApp.al_mahmoudapp.feature.quran.domain.model.MushafLineType.AYAH,
                        isCentered = line.isCentered,
                        words = words.map { word ->
                            val (_, verseNo) = com.almahmoudApp.al_mahmoudapp.feature.quran.util.QuranConstants.getChapterAndVerseFromAyahId(word.ayahId)
                            MushafWord(
                                ayahId = word.ayahId,
                                wordIndex = word.wordIndex,
                                text = QuranGlyphs.normalizeAyahNumber(word.text),
                                isLastWordOfAyah = word.isLastWordOfAyah,
                            )
                        },
                        surahNo = line.surahNo,
                    )
                }
            }
        }

        val surahNames = if (surahNos.isNotEmpty()) {
            repository.getChapterNames(surahNos.toList()).values.toList()
        } else {
            emptyList()
        }

        MushafPage(
            pageNumber = pageNumber,
            lines = mushafLines,
            surahNames = surahNames,
        )
    }
}
