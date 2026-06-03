package com.almahmoudApp.al_mahmoudapp.feature.onboarding.presentation.components

import android.annotation.SuppressLint
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.almahmoudApp.al_mahmoudapp.R
import com.almahmoudApp.al_mahmoudapp.core.ui.components.AppButton
import com.almahmoudApp.al_mahmoudapp.feature.onboarding.domain.model.OnboardingPage
import com.almahmoudApp.al_mahmoudapp.feature.onboarding.domain.model.OnboardingPageKey

@SuppressLint("Range")
@Composable
fun OnboardingPageContent(
    page: OnboardingPage,
    pageCount: Int,
    selectedPage: Int,
    isLastPage: Boolean,
    onNextClick: () -> Unit,
    onCompleteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        Box(modifier = modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 30.dp, top = 86.dp, end = 28.dp)
                    .widthIn(max = 315.dp),
                horizontalAlignment = Alignment.Start,
            ) {
                Text(
                    text = stringResource(page.key.titleRes()),
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 34.sp,
                        lineHeight = 40.sp,
                    ),
                    color = MaterialTheme.colorScheme.primary,
                )
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = stringResource(page.key.bodyRes()),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 18.sp,
                        lineHeight = 27.sp,
                        color = Color.White
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(modifier = Modifier.height(26.dp))
                AppButton(
                    text = stringResource(
                        if (isLastPage) {
                            R.string.onboarding_start
                        } else {
                            R.string.onboarding_next
                        },
                    ),
                    onClick = if (isLastPage) onCompleteClick else onNextClick,
                )
                Spacer(modifier = Modifier.height(18.dp))
                OnboardingIndicator(
                    pageCount = pageCount,
                    selectedPage = selectedPage,
                )
            }
            Image(
                painter = painterResource(page.key.imageRes()),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .fillMaxWidth(1.52f)
                    .sizeIn(minHeight = 600.dp, maxHeight = 830.dp)
                    .offset(x = 150.dp, y = if (selectedPage == 0) 150.dp else 120.dp),
            )
        }
    }
}

@DrawableRes
private fun OnboardingPageKey.imageRes(): Int {
    return when (this) {
        OnboardingPageKey.AD_FREE -> R.drawable.onboarding_mosque_1
        OnboardingPageKey.DAILY_QURAN -> R.drawable.onboarding_mosque_2
        OnboardingPageKey.SIMPLE_ACCESS -> R.drawable.onboarding_mosque_3
    }
}

@StringRes
private fun OnboardingPageKey.titleRes(): Int {
    return when (this) {
        OnboardingPageKey.AD_FREE -> R.string.onboarding_title_ad_free
        OnboardingPageKey.DAILY_QURAN -> R.string.onboarding_title_daily_quran
        OnboardingPageKey.SIMPLE_ACCESS -> R.string.onboarding_title_simple_access
    }
}

@StringRes
private fun OnboardingPageKey.bodyRes(): Int {
    return when (this) {
        OnboardingPageKey.AD_FREE -> R.string.onboarding_body_ad_free
        OnboardingPageKey.DAILY_QURAN -> R.string.onboarding_body_daily_quran
        OnboardingPageKey.SIMPLE_ACCESS -> R.string.onboarding_body_simple_access
    }
}
