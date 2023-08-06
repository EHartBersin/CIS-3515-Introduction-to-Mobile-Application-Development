package edu.temple.imagedisplayapp

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider

class MainActivity : AppCompatActivity(), SelectionFragment.ImageSelectedInterface {
    /**
     * Companion objects are used in Kotlin
     * as containers of public static fields
     */
    companion object {
        val ITEM_KEY = "key"
    }

    lateinit var displayFragment: DisplayFragment

    private lateinit var imageViewModel: ImageViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        displayFragment = DisplayFragment()

        imageViewModel = ViewModelProvider(this)[ImageViewModel::class.java]

        supportFragmentManager
            .beginTransaction()
            .add(R.id.selectionContainer, SelectionFragment.newInstance(getImages()))
            .add(R.id.displayContainer, displayFragment)
            .commit()

    }

    /**
     * Feel free to change these resources to whatever you'd like
     */
    fun getImages(): Array<Item> {
        return arrayOf(
            Item(R.drawable.ccf_original, "Original"),
            Item(R.drawable.ccf_freshstrawberry, "Fresh Strawberry"),
            Item(R.drawable.ccf_chocolatecaramelicious, "Chocolate Caramelicious Cheesecake "),
            Item(R.drawable.ccf_pineappleupsidedown, "Pineapple Upside-Down"),
            Item(R.drawable.ccf_celebration, "Celebration"),
            Item(R.drawable.ccf_caramelapple, "Caramel Apple"),
            Item(
                R.drawable.ccf_verycherryghirardellichocolate,
                "Very Cherry Ghirardelli® Chocolate"
            ),
            Item(R.drawable.ccf_lowlicious, "Low-Licious"),
            Item(R.drawable.ccf_cinnaboncinnamoncwirl, "Cinnabon® Cinnamon Swirl"),
            Item(R.drawable.ccf_godiva, "Godiva® Chocolate"),
            Item(R.drawable.ccf_coconutcreampie, "Coconut Cream Pie"),
            Item(R.drawable.ccf_saltedcaramel, "Salted Caramel")
        )
    }

    override fun imageSelected(image: Int, name: String) {
        displayFragment.changeImage(image)
        displayFragment.changeText(name)
    }
}