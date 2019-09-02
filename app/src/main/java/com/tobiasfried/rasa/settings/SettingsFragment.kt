package com.tobiasfried.rasa.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.tobiasfried.rasa.R


class SettingsFragment : Fragment() {

    private var rootView: View? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        rootView = inflater.inflate(R.layout.fragment_brews, container, false)
        return rootView
    }

    companion object {

        private val LOG_TAG = SettingsFragment::class.java.simpleName
    }

}
