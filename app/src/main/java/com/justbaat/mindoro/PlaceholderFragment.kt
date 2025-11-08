package com.justbaat.mindoro

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

/**
 * A simple placeholder fragment. You can replace this with your actual
 * feature fragments as you build them.
 */
class PlaceholderFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_placeholder, container, false)

        // You can optionally find a TextView and set its text to show which screen this is
        val textView: TextView = view.findViewById(R.id.placeholder_text)
        textView.text = arguments?.getString("fragment_name") ?: "Placeholder"

        return view
    }
}