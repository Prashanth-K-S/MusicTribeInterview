package com.arohaka.musictribegraph.activities

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.nfc.*
import android.nfc.tech.MifareClassic
import android.nfc.tech.MifareUltralight
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.arohaka.musictribegraph.R
import com.arohaka.musictribegraph.databinding.ActivityNfcactivityBinding


class NFCActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "NFCActivity"
    }

    private var mNfcAdapter: NfcAdapter? = null
    private lateinit var mBinding: ActivityNfcactivityBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_nfcactivity)

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_nfcactivity)
        mBinding.tbNfc.musicToolbar.title = "NFC"
        setSupportActionBar(mBinding.tbNfc.musicToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initNfcAdapter()
        if (mNfcAdapter == null) {
            Toast.makeText(this, "Device Does not support NFC", Toast.LENGTH_SHORT).show()
            finish()
        }


    }

    private fun initNfcAdapter() {
        val nfcManager = getSystemService(Context.NFC_SERVICE) as NfcManager
        mNfcAdapter = nfcManager.defaultAdapter
    }

    override fun onResume() {
        super.onResume()
        enableNfcForegroundDispatch()
    }

    private fun enableNfcForegroundDispatch() {
        try {
            val intent = Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            val nfcPendingIntent = PendingIntent.getActivity(this, 0, intent, 0)
            mNfcAdapter?.enableForegroundDispatch(this, nfcPendingIntent, null, null)
        } catch (ex: IllegalStateException) {
            Log.e(TAG, "Error enabling NFC foreground dispatch", ex)
        }
    }

    override fun onPause() {
        disableNfcForegroundDispatch()
        super.onPause()
    }

    private fun disableNfcForegroundDispatch() {
        try {
            mNfcAdapter?.disableForegroundDispatch(this)
        } catch (ex: IllegalStateException) {
            Log.e(TAG, "Error disabling NFC foreground dispatch", ex)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        Log.d("NFC Tag", "Detected")
        resolveIntent(intent!!)
    }


    private fun resolveIntent(intent: Intent) {
        val action = intent.action
        if (NfcAdapter.ACTION_TAG_DISCOVERED == action || NfcAdapter.ACTION_TECH_DISCOVERED == action || NfcAdapter.ACTION_NDEF_DISCOVERED == action) {
            val rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)
            val msgs: Array<NdefMessage?>
            if (rawMsgs != null) {
                msgs = arrayOfNulls(rawMsgs.size)
                for (i in rawMsgs.indices) {
                    msgs[i] = rawMsgs[i] as NdefMessage
                }
            } else {
                // Unknown tag type
                val empty = ByteArray(0)
                val id = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID)
                val tag = intent.getParcelableExtra<Parcelable>(NfcAdapter.EXTRA_TAG)
                val payload: ByteArray = dumpTagData(tag!!).toByteArray()
                val record = NdefRecord(NdefRecord.TNF_UNKNOWN, empty, id, payload)
                val msg = NdefMessage(arrayOf(record))
            }
        }
    }

    private fun dumpTagData(p: Parcelable): String {
        val sb = StringBuilder()
        val tag: Tag = p as Tag
        val id: ByteArray = tag.id
        sb.append("Tag ID (hex): ").append(getHex(id)).append("\n")
        val tagId = "${getHex(id)} "
        mBinding.tagId.text = tagId
        val prefix = "android.nfc.tech."
        sb.append("Technologies: ")
        val technologyBuilder = StringBuilder()
        for (tech in tag.techList) {
            sb.append(tech.substring(prefix.length))
            sb.append(", ")

            technologyBuilder.append(tech.substring(prefix.length))
            technologyBuilder.append(", ")
        }
        mBinding.technology.text = technologyBuilder.toString()
        sb.delete(sb.length - 2, sb.length)
        for (tech in tag.techList) {
            if (tech == MifareClassic::class.java.name) {
                sb.append('\n')
                val mifareTag = MifareClassic.get(tag)
                var type = "Unknown"
                when (mifareTag.type) {
                    MifareClassic.TYPE_CLASSIC -> type = "Classic"
                    MifareClassic.TYPE_PLUS -> type = "Plus"
                    MifareClassic.TYPE_PRO -> type = "Pro"
                }
                sb.append("Mifare Classic type: ")
                sb.append(type)
                sb.append('\n')
                sb.append("Mifare size: ")
                sb.append(mifareTag.size.toString() + " bytes")
                sb.append('\n')
                sb.append("Mifare sectors: ")
                sb.append(mifareTag.sectorCount)
                sb.append('\n')
                sb.append("Mifare blocks: ")
                sb.append(mifareTag.blockCount)
            }
            if (tech == MifareUltralight::class.java.name) {
                sb.append('\n')
                val mifareUlTag = MifareUltralight.get(tag)
                var type = "Unknown"
                when (mifareUlTag.type) {
                    MifareUltralight.TYPE_ULTRALIGHT -> type = "Ultralight"
                    MifareUltralight.TYPE_ULTRALIGHT_C -> type = "Ultralight C"
                }
                sb.append("Mifare Ultralight type: ")
                sb.append(type)
            }
        }
        Toast.makeText(this, "$sb", Toast.LENGTH_SHORT).show()
        return sb.toString()
    }

    private fun getHex(bytes: ByteArray): String {
        val sb = StringBuilder()
        for (i in bytes.indices.reversed()) {
            val b: Int = bytes[i] * 0xff
            if (b < 0x10) sb.append('0')
            sb.append(Integer.toHexString(b))
            if (i > 0) {
                sb.append(" ")
            }
        }
        return sb.toString()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home)
            finish()
        return true
    }

}