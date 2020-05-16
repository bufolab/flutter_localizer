package action

import com.google.gson.GsonBuilder
import com.intellij.openapi.project.Project;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

object JSONModifier {


    private fun getfileContent(project: Project, index: Int): String? {

        try {
            val path = Settings.getPath(project, index)?.trim()
            val file = File(path)
            val readFileToString = FileUtils.readFileToString(file, "UTF-8")
            return readFileToString
        } catch (e: IOException) {
            //TODO show bubble
           e.printStackTrace()
            return null
        }
    }

    private fun saveFile(project: Project, data: String, index: Int) {
        val file = File(Settings.getPath(project, index))
        try {
            FileUtils.write(file, data, "UTF-8")
        } catch (ignored: IOException) {
        }
    }

    internal fun addTranslation(project: Project, key: String, value: String, index: Int) {

        val gson = GsonBuilder().setPrettyPrinting().create()
        val fileContent = getfileContent(project, index) ?: return

        var jsonMap:kotlin.collections.MutableMap<*,*> =
            gson.fromJson(fileContent, kotlin.collections.MutableMap::class.java)?:hashMapOf<Any,Any>().toMutableMap()

        val toMutableMap = jsonMap.toMutableMap()

        toMutableMap[key] = value

        val json = gson.toJson(toMutableMap)

        saveFile(project, json, index)
    }
}
