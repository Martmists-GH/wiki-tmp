# Instructions for converting .flex to lexer

1. Install the Grammar-Kit plugin for IntelliJ
2. (optional in case things break) Edit IntelliJ VM options and add `-Xss100m` 
3. Right-click the .flex file and click "Run JFlex Generator"
4. Copy the generated java file to this folder
5. Run a regex replace of `"\+\n\s+"` to an empty string on the generated java file
6. Open the generated java file, and run the "Convert Java File to Kotlin" action
7. Remove the generated constructor and the reader property
8. Add the following property: `override val state: Int get() = zzLexicalState`
9. Remove `java.util.` and `java.lang.` from all classes used.
10. Replace `Character` with `Compat` and replace `zzForAction@{ while ... }` to `zzForAction@while ...` 
11. Remove all @Throws annotations and the `init` block
12. Add the `override` modifer to `tokenStart`, `tokenEnd`, `reset()` and `advance()`
13. Add missing imports
14. Do remaining cleanup
