package com.github.nekitivlev.statisticplugin

import com.intellij.ide.highlighter.XmlFileType
import com.intellij.openapi.components.service
import com.intellij.psi.xml.XmlFile
import com.intellij.testFramework.TestDataPath
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.intellij.util.PsiErrorElementUtil
import com.github.nekitivlev.statisticplugin.services.MyProjectService
import org.jetbrains.kotlin.psi.KtFile

@TestDataPath("\$CONTENT_ROOT/src/test/testData")
class MyPluginTest : BasePlatformTestCase() {
    fun testGetPsiInfo() {
        myFixture.configureByFiles("testClassFunction.kt")
        val projectService = project.service<MyProjectService>()
        val psiInfo = projectService.getPsiInfo()

        assertEquals(1, psiInfo.size)
        assertEquals(2, psiInfo[0].classes)
        assertEquals(5, psiInfo[0].functions)
    }

    fun testSimpleTopLevelFunc() {
        myFixture.configureByFile("SimpleTopLevelFunc.kt")
        val projectService = project.service<MyProjectService>()
        val psiInfo = projectService.getPsiInfo()


        assertEquals(1, psiInfo.size)
        assertEquals(0, psiInfo[0].classes)
        assertEquals(1, psiInfo[0].functions)
    }

    fun testClassInFunc() {
        // Configure the Kotlin file for the test
        myFixture.configureByFile("ClassInFunc.kt")
        val projectService = project.service<MyProjectService>()
        val psiInfo = projectService.getPsiInfo()


        assertEquals(1, psiInfo.size)
        assertEquals(1, psiInfo[0].classes)
        assertEquals(1, psiInfo[0].functions)
    }

    fun testFuncInInnerClass() {
        myFixture.configureByFile("FuncInInnerClass.kt")
        val projectService = project.service<MyProjectService>()
        val psiInfo = projectService.getPsiInfo()


        assertEquals(1, psiInfo.size)
        assertEquals(2, psiInfo[0].classes)
        assertEquals(1, psiInfo[0].functions)
    }

    fun testComplexProjectStructure() {
        // Configure multiple files for the test
        myFixture.configureByFiles("MultiFileProject/main/Main.kt", "MultiFileProject/main/Utility.kt", "MultiFileProject/models/User.kt",
            "MultiFileProject/models/Product.kt", "MultiFileProject/services/UserService.kt", "MultiFileProject/services/ProductService.kt")
        val projectService = project.service<MyProjectService>()
        val psiInfo = projectService.getPsiInfo()


        assertEquals(6, psiInfo.size)
        assertEquals(4, psiInfo.sumOf { it.classes })
        assertEquals(7, psiInfo.sumOf { it.functions })
    }

    fun testNonKotlinFile() {
        // Configure the Java file for the test
        myFixture.configureByFile("SampleJavaFile.java")
        val projectService = project.service<MyProjectService>()
        val psiInfo = projectService.getPsiInfo()


        assertTrue(psiInfo.isEmpty())
    }

    override fun getTestDataPath() = "src/test/testData"
}
