package action

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import java.awt.Dimension
import javax.swing.JTextField
import javax.swing.JLabel
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import javax.swing.BoxLayout
import javax.swing.JPanel
import com.intellij.openapi.ui.DialogBuilder
import com.intellij.openapi.actionSystem.CommonDataKeys
import java.awt.Component


class SetupExtractorAction: AnAction() {

    override fun actionPerformed(anActionEvent: AnActionEvent) {
        val project = anActionEvent.getData(CommonDataKeys.PROJECT)!!

        val path = Settings.getPath(project, 0)
//        val path2 = Settings.getPath(project, 1)

        val replacement = Settings.getReplacementString(project)

        val builder = DialogBuilder(project)

        val content = JPanel()
        content.layout = BoxLayout(content, BoxLayout.Y_AXIS)
        content.alignmentX = Component.LEFT_ALIGNMENT

        /// 1
        val selectFileLocationPath = JLabel()
        selectFileLocationPath.text = "Select the file location of the default json localization file"
        content.add(selectFileLocationPath)

        val jsonLocationTextField = TextFieldWithBrowseButton()
        jsonLocationTextField.setText(path)
        jsonLocationTextField.addBrowseFolderListener(
            "Choose json file",
            "Choose a localization file, it has to be in JSON format",
            project,
            FileChooserDescriptorFactory.createSingleFileDescriptor()
        )
        jsonLocationTextField.maximumSize = Dimension(
            java.lang.Short.MAX_VALUE.toInt(),
            56
        )
        content.add(jsonLocationTextField)


        /// Two
//        val selectFileLocationPath2 = JLabel()
//        selectFileLocationPath2.text = "Select the file location of another json localization file"
//        content.add(selectFileLocationPath2)
//
//        val jsonLocationTextField2 = TextFieldWithBrowseButton()
//        jsonLocationTextField2.setText(path2)
//        jsonLocationTextField2.addBrowseFolderListener(
//            "Choose json file",
//            "Choose a localization file, it has to be in JSON format",
//            project,
//            FileChooserDescriptorFactory.createSingleFileDescriptor()
//        )
//        jsonLocationTextField2.maximumSize = Dimension(
//            java.lang.Short.MAX_VALUE.toInt(),
//            56
//        )
//        content.add(jsonLocationTextField2)

        ///

        val replacementLabel = JLabel()
        replacementLabel.text =
            "The code that should replace the Text. \n '$$' will be replaced by the key in CamelCase"
        content.add(replacementLabel)

        val replacementTextField = JTextField()
        replacementTextField.text = replacement
        replacementTextField.maximumSize = Dimension(
            java.lang.Short.MAX_VALUE.toInt(),
            56
        )
        content.add(replacementTextField)

        builder.setDimensionServiceKey("GrepConsoleTailFileDialog")
        builder.setTitle("Choose your json path")
        builder.centerPanel(content)
        builder.removeAllActions()
        builder.addOkAction()
        builder.addCancelAction()

        builder.setOkOperation {
            val location = jsonLocationTextField.text
            Settings.savePath(project, location, 0)
//            Settings.savePath(project, jsonLocationTextField2.text, 1)

            val replacementText = replacementTextField.text
            Settings.saveReplacement(project, replacementText)

            builder.getDialogWrapper().close(1)
        }

        builder.show()
    }
}