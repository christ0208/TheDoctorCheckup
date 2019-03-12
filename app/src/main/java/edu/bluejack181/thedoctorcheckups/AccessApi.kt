package edu.bluejack181.thedoctorcheckups

import Decoder.BASE64Encoder
import android.widget.Toast
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.http.HttpStatus
import org.apache.http.client.methods.HttpPost
import org.apache.http.impl.client.HttpClients
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

class AccessApi {
    private val URL: String = "https://sandbox-healthservice.priaid.ch"
    private val authUrl: String = "https://sandbox-authservice.priaid.ch/login"
    private var TOKEN: String = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJlbWFpbCI6InRkdW1teTczOUBnbWFpbC5jb20iLCJyb2xlIjoiVXNlciIsImh0dHA6Ly9zY2hlbWFzLnhtbHNvYXAub3JnL3dzLzIwMDUvMDUvaWRlbnRpdHkvY2xhaW1zL3NpZCI6IjQ2ODgiLCJodHRwOi8vc2NoZW1hcy5taWNyb3NvZnQuY29tL3dzLzIwMDgvMDYvaWRlbnRpdHkvY2xhaW1zL3ZlcnNpb24iOiIyMDAiLCJodHRwOi8vZXhhbXBsZS5vcmcvY2xhaW1zL2xpbWl0IjoiOTk5OTk5OTk5IiwiaHR0cDovL2V4YW1wbGUub3JnL2NsYWltcy9tZW1iZXJzaGlwIjoiUHJlbWl1bSIsImh0dHA6Ly9leGFtcGxlLm9yZy9jbGFpbXMvbGFuZ3VhZ2UiOiJlbi1nYiIsImh0dHA6Ly9zY2hlbWFzLm1pY3Jvc29mdC5jb20vd3MvMjAwOC8wNi9pZGVudGl0eS9jbGFpbXMvZXhwaXJhdGlvbiI6IjIwOTktMTItMzEiLCJodHRwOi8vZXhhbXBsZS5vcmcvY2xhaW1zL21lbWJlcnNoaXBzdGFydCI6IjIwMTktMDItMjYiLCJpc3MiOiJodHRwczovL3NhbmRib3gtYXV0aHNlcnZpY2UucHJpYWlkLmNoIiwiYXVkIjoiaHR0cHM6Ly9oZWFsdGhzZXJ2aWNlLnByaWFpZC5jaCIsImV4cCI6MTU1MjM5Mzg4OSwibmJmIjoxNTUyMzg2Njg5fQ.sf-INiw2pU-Dxok3ZsYtl3VOKkvfwctKQY1HSacL9oU"
    private val LANGUAGE: String = "en-gb"
    private val USERNAME: String = "tdummy739@gmail.com"
    private val PASSWORD: String = "Android123"
    private val ALGORITHM: String = "HmacMD5"

    inner class AccessToken{
        var Token: String = ""
        var ValidThrough: Int = 0
    }

    init{
//        generateToken()
    }

    public fun getUrl(): String{
        return URL
    }

    public fun getToken(): String{
        return TOKEN
    }

    public fun getLang(): String{
        return LANGUAGE
    }

    public fun generateToken(){
        val keySpec: SecretKeySpec = SecretKeySpec(PASSWORD.toByteArray(), ALGORITHM)
        var computedString: String = ""
        val mac: Mac = Mac.getInstance(ALGORITHM)
        mac.init(keySpec)
        var res: ByteArray = mac.doFinal(authUrl.toByteArray())

        val encoder: BASE64Encoder = BASE64Encoder()
        computedString = encoder.encode(res)

        val httpPost = HttpPost(authUrl)
        httpPost.setHeader("Authorization", "Bearer " + USERNAME + ":" + computedString)

        val httpClient = HttpClients.createDefault()
        val response = httpClient.execute(httpPost)

        val objectMapper = ObjectMapper()
        if(response.statusLine.statusCode != HttpStatus.SC_OK){
            System.out.println("Error when requesting token")
        }
        else{
            val accessToken = objectMapper.readValue(response.entity.content, AccessToken::class.java)
            TOKEN = accessToken.Token
        }
    }

}