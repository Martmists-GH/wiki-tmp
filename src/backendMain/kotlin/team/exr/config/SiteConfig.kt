package team.exr.config

data class SiteConfig(
    val website: WebsiteConfig,
    val database: DatabaseConfig,
    val oauth: OAuthConfig,
)
