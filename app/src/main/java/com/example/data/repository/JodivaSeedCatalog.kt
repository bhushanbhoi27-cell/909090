package com.example.data.repository

import com.example.data.model.CareerOpportunity
import com.example.data.model.ChatMessageEntity
import com.example.data.model.ChatThreadEntity
import com.example.data.model.DailyMissionEntity
import com.example.data.model.LearningCourse
import com.example.data.model.LiveStreamRoom
import com.example.data.model.NewsArticle
import com.example.data.model.NotificationEntity
import com.example.data.model.PostEntity
import com.example.data.model.QuizQuestion
import com.example.data.model.StoryEntity
import com.example.data.model.UserProfileEntity

object JodivaSeedCatalog {

    fun starterUsers(now: Long): List<UserProfileEntity> = listOf(
        UserProfileEntity(
            userId = "usr_aarav_in",
            jodivaId = "JDV-IN-104829",
            username = "aarav_mumbai",
            displayName = "Aarav Deshmukh",
            emailOrPhone = "aarav@jodiva.world",
            passwordHash = "seed_hash_1",
            bio = "Photographer & STEM Quiz Champion from Mumbai. Connecting cultures across Asia and Europe.",
            country = "India",
            stateProvince = "Maharashtra",
            city = "Mumbai",
            preferredLanguage = "Marathi",
            isPrivateAccount = false,
            followersCount = 1420,
            followingCount = 310,
            points = 1620,
            streakDays = 14,
            badgeTier = "Legend",
            isIdentityVerified = true,
            hasAchievementTick = true,
            subscriptionTier = "Gold",
            createdAt = now - 86_400_000L * 20
        ),
        UserProfileEntity(
            userId = "usr_hana_jp",
            jodivaId = "JDV-JP-229104",
            username = "hana_kyoto",
            displayName = "Hana Takahashi",
            emailOrPhone = "hana@jodiva.world",
            passwordHash = "seed_hash_2",
            bio = "Kyoto visual storyteller, tea culture curator, and language learner.",
            country = "Japan",
            stateProvince = "Kyoto Prefecture",
            city = "Kyoto",
            preferredLanguage = "Japanese",
            isPrivateAccount = false,
            followersCount = 980,
            followingCount = 245,
            points = 890,
            streakDays = 9,
            badgeTier = "Diamond",
            isIdentityVerified = true,
            hasAchievementTick = true,
            subscriptionTier = "Silver",
            createdAt = now - 86_400_000L * 15
        ),
        UserProfileEntity(
            userId = "usr_lukas_de",
            jodivaId = "JDV-DE-558312",
            username = "lukas_berlin",
            displayName = "Lukas Weber",
            emailOrPhone = "lukas@jodiva.world",
            passwordHash = "seed_hash_3",
            bio = "Indie sound designer & Berlin geography quiz rival. Private circle for close friends.",
            country = "Germany",
            stateProvince = "Berlin",
            city = "Berlin",
            preferredLanguage = "German",
            isPrivateAccount = true,
            followersCount = 412,
            followingCount = 180,
            points = 460,
            streakDays = 5,
            badgeTier = "Gold",
            isIdentityVerified = false,
            hasAchievementTick = true,
            subscriptionTier = "Free",
            createdAt = now - 86_400_000L * 12
        ),
        UserProfileEntity(
            userId = "usr_sofia_br",
            jodivaId = "JDV-BR-774920",
            username = "sofia_rio",
            displayName = "Sofia Oliveira",
            emailOrPhone = "sofia@jodiva.world",
            passwordHash = "seed_hash_4",
            bio = "Carnival percussionist, football trivia fan & multilingual community host.",
            country = "Brazil",
            stateProvince = "Rio de Janeiro",
            city = "Rio de Janeiro",
            preferredLanguage = "Portuguese",
            isPrivateAccount = false,
            followersCount = 2150,
            followingCount = 405,
            points = 1140,
            streakDays = 11,
            badgeTier = "Diamond",
            isIdentityVerified = true,
            hasAchievementTick = true,
            subscriptionTier = "Premium",
            createdAt = now - 86_400_000L * 10
        ),
        UserProfileEntity(
            userId = "usr_maya_us",
            jodivaId = "JDV-US-339184",
            username = "maya_austin",
            displayName = "Maya Chen",
            emailOrPhone = "maya@jodiva.world",
            passwordHash = "seed_hash_5",
            bio = "EdTech educator sharing free competitive exam prep & science reels.",
            country = "United States",
            stateProvince = "Texas",
            city = "Austin",
            preferredLanguage = "English",
            isPrivateAccount = false,
            followersCount = 760,
            followingCount = 290,
            points = 340,
            streakDays = 4,
            badgeTier = "Gold",
            isIdentityVerified = true,
            hasAchievementTick = false,
            subscriptionTier = "Free",
            createdAt = now - 86_400_000L * 7
        )
    )

    fun starterPosts(now: Long): List<PostEntity> = listOf(
        PostEntity(
            authorId = "usr_aarav_in",
            authorName = "Aarav Deshmukh",
            authorHandle = "aarav_mumbai",
            authorCountry = "India",
            authorBadgeTier = "Legend",
            authorHasAchievementTick = true,
            authorIsPrivate = false,
            postType = "PHOTO",
            caption = "Golden hour along Marine Drive never gets old. Connecting with creators from 12 countries tonight on JODIVA!",
            hashtags = "#Mumbai #MarineDrive #OneWorld #India",
            musicTrack = "Arabian Sea Breeze — Instrumental",
            drawableResName = "img_post_mumbai",
            likesCount = 342,
            commentsCount = 2,
            language = "English",
            createdAt = now - 3_600_000L * 2
        ),
        PostEntity(
            authorId = "usr_hana_jp",
            authorName = "Hana Takahashi",
            authorHandle = "hana_kyoto",
            authorCountry = "Japan",
            authorBadgeTier = "Diamond",
            authorHasAchievementTick = true,
            authorIsPrivate = false,
            postType = "PHOTO",
            caption = "Morning sunlight filtering through Kyoto's bamboo lantern trail. Wishing everyone a peaceful week across every timezone.",
            hashtags = "#Kyoto #Japan #MorningWalk #GlobalCommunity",
            musicTrack = "Bamboo Wind Chimes",
            drawableResName = "img_post_kyoto",
            likesCount = 518,
            commentsCount = 1,
            language = "English",
            createdAt = now - 3_600_000L * 5
        ),
        PostEntity(
            authorId = "usr_sofia_br",
            authorName = "Sofia Oliveira",
            authorHandle = "sofia_rio",
            authorCountry = "Brazil",
            authorBadgeTier = "Diamond",
            authorHasAchievementTick = true,
            authorIsPrivate = false,
            postType = "REEL",
            caption = "60-second acoustic samba groove from Rio! Tap the sound to remix or challenge our Brazil vs India quiz squad.",
            hashtags = "#RioDeJaneiro #MusicReel #Samba #JodivaReels",
            musicTrack = "Rio Sunset Acoustic (Licensed)",
            drawableResName = "img_jodiva_hero_globe",
            likesCount = 894,
            commentsCount = 1,
            language = "English",
            createdAt = now - 3_600_000L * 7
        ),
        PostEntity(
            authorId = "usr_maya_us",
            authorName = "Maya Chen",
            authorHandle = "maya_austin",
            authorCountry = "United States",
            authorBadgeTier = "Gold",
            authorHasAchievementTick = false,
            authorIsPrivate = false,
            postType = "REEL",
            caption = "3 memory tricks for today's 10-question Global Science & Geography challenge. Who is taking the #1 World Rank this week?",
            hashtags = "#StudyTips #QuizArena #STEM #Education",
            musicTrack = "Focus Pulse Lo-Fi",
            drawableResName = "img_quiz_arena",
            likesCount = 615,
            commentsCount = 0,
            language = "English",
            createdAt = now - 3_600_000L * 10
        ),
        PostEntity(
            authorId = "usr_lukas_de",
            authorName = "Lukas Weber",
            authorHandle = "lukas_berlin",
            authorCountry = "Germany",
            authorBadgeTier = "Gold",
            authorHasAchievementTick = true,
            authorIsPrivate = true,
            postType = "TEXT",
            caption = "Private circle update: Just finished mastering our Berlin ambient field recordings. Request to follow if you want early stems!",
            hashtags = "#Berlin #SoundDesign #PrivateCircle",
            musicTrack = "Midnight U-Bahn Synth",
            likesCount = 74,
            commentsCount = 0,
            language = "English",
            createdAt = now - 3_600_000L * 14
        )
    )

    fun starterStories(now: Long): List<StoryEntity> = listOf(
        StoryEntity(
            authorId = "usr_hana_jp",
            authorName = "Hana",
            authorCountry = "Japan",
            storyType = "PHOTO",
            textContent = "Early morning tea in Kyoto before the global quiz!",
            musicTitle = "Kyoto Mist Lo-Fi",
            drawableResName = "img_post_kyoto",
            viewersCount = 128,
            reactionCount = 34,
            isHighlight = true,
            createdAt = now - 3_600_000L * 3,
            expiresAt = now + 86_400_000L
        ),
        StoryEntity(
            authorId = "usr_aarav_in",
            authorName = "Aarav",
            authorCountry = "India",
            storyType = "PHOTO",
            textContent = "Sunset walk at Marine Drive, Mumbai.",
            musicTitle = "Ocean Drive Strings",
            drawableResName = "img_post_mumbai",
            viewersCount = 214,
            reactionCount = 59,
            isHighlight = true,
            createdAt = now - 3_600_000L * 4,
            expiresAt = now + 86_400_000L
        ),
        StoryEntity(
            authorId = "usr_sofia_br",
            authorName = "Sofia",
            authorCountry = "Brazil",
            storyType = "TEXT",
            textContent = "Joining the Brazil vs Japan Quiz Battle in 20 mins! Who's ready?",
            musicTitle = "Copacabana Beat",
            backgroundColorHex = 0xFFD95D39,
            viewersCount = 186,
            reactionCount = 47,
            isHighlight = false,
            createdAt = now - 3_600_000L * 2,
            expiresAt = now + 86_400_000L
        ),
        StoryEntity(
            authorId = "usr_maya_us",
            authorName = "Maya",
            authorCountry = "USA",
            storyType = "MUSIC",
            textContent = "Today's study playlist: Quantum Physics & Chill",
            musicTitle = "Orbital Study Synth",
            backgroundColorHex = 0xFF0A5C68,
            viewersCount = 95,
            reactionCount = 21,
            isHighlight = false,
            createdAt = now - 3_600_000L * 1,
            expiresAt = now + 86_400_000L
        )
    )

    fun starterChatThreads(now: Long): List<ChatThreadEntity> = listOf(
        ChatThreadEntity(
            threadId = "thr_aarav",
            title = "Aarav Deshmukh",
            subtitle = "Mumbai, India • JDV-IN-104829",
            isGroup = false,
            participantCountry = "India",
            isOnline = true,
            isEncrypted = true,
            lastMessagePreview = "Ready for tonight's 10-question Science quiz battle?",
            unreadCount = 1,
            updatedAt = now - 600_000L
        ),
        ChatThreadEntity(
            threadId = "thr_global_group",
            title = "Global Creators & Travelers",
            subtitle = "Group • India, Japan, Brazil, USA",
            isGroup = true,
            participantCountry = "Global",
            isOnline = true,
            isEncrypted = true,
            lastMessagePreview = "Hana: Sharing Kyoto golden hour shots soon!",
            unreadCount = 2,
            updatedAt = now - 1_800_000L
        ),
        ChatThreadEntity(
            threadId = "thr_maya",
            title = "Maya Chen",
            subtitle = "Austin, USA • JDV-US-339184",
            isGroup = false,
            participantCountry = "United States",
            isOnline = false,
            isEncrypted = true,
            lastMessagePreview = "Sent you the competitive exam study notes PDF summary.",
            unreadCount = 0,
            updatedAt = now - 7_200_000L
        )
    )

    fun starterChatMessages(now: Long): List<ChatMessageEntity> = listOf(
        ChatMessageEntity(
            threadId = "thr_aarav",
            senderId = "usr_aarav_in",
            senderName = "Aarav Deshmukh",
            text = "Namaste! Welcome to JODIVA. Loved your profile setup.",
            deliveryStatus = "READ",
            isFromMe = false,
            createdAt = now - 1_200_000L
        ),
        ChatMessageEntity(
            threadId = "thr_aarav",
            senderId = "usr_aarav_in",
            senderName = "Aarav Deshmukh",
            text = "Ready for tonight's 10-question Science quiz battle?",
            deliveryStatus = "READ",
            isFromMe = false,
            createdAt = now - 600_000L
        ),
        ChatMessageEntity(
            threadId = "thr_global_group",
            senderId = "usr_sofia_br",
            senderName = "Sofia Oliveira",
            text = "Olá everyone! Greetings from Rio de Janeiro!",
            deliveryStatus = "READ",
            isFromMe = false,
            createdAt = now - 3_600_000L
        ),
        ChatMessageEntity(
            threadId = "thr_global_group",
            senderId = "usr_hana_jp",
            senderName = "Hana Takahashi",
            text = "Sharing Kyoto golden hour shots soon!",
            deliveryStatus = "READ",
            isFromMe = false,
            createdAt = now - 1_800_000L
        ),
        ChatMessageEntity(
            threadId = "thr_maya",
            senderId = "usr_maya_us",
            senderName = "Maya Chen",
            text = "Sent you the competitive exam study notes summary.",
            messageType = "VOICE",
            voiceDurationSec = 18,
            deliveryStatus = "READ",
            isFromMe = false,
            createdAt = now - 7_200_000L
        )
    )

    fun starterMissions(): List<DailyMissionEntity> = listOf(
        DailyMissionEntity(
            id = "msn_daily_quiz",
            title = "Complete Today's 10-Question Quiz",
            description = "Finish a timed quiz in any category to earn verified points.",
            rewardPoints = 50,
            progress = 0,
            target = 1,
            isCompleted = false
        ),
        DailyMissionEntity(
            id = "msn_share_post",
            title = "Share With the Global Community",
            description = "Publish a post, story, or reel in your preferred language.",
            rewardPoints = 20,
            progress = 0,
            target = 1,
            isCompleted = false
        ),
        DailyMissionEntity(
            id = "msn_react_story",
            title = "Explore World Stories",
            description = "React to 2 stories from creators in other countries.",
            rewardPoints = 15,
            progress = 0,
            target = 2,
            isCompleted = false
        ),
        DailyMissionEntity(
            id = "msn_streak_7",
            title = "7-Day Learning & Community Streak",
            description = "Stay active across 7 consecutive days for a special streak bonus.",
            rewardPoints = 100,
            progress = 1,
            target = 7,
            isCompleted = false
        )
    )

    fun starterNotifications(now: Long): List<NotificationEntity> = listOf(
        NotificationEntity(
            title = "Global Live Stream Starting",
            body = "Aarav Deshmukh is hosting 'International Photography & Travel Q&A' live now.",
            category = "LIVE",
            isRead = false,
            createdAt = now - 900_000L
        ),
        NotificationEntity(
            title = "Daily Mission Available",
            body = "Complete today's 10-question quiz to earn +50 verified points toward your Silver Badge.",
            category = "MISSION",
            isRead = false,
            createdAt = now - 3_600_000L
        ),
        NotificationEntity(
            title = "Security Alert: New Sign-In",
            body = "Your JODIVA account logged in on this Android device. Review devices in Settings.",
            category = "SECURITY",
            isRead = true,
            createdAt = now - 7_200_000L
        )
    )

    val quizBank: List<QuizQuestion> = listOf(
        QuizQuestion(
            id = "q1",
            category = "General Knowledge",
            question = "Which planet in our solar system has the most confirmed moons?",
            options = listOf("Jupiter", "Saturn", "Uranus", "Neptune"),
            correctIndex = 1,
            explanation = "Saturn leads with well over 140 confirmed moons."
        ),
        QuizQuestion(
            id = "q2",
            category = "Geography",
            question = "Which river flows through the city of Kyoto and Osaka in Japan's Kansai region?",
            options = listOf("Shinano River", "Yodo River", "Tone River", "Ishikari River"),
            correctIndex = 1,
            explanation = "The Yodo River flows from Lake Biwa past Kyoto into Osaka Bay."
        ),
        QuizQuestion(
            id = "q3",
            category = "Science",
            question = "What is the chemical symbol for Tungsten?",
            options = listOf("Tu", "Tg", "W", "Tn"),
            correctIndex = 2,
            explanation = "W comes from Wolfram, the historical name for tungsten."
        ),
        QuizQuestion(
            id = "q4",
            category = "Mathematics",
            question = "What is the value of 15% of 240?",
            options = listOf("32", "36", "38", "42"),
            correctIndex = 1,
            explanation = "10% of 240 is 24 and 5% is 12; 24 + 12 = 36."
        ),
        QuizQuestion(
            id = "q5",
            category = "History",
            question = "In which year was the United Nations officially founded?",
            options = listOf("1919", "1945", "1950", "1961"),
            correctIndex = 1,
            explanation = "The UN Charter entered into force on October 24, 1945."
        ),
        QuizQuestion(
            id = "q6",
            category = "Current Affairs",
            question = "Which Indian state is home to the historic Ajanta and Ellora Caves?",
            options = listOf("Gujarat", "Maharashtra", "Karnataka", "Madhya Pradesh"),
            correctIndex = 1,
            explanation = "Both UNESCO World Heritage sites are located in Chhatrapati Sambhajinagar district, Maharashtra."
        ),
        QuizQuestion(
            id = "q7",
            category = "Science",
            question = "Which part of the human cell is primarily responsible for ATP energy production?",
            options = listOf("Golgi apparatus", "Ribosome", "Mitochondria", "Lysosome"),
            correctIndex = 2,
            explanation = "Mitochondria generate most of the cell's supply of ATP."
        ),
        QuizQuestion(
            id = "q8",
            category = "Sports",
            question = "How many players are on the field for one football (soccer) team during a standard match?",
            options = listOf("9", "10", "11", "12"),
            correctIndex = 2,
            explanation = "Each team fields 11 players including the goalkeeper."
        ),
        QuizQuestion(
            id = "q9",
            category = "Geography",
            question = "Which country spans the most time zones when including its overseas territories?",
            options = listOf("Russia", "United States", "France", "Canada"),
            correctIndex = 2,
            explanation = "France spans 12 time zones due to its overseas departments and territories."
        ),
        QuizQuestion(
            id = "q10",
            category = "Mathematics",
            question = "What is the smallest prime number greater than 20?",
            options = listOf("21", "23", "27", "29"),
            correctIndex = 1,
            explanation = "23 is divisible only by 1 and itself."
        )
    )

    val newsArticles: List<NewsArticle> = listOf(
        NewsArticle(
            id = "n1",
            category = "India News",
            country = "India",
            headline = "India Expands Digital Public Infrastructure Partnerships Across 15 Nations",
            summary = "New cross-border education and digital identity standards aim to help students and creators verify credentials seamlessly worldwide.",
            source = "Global Civic Wire",
            publishedAgo = "25m ago"
        ),
        NewsArticle(
            id = "n2",
            category = "Maharashtra News",
            country = "India",
            headline = "Pune & Mumbai Universities Launch Open Multilingual STEM Fellowship",
            summary = "Over 12,000 students in Maharashtra gain free access to bilingual Marathi-English competitive exam prep and coding labs.",
            source = "Deccan Chronicle Desk",
            publishedAgo = "1h ago"
        ),
        NewsArticle(
            id = "n3",
            category = "Technology News",
            country = "United States",
            headline = "Real-Time Neural Speech Translation Reaches Sub-Second Latency on Mobile",
            summary = "Breakthrough on-device and cloud translation models help international communities converse naturally across 40+ languages.",
            source = "Tech Horizon Daily",
            publishedAgo = "2h ago"
        ),
        NewsArticle(
            id = "n4",
            category = "World News",
            country = "Japan",
            headline = "Kyoto Introduces Sustainable Heritage Trails for Global Cultural Exchange",
            summary = "Digital city passes now connect local artisans with international travelers while preserving historic temple districts.",
            source = "Asia Pacific Observer",
            publishedAgo = "3h ago"
        ),
        NewsArticle(
            id = "n5",
            category = "Education News",
            country = "United Kingdom",
            headline = "Open Global Quiz Leagues Boost Student Retention in Geography & Science",
            summary = "A multi-country study shows timed daily 10-question challenges improve long-term recall by 34% among learners.",
            source = "EduWorld Journal",
            publishedAgo = "5h ago"
        ),
        NewsArticle(
            id = "n6",
            category = "Sports News",
            country = "Brazil",
            headline = "Youth International Community Cup Kicks Off in Rio With 24 Nations",
            summary = "Cultural festivals and friendly matches bring youth clubs from South America, Asia, and Europe together.",
            source = "World Sports Network",
            publishedAgo = "6h ago"
        )
    )

    val liveStreams: List<LiveStreamRoom> = listOf(
        LiveStreamRoom(
            id = "live_1",
            hostName = "Aarav Deshmukh",
            hostHandle = "aarav_mumbai",
            hostCountry = "India",
            title = "Golden Hour Marine Drive Walk & Creator Q&A",
            category = "Travel & Culture",
            viewerCount = 428,
            language = "English / Marathi",
            initialComments = listOf(
                "Hana (Japan)" to "The sunset reflection looks incredible!",
                "Sofia (Brazil)" to "Sending love from Rio!",
                "Maya (USA)" to "What camera lens are you using?"
            )
        ),
        LiveStreamRoom(
            id = "live_2",
            hostName = "Maya Chen",
            hostHandle = "maya_austin",
            hostCountry = "United States",
            title = "Live Competitive Exam Rapid-Fire Practice Session",
            category = "Education & Quiz",
            viewerCount = 312,
            language = "English",
            initialComments = listOf(
                "Aarav (India)" to "Ready for round 2!",
                "Lukas (Germany)" to "Great math shortcut on question 4."
            )
        )
    )

    val learningCourses: List<LearningCourse> = listOf(
        LearningCourse(
            id = "edu_1",
            title = "Competitive Exam Quantitative Aptitude Mastery",
            category = "Competitive Exams",
            language = "English / Hindi / Marathi",
            lessonsCount = 24,
            level = "Intermediate",
            summary = "Timed problem solving, percentages, ratios, and data interpretation."
        ),
        LearningCourse(
            id = "edu_2",
            title = "Conversational Japanese for Global Travelers",
            category = "Language Learning",
            language = "Japanese / English",
            lessonsCount = 16,
            level = "Beginner",
            summary = "Essential greetings, transit phrases, and cultural etiquette."
        ),
        LearningCourse(
            id = "edu_3",
            title = "Modern Science & Space Exploration Foundations",
            category = "STEM & Science",
            language = "English / Spanish",
            lessonsCount = 18,
            level = "All Levels",
            summary = "Interactive physics, astronomy, and climate science modules."
        )
    )

    val careerOpportunities: List<CareerOpportunity> = listOf(
        CareerOpportunity(
            id = "job_1",
            title = "Public Digital Services Fellow (2026 Cohort)",
            organization = "National Civic Tech Mission",
            sector = "Government",
            location = "Mumbai / New Delhi, India",
            deadline = "Dec 15, 2026",
            summary = "Official notification for engineering & policy graduates supporting citizen portals."
        ),
        CareerOpportunity(
            id = "job_2",
            title = "Multilingual Community Moderator & Localization Specialist",
            organization = "Global Horizon Media",
            sector = "International",
            location = "Remote (Worldwide)",
            deadline = "Rolling",
            summary = "Bilingual creators fluent in English + Hindi, Marathi, Japanese, or Portuguese."
        ),
        CareerOpportunity(
            id = "job_3",
            title = "Android Kotlin & Jetpack Compose Engineering Intern",
            organization = "NorthStar Labs",
            sector = "Internship",
            location = "Austin, USA / Hybrid",
            deadline = "Jan 10, 2027",
            summary = "Paid 12-week mentorship building accessible mobile interfaces."
        )
    )
}
