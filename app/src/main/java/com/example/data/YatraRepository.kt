package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class YatraRepository(private val yatraDao: YatraDao) {

    val progress: Flow<YatraProgress> = yatraDao.getYatraProgress().map {
        it ?: YatraProgress()
    }

    suspend fun updateRunResult(
        distanceMeters: Int,
        modaks: Int,
        flowers: Int,
        diyas: Int,
        score: Int
    ) {
        val current = yatraDao.getYatraProgress()
        // We read or update directly
    }

    suspend fun save(progress: YatraProgress) {
        yatraDao.saveYatraProgress(progress)
    }

    companion object {
        val CHAPTERS = listOf(
            StoryChapter(
                id = 1,
                title = "The Humble Giant",
                subtitle = "Mushika Becomes the Sacred Vahana",
                description = "Once an uncontrollable giant asura named Kroncha, he realized the divine truth upon encountering Lord Ganesha. Surrendering with pure humility, he requested to serve the Lord forever. Ganesha made him light as a feather yet strong as Mount Meru.",
                requiredDistanceMeters = 0,
                moralValue = "True strength begins when ego surrenders to devotion."
            ),
            StoryChapter(
                id = 2,
                title = "The Forest of Desires",
                subtitle = "Conquering Ego & Greed",
                description = "Mushika sprints through the mystical Naimisha forest. The thorns of Lobha (Greed) and heavy stone idols of Ahamkara (Ego) block the path. Only by lifting the Trishula and fixing his gaze on the golden Modak does he break through.",
                requiredDistanceMeters = 300,
                moralValue = "Focus on the divine purpose dissolves all worldly distractions."
            ),
            StoryChapter(
                id = 3,
                title = "Pradakshina of Wisdom",
                subtitle = "Circumambulating the Universe",
                description = "When Sage Narada brought the fruit of divine wisdom, Lord Shiva announced a contest: whoever circles the three worlds fastest wins. While Kartikeya sped across galaxies on his peacock, Ganesha lovingly circled Shiva and Parvati, uttering: 'My parents are my entire universe.'",
                requiredDistanceMeters = 800,
                moralValue = "Wisdom sees depth and gratitude where others see only distance."
            ),
            StoryChapter(
                id = 4,
                title = "The Unbroken Scribe",
                subtitle = "Sacrificing the Tusk for Knowledge",
                description = "Sage Vyasa required a scribe who could write the Mahabharata without pause. Ganesha accepted with joy. When the quill shattered under Vyasa's thunderous dictation, Ganesha broke his own ivory tusk to continue without dropping a single syllable.",
                requiredDistanceMeters = 1500,
                moralValue = "Unwavering commitment turns any sacrifice into eternal truth."
            ),
            StoryChapter(
                id = 5,
                title = "Ascent to Kailash",
                subtitle = "Transcendence of the Five Vices",
                description = "High on the snowy ridges of the Himalayas, the illusions of Anger (Krodha), Fear (Bhaya), and Confusion (Moha) manifest. Mushika pumps iron, flexes his devotion, and lights the sacred diyas to warm the path ahead.",
                requiredDistanceMeters = 2500,
                moralValue = "Spiritual discipline and physical vigor empower one another."
            ),
            StoryChapter(
                id = 6,
                title = "The Divine Darshan",
                subtitle = "Meeting Lord Ganesha",
                description = "At the golden sanctum of Kailash, bells ring across the cosmos. Lord Ganesha smiles warmly, lifting Mushika onto his lotus seat and bestowing eternal blessings upon the brave, muscular mouse vahana.",
                requiredDistanceMeters = 4000,
                moralValue = "Devotion always reaches its divine destination."
            )
        )

        val BLESSINGS = listOf(
            Blessing(
                id = 1,
                title = "Mayureshwar",
                temple = "Morgaon",
                boonEffect = "Devotional Vigor (+15% Speed Control)",
                description = "The first of the Ashtavinayaka, granting initial courage and inner strength on the spiritual path.",
                requiredDistance = 100
            ),
            Blessing(
                id = 2,
                title = "Siddhivinayak",
                temple = "Siddhatek",
                boonEffect = "Modak Multiplier (2x Prasad Points)",
                description = "Lord of Siddhi (spiritual powers), fulfilling noble wishes and multiplying sacred prasad.",
                requiredDistance = 400
            ),
            Blessing(
                id = 3,
                title = "Ballaleshwar",
                temple = "Pali",
                boonEffect = "Unshakeable Shield (+3s Trishula duration)",
                description = "Named after child devotee Ballal, bestowing unwavering protection to sincere seekers.",
                requiredDistance = 900
            ),
            Blessing(
                id = 4,
                title = "Varadavinayak",
                temple = "Mahad",
                boonEffect = "Prasad Magnetism (Collect items from all lanes)",
                description = "Giver of all boons, drawing auspicious blessings and sacred offerings effortlessly.",
                requiredDistance = 1600
            ),
            Blessing(
                id = 5,
                title = "Chintamani",
                temple = "Theur",
                boonEffect = "Cosmic Clarity (Calmer rhythm timing)",
                description = "Remover of worries and restlessness, bringing meditative stillness to the scribe challenge.",
                requiredDistance = 2400
            ),
            Blessing(
                id = 6,
                title = "Girijatmaj",
                temple = "Lenyadri",
                boonEffect = "Planetary Harmony (Slower orbital decay)",
                description = "Born in the mountain caves of Lenya, aligning the seeker with the cosmic rhythms of Shiva-Shakti.",
                requiredDistance = 3200
            ),
            Blessing(
                id = 7,
                title = "Vighnahar",
                temple = "Ozar",
                boonEffect = "Obstacle Demolisher (Power punch through 1 vice barrier)",
                description = "The supreme vanquisher of impediments, shattering ego and anger with divine mace power.",
                requiredDistance = 4000
            ),
            Blessing(
                id = 8,
                title = "Mahaganapati",
                temple = "Ranjangaon",
                boonEffect = "Eternal Grace (Supreme Kailash Sanctum Entry)",
                description = "The ten-armed cosmic manifestation who blessed Shiva before defeating the demon Tripurasura.",
                requiredDistance = 5000
            )
        )
    }
}
