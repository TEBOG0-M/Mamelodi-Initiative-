package com.mamelodiinitiative.navigation

object Routes {
    const val HOME = "home"
    const val PROGRAMMES = "programmes"
    const val PROGRAMME_DETAIL = "programme_detail/{programmeId}"
    const val VOLUNTEER = "volunteer"
    const val VOLUNTEER_FORM = "volunteer_form"
    const val VOLUNTEER_REVIEW = "volunteer_review"
    const val VOLUNTEER_SUCCESS = "volunteer_success"
    const val NEWS = "news"
    const val NEWS_DETAIL = "news_detail/{newsId}"
    const val MORE = "more"
    const val ABOUT = "about"
    const val DONATE = "donate"
    const val CONTACT = "contact"
    fun programmeDetail(id: Int) = "programme_detail/$id"
    fun newsDetail(id: Int) = "news_detail/$id"
}
