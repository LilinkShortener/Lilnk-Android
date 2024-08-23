package arash.lilnk.model

data class Notes(
    val id: Int,
    val userId: Int,
    val title: String?,
    val content: String,
    val shortUrl: String,
    val createdAt: String,
    val updatedAt: String?,
    val accessCount: Int,
    val lastAccessed: String?,
)
