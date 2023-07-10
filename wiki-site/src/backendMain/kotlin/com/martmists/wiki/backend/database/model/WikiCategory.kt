package com.martmists.wiki.backend.database.model

import com.martmists.graphql.annotations.GraphQLModel
import com.martmists.wiki.backend.database.table.WikiCategoryTable
import com.martmists.wiki.backend.database.table.WikiPageTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

@GraphQLModel
class WikiCategory(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<WikiCategory>(WikiCategoryTable)

    var name by WikiCategoryTable.name
    var nameUrl by WikiCategoryTable.nameUrl
    var pages by WikiPage via WikiPageTable
}
