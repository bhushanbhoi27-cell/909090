package com.example.data.ai

import kotlinx.coroutines.delay

data class AiResult(
    val text: String,
    val isPlaceholder: Boolean = true,
    val notice: String = FIREBASE_REQUIRED_NOTICE
) {
    companion object {
        const val FIREBASE_REQUIRED_NOTICE =
            "Sample AI output — Enable Firebase to connect live Gemini AI via Firebase AI Logic."
    }
}

/**
 * Single AI gateway for JODIVA translation and Help Center assistant.
 * When Firebase is enabled, replace PlaceholderAiRepository with a FirebaseAiLogicRepository
 * backed by Firebase.ai.generativeModel("gemini-3.8-flash").
 */
interface AiRepository {
    suspend fun translateText(text: String, targetLanguage: String): AiResult
    suspend fun askHelpAssistant(userQuery: String, userHandle: String): AiResult
}

class PlaceholderAiRepository : AiRepository {
    override suspend fun translateText(text: String, targetLanguage: String): AiResult {
        delay(250)
        val trimmed = text.trim()
        val translatedPreview = when (targetLanguage) {
            "Hindi" -> "[हिन्दी अनुवाद नमूना] $trimmed — (वैश्विक समुदाय JODIVA पर साझा किया गया)"
            "Marathi" -> "[मराठी भाषांतर नमुना] $trimmed — (JODIVA जागतिक समुदायावर शेअर केले)"
            "Japanese" -> "[日本語翻訳サンプル] $trimmed — (JODIVAグローバルコミュニティより)"
            "Spanish" -> "[Traducción de muestra] $trimmed — (Compartido en la comunidad global JODIVA)"
            "French" -> "[Traduction exemple] $trimmed — (Partagé sur la communauté JODIVA)"
            "German" -> "[Beispielübersetzung] $trimmed — (Geteilt auf JODIVA Global)"
            "Portuguese" -> "[Tradução de exemplo] $trimmed — (Compartilhado na comunidade JODIVA)"
            "Arabic" -> "[نموذج ترجمة عربية] $trimmed — (مشاركة عبر مجتمع JODIVA العالمي)"
            "Chinese" -> "[中文翻译示例] $trimmed — (分享自 JODIVA 全球社区)"
            else -> "[Translated to $targetLanguage (Sample)] $trimmed"
        }
        return AiResult(
            text = translatedPreview,
            isPlaceholder = true,
            notice = AiResult.FIREBASE_REQUIRED_NOTICE
        )
    }

    override suspend fun askHelpAssistant(userQuery: String, userHandle: String): AiResult {
        delay(300)
        val q = userQuery.lowercase()
        val reply = when {
            "password" in q || "login" in q || "recover" in q || "hacked" in q ->
                "Sample Help Response for @$userHandle: To protect your account, open Profile > Settings > Security & Devices to toggle Two-Factor Authentication, inspect active login sessions, or use 'Logout From All Devices'. For hacked account recovery, submit a High-Priority ticket below."
            "private" in q || "privacy" in q || "block" in q ->
                "Sample Help Response: You can switch between Public and Private account modes anytime from Edit Profile. Private accounts hide posts, stories, and highlights from unapproved followers."
            "quiz" in q || "badge" in q || "rank" in q || "point" in q ->
                "Sample Help Response: Quiz rankings and Achievement Ticks are earned strictly through verified quiz performance and daily missions—never through paid subscriptions."
            "subscription" in q || "premium" in q || "payment" in q ->
                "Sample Help Response: JODIVA offers Free, Silver, Gold, and Premium plans for profile frames, analytics, and ad-free browsing. If you have a billing issue, create a support ticket below to receive a Ticket Number."
            else ->
                "Sample Help Response for @$userHandle: Thanks for contacting JODIVA Help Center! You can manage account privacy, report abusive content, or submit a tracked Complaint Ticket right here. Enable Firebase to activate live Gemini AI troubleshooting."
        }
        return AiResult(
            text = reply,
            isPlaceholder = true,
            notice = AiResult.FIREBASE_REQUIRED_NOTICE
        )
    }
}
