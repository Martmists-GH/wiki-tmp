package team.exr.site.context

class Branding(
    val brand: String,
    val socials: List<Social>
) {
    class Social(val label: String, val url: String, val icon: String)
}
