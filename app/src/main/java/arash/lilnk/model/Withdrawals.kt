package arash.lilnk.model

// Data class to hold individual withdrawal information
data class Withdrawal(
    val id: Int,
    val userId: Int,
    val iban: String,
    val amount: Int,
    val name: String,
    val surname: String,
    val requestTime: String,
    val status: Int
)

// Data class to hold overall withdrawals statistics
data class WithdrawalsStats(
    val userId: Int,
    val email: String,
    val registrationTime: String,
    val totalLinks: Int,
    val totalClicks: Int,
    val adsLinks: Int,
    val noAdsLinks: Int,
    val adsEarnings: Int,
    val currentBalance: Int,
    val withdrawals: List<Withdrawal>
)
