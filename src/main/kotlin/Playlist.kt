import androidx.compose.runtime.MutableState
import com.google.gson.Gson
import com.google.gson.stream.JsonReader
import java.io.File
import java.io.FileReader

/*
Copyright (c) 2020 Kotlin Data Classes Generated from JSON powered by http://www.json2kotlin.com

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

For support, please feel free to contact me at https://www.linkedin.com/in/syedabsar */


data class Playlist (

	val playlistTitle : String,
	val playlistAuthor : String,
	val playlistDescription : String,
	val image : String,
	val songs : MutableList<Hash>
){
	fun writePlaylist(path: String, select: MutableMap<String, MutableState<Boolean>>){
		songs.clear();
		select.keys.forEach { if(select.get(it)!!.value) songs.add(Hash(it)) }
		//println(Gson().toJson(plist))
		File(path).writeText(Gson().toJson(this))
	}
}