package com.sun.jsondemo

import android.app.Dialog
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.gson.Gson
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        CallApiAsyncTask().execute()
    }

    private inner class CallApiAsyncTask(): AsyncTask<Any, Void, String>(){

        private lateinit var progressDialog: Dialog

        override fun onPreExecute() {
            super.onPreExecute()
            showProgressDialog()

        }
        override fun doInBackground(vararg p0: Any?): String {
            var result: String
            var connection: HttpURLConnection? = null

            try {
                // prev url - val url = URL("https://run.mocky.io/v3/f5c9c34a-7be4-4d19-aa8c-c8f2a4dc5600")
                val url = URL("https://run.mocky.io/v3/97399f73-a80b-41cf-801e-f770c0eeeb4c")

                connection = url.openConnection() as HttpURLConnection
                connection.doInput = true
                connection.doOutput = true

                // RECEIVE DATA
                val httpResult : Int = connection.responseCode

                if (httpResult == HttpURLConnection.HTTP_OK){
                    val inputStream = connection.inputStream
                    val reader = BufferedReader(
                        InputStreamReader(inputStream)
                    )

                    val stringBuilder = StringBuilder()
                    var line :String?

                    try {
                        while (reader.readLine().also { line = it } != null){
                            stringBuilder.append(line + "\n")
                        }
                    }catch (e: IOException){
                        e.printStackTrace()
                    }finally {
                        try {
                            inputStream.close()
                        }catch (e: IOException){
                            e.printStackTrace()
                        }
                    }
                    result = stringBuilder.toString()
                }
                else {
                    result = connection.responseMessage
                }
            }catch (e: SocketTimeoutException){
                result = "connection timeout"
            }
            catch (e: Exception){
                result = "Error: " + e.message
            }
            finally {
                connection?.disconnect()
            }
            return result
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            dismissProgressDialog()

            if (result == null) return
            Log.i("JSON Response Result", result)

            // Fetch Json USING Gson library
            val responseData = Gson()
                .fromJson(result, ResponseData::class.java)

            Log.i("Message", responseData.name)

            Log.i("Linkedin Id", responseData.professional_id.linkedin_id)
            Log.i("Github Id", responseData.professional_id.github_id)
            Log.i("Twitter Id", responseData.professional_id.twitter_id)

            for (item in responseData.address.indices){
                Log.i("Item: $item", "${responseData.address[item]}")

                Log.i("City", responseData.address[item].city)
                Log.i("Pin", "${responseData.address[item].pin}")
            }

            // Fetch JSONObject USING JSONObject
            /*
            val jsonObject = JSONObject(result)
            val name = jsonObject.optString("name")
            Log.i("Name", name)

            // Fetch json object inside another json object i.e -> professional_id_object
            val professionalIdObject = jsonObject.optJSONObject("professional_id")
            val linkedinId = professionalIdObject?.optString("linkedin_id")
            Log.i("linkedin_id","$linkedinId")
            val githubId = professionalIdObject?.optString("github_id")
            Log.i("github_id","$githubId")
            val twitterId = professionalIdObject?.optString("twitter_id")
            Log.i("twitter_id","$twitterId")

            // fetch JSON array i.e. address
            val addressArray = jsonObject.optJSONArray("address")
            Log.i("addressArray's Length", "${addressArray?.length()}")

            for (item in 0 until addressArray!!.length()){
                Log.i("Value $item", "${addressArray[item]}")

                val addressItemObject = addressArray[item] as JSONObject
                val city = addressItemObject.optString("city")
                Log.i("city",city)
                val pin = addressItemObject.optInt("pin")
                Log.i("pin","$pin")
            }

             */

        }

        private fun showProgressDialog(){
            progressDialog = Dialog(this@MainActivity)
            progressDialog.setContentView(R.layout.progress_dialog)
            progressDialog.show()
        }

        private fun dismissProgressDialog(){
            progressDialog.dismiss()
        }

    }
}