package com.github.nekitivlev.statisticplugin.services

import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectFileIndex
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.search.FilenameIndex
import com.intellij.psi.search.GlobalSearchScope
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.kotlin.psi.KtVisitorVoid

@Service(Service.Level.PROJECT)
class MyProjectService(private val project: Project) {
    fun getPsiInfo(): List<FileStats> {
        val stats = mutableListOf<FileStats>()

        getAllKotlinFiles(project).forEach { psiFile ->
            var classes = 0
            var functions = 0
            psiFile.acceptChildren(object : KtVisitorVoid() {
                override fun visitClass(ktClass: KtClass) {
                    classes++
                    ktClass.declarations.forEach { it.accept(this) }
                    super.visitClass(ktClass)
                }

                override fun visitNamedFunction(function: KtNamedFunction) {
                    functions++
                    function.bodyExpression?.acceptChildren(this)
                    super.visitNamedFunction(function)
                }

                override fun visitObjectDeclaration(declaration: KtObjectDeclaration) {
                    classes++
                    declaration.declarations.forEach { it.accept(this) }
                    super.visitObjectDeclaration(declaration)
                }

            })
            stats.add(FileStats(psiFile.name, classes, functions))
        }
        return stats
    }

    data class FileStats(val fileName: String, val classes: Int, val functions: Int)

    private fun getAllKotlinFiles(project: Project): List<PsiFile> {
        val psiManager = PsiManager.getInstance(project)
        val kotlinFiles = mutableListOf<PsiFile>()
        val fileIndex = ProjectFileIndex.getInstance(project)
        val kotlinFileNames =
            FilenameIndex.getAllFilesByExt(project, "kt", GlobalSearchScope.allScope(project))
        val sourceKotlinFiles = kotlinFileNames.filter { kotlinFile ->
            fileIndex.isInSourceContent(kotlinFile)
        }
        for (kotlinFile in sourceKotlinFiles) {
            val psiFile = psiManager.findFile(kotlinFile)
            if (psiFile != null) {
                kotlinFiles.add(psiFile)
            }
        }
        return kotlinFiles
    }

}
