import com.google.gson.Gson
import com.google.gson.stream.JsonReader
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.io.FileReader

import androidx.compose.desktop.Window
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntSize

//notes: mapping should not change
var mapping: MutableMap<File, String>? = null

val playLists = mutableStateMapOf<File, Playlist>()
val selectedPlaylist = mutableStateOf(Playlist("","","","", mutableListOf()))


fun MutableState<Playlist>.equalsPlaylist(plist: Playlist?): Boolean{
    //println(this.value.playlistTitle)
    if(plist == null || this.value.playlistTitle == "")
        return false
    else return plist == this.value
}
//
enum class Screen(val value: Int){
    HOME(1), SONGS(2), PLAYLISTS(3), GENERATE(4)
}
fun readFileAsTextUsingInputStream(fileName: String)
        = File(fileName).inputStream().readBytes()

fun loadEverything(base: String, select: MutableMap<String, MutableState<Boolean>>){
    //C:\Program Files (x86)\Steam\steamapps\common\Beat Saber\Playlists
    mapping = generateSongHashMap("$base\\Beat Saber_Data\\CustomLevels\\")
    for( v in mapping?.values ?: mutableListOf()){
        select[v] = mutableStateOf(false)
    }

    //load playlists
    File("$base\\Playlists").walkTopDown().maxDepth(1).filter{it.name.endsWith(".json")}.forEach {
        //TODO: Check validity
        val plist: Playlist= Gson().fromJson(JsonReader(FileReader(it)), Playlist::class.java)
        playLists[it] = plist
    }


}

fun generateHash(id: File): String? {
    var hashBase = readFileAsTextUsingInputStream(id.absolutePath)
    val topic: infodat = Gson().fromJson(JsonReader(FileReader(id)), infodat::class.java)
    for(i in topic._difficultyBeatmapSets)
    {
        for(d in i._difficultyBeatmaps){
            //println(d._beatmapFilename)
            hashBase += readFileAsTextUsingInputStream(id.parent + "\\" + d._beatmapFilename)
        }
    }
    return HashUtils.SHAsum(hashBase)
}

fun generateSongHashMap(songFolder: String): MutableMap<File, String>{
    val mapping: MutableMap<File, String> = mutableMapOf()
    File(songFolder).walkTopDown().maxDepth(2).filter { it.name.toLowerCase() == "info.dat" }.forEach{
        val hash = generateHash(it)
        if(hash != null) mapping[it] = hash
    }
    return mapping
}

fun cacheSongHashMap(fileName: String, path:String) = File("$path\\$fileName").writeText(Json.encodeToString(mapping))

fun loadCachedSongHashMap(file: String){
    mapping = Json.decodeFromString<MutableMap<File, String>>(File(file).readText())
}

fun main() = Window(title = "B-List Beatlist", size = IntSize(800,600)){
    //TODO: Check if cache exists, if it doesn't generate new song hash
    val beatSaberFolder = mutableStateOf("C:\\Program Files (x86)\\Steam\\steamapps\\common\\Beat Saber")
    val folderSet = mutableStateOf(false)
    //Map to remember selected songs
    val selectedSongs by remember {mutableStateOf(mutableMapOf<String, MutableState<Boolean>>())}

    MaterialTheme {
        var currentScreen by remember { mutableStateOf(Screen.HOME) }
        Column {
            TopAppBar( title = {Text("B-List Beatlist")})
            Row {
                //side panel
                Column (Modifier.weight(1f)) {
                    Text("Home", modifier = Modifier.clickable {currentScreen = Screen.HOME }.fillMaxWidth(1f))
                    if(folderSet.value) {
                        Text(
                            "Playlists",
                            modifier = Modifier.clickable { currentScreen = Screen.PLAYLISTS }.fillMaxWidth(1f)
                        )
                        Text("Songs", modifier = Modifier.clickable { currentScreen = Screen.SONGS }.fillMaxWidth(1f))
                        Text(
                            "Generate",
                            modifier = Modifier.clickable { currentScreen = Screen.GENERATE }.fillMaxWidth(1f)
                        )
                    }
                }
                //loosely is content
                Column(Modifier.weight(4f)){
                    if(folderSet.value) {
                        if (mapping == null) loadEverything(beatSaberFolder.value, selectedSongs)
                        when (currentScreen) {
                            Screen.HOME -> homeScreen(beatSaberFolder, folderSet)
                            Screen.SONGS -> songScreen(selectedSongs)
                            Screen.PLAYLISTS -> playlistsScreen(selectedSongs)
                            Screen.GENERATE -> generateScreen(selectedSongs, beatSaberFolder)
                        }

                    }
                    else {
                        currentScreen = Screen.HOME
                        homeScreen(beatSaberFolder, folderSet)
                    }
                }

            }
        }
    }


}
@Composable
fun songEntry(song: File, hash: String, selection: MutableMap<String, MutableState<Boolean>>)
{
    val actualName = song.parentFile.name
    val myStateBoolean = selection[hash] ?: return
    Row{
        Checkbox(myStateBoolean.value, { myStateBoolean.value = it})
        Text(actualName, Modifier.weight(1f))
        Text(hash, Modifier.weight(1f))
    }
}

@Composable
fun playlistEntry(playlist: Playlist, selection: MutableMap<String, MutableState<Boolean>>)
{
    Row{
        Checkbox(selectedPlaylist.equalsPlaylist(playlist), {
            selectedPlaylist.value = playlist
            selection.forEach{it.value.value = false}
            for(h in playlist.songs) selection[h.hash]?.value = true})
        Text(playlist.playlistTitle)
    }
}
@Composable
fun homeScreen(field: MutableState<String>, folderCheck: MutableState<Boolean>) {
    Column{
        Text("Type in the path to the Beat Saber Folder (Folder where Beat Saber.exe is located)")
        Row{
            TextField(field.value, {field.value = it })
            Button(onClick = {folderCheck.value = File(field.value + "\\Beat Saber.exe").exists()}){Text("Check Path")}
        }
        Text("Steps:")
        Text("1) Validate your Beat Saber Install Path (click check path after typing in the path)")
        Text("2) Select Playlist")
        Text("3) Select Songs")
        Text("4) Go to Generate, fill out info, and click generate!")
        Text("5) Let me know if it worked! (Can't wait for the reports of null objects!)")
    }

}

@Composable
fun songScreen(selection: MutableMap<String, MutableState<Boolean>>){
    val nonMuteMap: Map<File, String> = mapping?.toMap() ?: mutableMapOf()
        Column{
        Text("Songs")
        LazyColumnFor(items = nonMuteMap.keys.toList(), itemContent = {
            songEntry(it, nonMuteMap[it] ?: "", selection)
        })
    }
}

@Composable
fun playlistsScreen(selection: MutableMap<String, MutableState<Boolean>>){
    Column{
        Text("Playlists")
        LazyColumnFor(items = playLists.values.toList(), itemContent = {
            playlistEntry(it, selection)
        })
    }
}

@Composable
fun generateScreen(selection: MutableMap<String, MutableState<Boolean>>, base: MutableState<String>){
    val visible = remember {mutableStateOf(false)}
    val alreadyExists = remember{mutableStateOf(false)}
    val fileNameField = remember{mutableStateOf("")}
    val playListAuthor = remember{mutableStateOf(selectedPlaylist.value.playlistAuthor)}
    val playListDescription = remember{mutableStateOf(selectedPlaylist.value.playlistDescription)}
    Column{
        Text("Generate")
        Text("File Name")
        Row{
            TextField(fileNameField.value, {fileNameField.value = it})
            Button(onClick = {visible.value = fileNameField.value.isNotBlank()
            alreadyExists.value = File(base.value + "\\Playlists\\" + fileNameField.value + ".json").exists()}){Text("Exists")}
        }
        Text("Playlist Author")
        TextField(playListAuthor.value, {playListAuthor.value = it})
        Text("Playlist Description")
        TextField(playListDescription.value, {playListDescription.value = it})
        if(alreadyExists.value)
        {
            Text("Note: File Already Exists!")
        }
        Row{
            Button(onClick = {
                val plist = Playlist(fileNameField.value, playListAuthor.value, playListDescription.value,"", mutableListOf())
                selection.keys.forEach {if(selection[it]?.value == true) plist.songs.add(Hash(it)) }
                File(base.value + "\\Playlists\\" + fileNameField.value + ".json").writeText(Gson().toJson(plist))
                //reload playlists and select the new playlist
                File(base.value + "\\Playlists").walkTopDown().maxDepth(1).filter{it.name.endsWith(".json")}.forEach {
                    //TODO: Check validity
                    val reloadList: Playlist= Gson().fromJson(JsonReader(FileReader(it)), Playlist::class.java)
                    playLists[it] = reloadList
                    if(it.name.contains(fileNameField.value)) selectedPlaylist.value = reloadList
                }
            }, enabled = visible.value){Text("Generate")}
        }

    }
}
