package action

import com.google.common.base.CaseFormat
import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction
import com.intellij.ide.highlighter.HtmlFileType
import com.intellij.lang.PsiBuilderFactory
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogBuilder
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil
import com.jetbrains.lang.dart.psi.DartStringLiteralExpression
import com.jetbrains.lang.dart.psi.impl.DartStringLiteralExpressionBase
import com.jetbrains.lang.dart.psi.impl.DartStringLiteralExpressionImpl
import java.awt.Component
import java.awt.Dimension
import javax.swing.BoxLayout
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextField
import com.intellij.psi.AbstractFileViewProvider.findElementAt
import com.jetbrains.lang.dart.util.DartElementGenerator
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.impl.PsiElementBase


class ExtractStringIntentionAction : PsiElementBaseIntentionAction() {

    override fun startInWriteAction(): Boolean {
        return false;
    }

    override fun getFamilyName(): String {
        return "Localizers";
    }

    override fun isAvailable(project: Project, editor: Editor?, element: PsiElement): Boolean {
        return editor?.let {
            //TODO investigate why "is" is not working  and always returns false
            //return parent is DartStringLiteralExpressionImpl
            return element.parent::class.java.canonicalName == DartStringLiteralExpressionImpl::class.java.canonicalName
        } ?: false
    }

    override fun getText(): String {
        return "Localize String";
    }

    override fun invoke(project: Project, editor: Editor?, element: PsiElement) {
        val builder = DialogBuilder(project)

        val content = JPanel()
        content.layout = BoxLayout(content, BoxLayout.Y_AXIS)
        content.alignmentX = Component.LEFT_ALIGNMENT

        val keyLabel = JLabel()
        keyLabel.text = "Key"
        content.add(keyLabel)

        val keyString = element.text.toLowerCase().replace(" ","_").replace("\'","")
        val keyTextField = JTextField()
        content.add(keyTextField)
        keyTextField.maximumSize = Dimension(
            java.lang.Short.MAX_VALUE.toInt(),
            56
        )
        keyTextField.requestFocusInWindow()
        keyTextField.text = keyString

        val valueLabel = JLabel()
        valueLabel.text = "Value"
        content.add(valueLabel)

        val valueTextField = JTextField()
//        valueTextField.setText(text);
        content.add(valueTextField)
        valueTextField.maximumSize = Dimension(
            java.lang.Short.MAX_VALUE.toInt(),
            56
        )
        valueTextField.text = element.text

        builder.setDimensionServiceKey("GrepConsoleTailFileDialog")
        builder.setTitle("Localize \"$text\"");
        builder.centerPanel(content)
        builder.removeAllActions()
        builder.addOkAction()
        builder.addCancelAction()

        builder.setOkOperation {
            val camelCaseKey =    CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_UNDERSCORE,keyTextField.text.trim())
            JSONModifier.addTranslation(project, camelCaseKey, valueTextField.text, 0)
//            JSONModifier.addTranslation(project, keyTextField.text, valueTextField.text, 1)


            var toSave = "'$camelCaseKey'"

            val replacementString = Settings.getReplacementString(project)

            if(!replacementString.isNullOrBlank()) {
                 toSave = replacementString.replace("$$", toSave)
            }
            WriteCommandAction.runWriteCommandAction(project) {
                val psfile =  PsiFileFactory.getInstance(project).createFileFromText("dummy",HtmlFileType.INSTANCE,toSave)
                element.parent.replace(psfile)
            }

            builder.dialogWrapper.close(1)

        }


        builder.setPreferredFocusComponent(keyTextField)
        builder.show()
        builder.dispose()
    }

}