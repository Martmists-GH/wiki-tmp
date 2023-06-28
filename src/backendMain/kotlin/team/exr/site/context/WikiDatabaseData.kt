package team.exr.site.context

class WikiDatabaseData(
    val groups: List<Group>,
    val pages: List<Page>,
    val images: List<Image>
) {
    class Group(val id: Int, val name: String)

    class Page(val id: Int, val name: String, val group: Int)

    class Image(val id: Int, val name: String)
}
