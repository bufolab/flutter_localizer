package action

import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.project.Project

object Settings {

    internal fun getPath(project: Project, index: Int): String? {
        return PropertiesComponent.getInstance(project).getValue("flutter_localizer_json_path_$index")
    }

    internal fun savePath(project: Project, path: String, index: Int) {
        PropertiesComponent.getInstance(project).setValue("flutter_localizer_json_path_$index", path)
    }

    internal fun getReplacementString(project: Project): String? {
        return PropertiesComponent.getInstance(project).getValue("flutter_localizer_replacement_string")
    }

    internal fun saveReplacement(project: Project, string: String) {
        PropertiesComponent.getInstance(project).setValue("flutter_localizer_replacement_string", string)
    }
}
