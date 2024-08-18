package arash.lilnk.model

// Data class to hold link information
data class Links(
    val shortUrl: String,
    val originalUrl: String,
    val createdAt: String,
    val accessCount: Int,
    val earnings: Int
)
