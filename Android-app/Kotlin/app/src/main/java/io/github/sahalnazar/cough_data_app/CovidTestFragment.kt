package io.github.sahalnazar.cough_data_app

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import io.github.sahalnazar.cough_data_app.databinding.FragmentCovidTestBinding

class CovidTestFragment : Fragment() {

    lateinit var binding: FragmentCovidTestBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val binding = DataBindingUtil.inflate<FragmentCovidTestBinding>(inflater, R.layout.fragment_covid_test, container, false)

        val args = CovidTestFragmentArgs.fromBundle(requireArguments())
        Toast.makeText(context, args.bloodPressureReading.toString(), Toast.LENGTH_LONG).show()

        return binding.root
    }

}