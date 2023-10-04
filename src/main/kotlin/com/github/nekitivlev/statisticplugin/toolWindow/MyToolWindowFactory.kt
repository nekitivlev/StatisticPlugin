package com.github.nekitivlev.statisticplugin.toolWindow

import com.github.nekitivlev.statisticplugin.services.MyProjectService
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.components.JBPanel
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.content.ContentFactory
import com.intellij.util.ui.JBUI
import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.JButton
import javax.swing.JTable
import javax.swing.table.DefaultTableModel

class MyToolWindowFactory : ToolWindowFactory {
    private val contentFactory = ContentFactory.getInstance()

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val myToolWindow = MyToolWindow(toolWindow)
        val content = contentFactory.createContent(myToolWindow.getContent(), null, false)
        toolWindow.contentManager.addContent(content)
    }

    override fun shouldBeAvailable(project: Project) = true

    inner class MyToolWindow(toolWindow: ToolWindow){
        private val tableModel: DefaultTableModel = DefaultTableModel()
        private val service = toolWindow.project.service<MyProjectService>()

        init {
            tableModel.addColumn("File Name")
            tableModel.addColumn("Classes")
            tableModel.addColumn("Functions")
        }
        private fun refreshData() {
            tableModel.rowCount = 0
            val psiInfo = service.getPsiInfo()
            psiInfo.forEach {
                tableModel.addRow(arrayOf(it.fileName, it.classes, it.functions))
            }
        }

        fun getContent() = JBPanel<JBPanel<*>>().apply {
            layout = BorderLayout()
            val table = JTable(tableModel)
            table.fillsViewportHeight = true
            val scrollPane = JBScrollPane(table)
            add(scrollPane, BorderLayout.CENTER)
            val refreshButton = JButton("Refresh").apply {
                maximumSize = Dimension(Int.MAX_VALUE, preferredSize.height)  // Make the button full width
                margin = JBUI.insets(5, 15)
                addActionListener {
                    refreshData()
                }
            }
            val buttonPanel = JBPanel<JBPanel<*>>().apply {
                layout = BorderLayout()
                add(refreshButton, BorderLayout.CENTER)
            }
            add(buttonPanel, BorderLayout.SOUTH)
            refreshData()
        }


    }

}