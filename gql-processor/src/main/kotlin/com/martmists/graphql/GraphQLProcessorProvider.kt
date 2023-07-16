package com.martmists.graphql

import com.google.auto.service.AutoService
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider

class GraphQLProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): GraphQLProcessor {
        return GraphQLProcessor(environment.codeGenerator)
    }
}
