package com.example.streakcard.data.model


package com.streakcard.data.model

enum class CardColorScheme {
    OCEAN, SUNSET, FOREST, AURORA, VOLCANIC, COSMIC, ROSE, MIDNIGHT, GOLDEN, CANDY
}

enum class CardShape {
    ROUNDED, STADIUM, DIAMOND, CIRCLE, SHARP
}

enum class CardSize {
    SMALL, MEDIUM, LARGE
}

enum class MissPolicy {
    RESET,      // streak resets to 0 if a day is missed
    CONTINUE    // streak continues from where it was
}
