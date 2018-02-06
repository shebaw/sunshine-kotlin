package com.example.shebaw.implicitintent

import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ShareCompat
import android.view.View
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    private val REQUEST_IMAGE_CAPTURE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun onClickOpenWebpageButton(v: View) {
        val url = "https://udacity.com"
        openWebPage(url)
    }

    fun onClickOpenAddressButton(v: View) {
        val address = "1600 Amphitheatre Parkway, CA"
        val builder = Uri.Builder()
        builder.scheme("geo")
                .path("0,0")
                .query(address)
        showMap(builder.build())
    }

    fun onClickShareTextButton(v: View) {
        val textToShare = "Hello there"
        shareText(textToShare)
    }

    fun onCapturePhoto(v: View) {
        capturePhoto("asdf")
    }

    private fun openWebPage(url: String) {
        val uri = Uri.parse(url)
        with(Intent(Intent.ACTION_VIEW, uri)) {
            // see if there is an app that can handle our request before launching
            // our intent
            resolveActivity(packageManager).let {
                startActivity(this)
            }
        }
    }

    private fun showMap(uri: Uri) =
            with(Intent(Intent.ACTION_VIEW)) {
                data = uri
                resolveActivity(packageManager).let {
                    startActivity(this)
                }
            }

    private fun capturePhoto(saveLocation: String) =
            with(Intent(MediaStore.ACTION_IMAGE_CAPTURE)) {
                putExtra(MediaStore.EXTRA_OUTPUT, Uri.parse(saveLocation))
                resolveActivity(packageManager).let {
                    startActivityForResult(this, REQUEST_IMAGE_CAPTURE)
                }
            }

    private fun shareText(textToShare: String) {
        val mimeType = "text/plain"
        val title = "Learning How to Share"
        ShareCompat.IntentBuilder.from(this)
                .setChooserTitle(title)
                .setType(mimeType)
                .setText(textToShare)
                .startChooser()
    }
}
