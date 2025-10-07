package com.example.elasticsearch.config.constant

object Analyzer {
    const val STANDARD_ANALYZER = "standard"
    const val SIMPLE_ANALYZER = "simple"
    const val KOREAN_ANALYZER = "nori"
    const val WHITESPACE_ANALYZER = "whitespace"
    const val STOP_ANALYZER = "stop"
    const val KEYWORD_ANALYZER = "keyword"
    const val PATTERN_ANALYZER = "pattern"
    const val LANGUAGE_ANALYZER = "language"
    const val FINGERPRINT_ANALYZER = "fingerprint"

    object NORMALIZER {
        const val LOWERCASE_NORMALIZER = "lowercase_normalizer"
    }
}