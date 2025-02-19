package com.example.pcwake

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

class MainActivity : AppCompatActivity() {
    private lateinit var btnWakePc : ImageView

    private var isComputerOn = false
    private val ipAddress = ""
    private val macAddress = ""

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                )

        btnWakePc = findViewById(R.id.btnWakePc)
        btnWakePc.setOnClickListener{
            sendWakeOnLanPacket(macAddress, ipAddress){
                isComputerOn = true;
            }
        }
    }

    private fun sendWakeOnLanPacket(macAddress: String, broadcastAddress: String, onSuccess: () -> Unit){
        Thread{
            try {

                val macBytes = macAddress.split(":").map{
                    it.toInt(16).toByte()
                }.toByteArray()

                val packetData = ByteArray(102)
                for (i in 1 .. 16){
                    packetData[i] = 0xFF.toByte()
                }
                for (i in 1..16){
                    System.arraycopy(macBytes, 0, packetData, i * 16, macBytes.size)
                }

                val broadcast = InetAddress.getByName(ipAddress)
                val packet = DatagramPacket(packetData, packetData.size, broadcast, 9)
                val socket = DatagramSocket()
                socket.send(packet)
                socket.close()

                runOnUiThread{
                    Toast.makeText(this, "Computador ligado com sucesso", Toast.LENGTH_SHORT).show()
                }
                onSuccess()
            }catch (e:Exception){

                runOnUiThread{
                    Toast.makeText(this, "Erro ao ligar o computador: ${e.message}", Toast.LENGTH_SHORT).show()
                }

            }
        }.start()
    }
}