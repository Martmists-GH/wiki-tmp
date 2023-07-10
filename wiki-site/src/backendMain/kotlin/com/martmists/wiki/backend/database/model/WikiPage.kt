package com.martmists.wiki.backend.database.model

import com.martmists.graphql.annotations.GraphQLModel
import com.martmists.wiki.backend.database.table.WikiPageTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

@GraphQLModel
class WikiPage(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<WikiPage>(WikiPageTable)

    var title by WikiPageTable.title
    var titleUrl by WikiPageTable.titleUrl
    var content by WikiPageTable.content
    var published by WikiPageTable.published
    var category by WikiCategory optionalReferencedOn WikiPageTable.category
}
