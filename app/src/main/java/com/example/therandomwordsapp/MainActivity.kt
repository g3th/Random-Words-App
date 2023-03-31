package com.example.therandomwordsapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.therandomwordsapp.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val wordOfTheDayUrl = "https://www.merriam-webster.com/word-of-the-day"
        val randomWordUrl = "https://randomword.com/"
        val dictionaryUrl = "https://www.thefreedictionary.com/"

        binding.infoBox.text = getString(R.string.infobox)

        suspend fun fetchWordOfTheDay() = withContext(Dispatchers.IO) {
            val rawHTML = Jsoup.connect(wordOfTheDayUrl).get()
            rawHTML.select("h2[class='word-header-txt']").text()
        }

        suspend fun fetchRandomWord() = withContext(Dispatchers.IO) {
            val rawWordHTML = Jsoup.connect(randomWordUrl).get()
            rawWordHTML.select("div[id='random_word']").text()
        }

        suspend fun fetchDefinition(word : String) = withContext(Dispatchers.IO){
            val rawHTML = Jsoup.connect(dictionaryUrl + word ).get()
            rawHTML.select("div[class='ds-single']").text()
        }

        fun scrapeDailyWord(state : Boolean): String {
            val scrape = runBlocking {
                if (!state) {
                    binding.wordButton.text = getString(R.string.wordButton)
                    fetchWordOfTheDay()
                }
                else {
                    fetchRandomWord()
                }
            }
            return scrape.toString()
        }

        fun getWordDefinition() : String {
            val definition = runBlocking {
                fetchDefinition(binding.wordOfTheDay.text.toString())
            }
            return definition.toString()
        }

        fun definitionResult() : String{
            return getWordDefinition()
        }
        var state = false
        binding.definitionButton.text = getString(R.string.definitionButton)

        binding.wordButton.setOnClickListener{
            binding.wordOfTheDay.text = scrapeDailyWord(state).replaceFirstChar {it.uppercase()}
            state = true
        }

        binding.definitionButton.setOnClickListener{

            if (definitionResult().isEmpty()){
              binding.definitionBox.text = getString(R.string.definitionBox)
            }
            else if (definitionResult().length > 30){
                val shorterDefinition = definitionResult().split(".")
                binding.definitionBox.text = shorterDefinition[0].replaceFirstChar { it.uppercase() } + "."
            }
            else {
                binding.definitionBox.text = definitionResult().replaceFirstChar {it.uppercase()}
            }
        }

    }
}