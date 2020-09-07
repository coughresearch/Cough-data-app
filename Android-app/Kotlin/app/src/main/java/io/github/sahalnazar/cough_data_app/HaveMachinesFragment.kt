package io.github.sahalnazar.cough_data_app

import android.os.Bundle
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import io.github.sahalnazar.cough_data_app.databinding.FragmentHaveMachinesBinding


class HaveMachinesFragment : Fragment() {

    private lateinit var binding: FragmentHaveMachinesBinding
    private var hasThermometer: Boolean = false
    private var hasDiabetesDevice: Boolean = false
    private var hasBloodPressureDevice: Boolean = false
    private var temperatureReading: String = ""
    private var diabetesReading: String = ""
    private var bloodPressureReading: String = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_have_machines, container, false)

        onMachineCheckboxClicked()

        binding.firstFloatBtn.setOnClickListener{view : View ->
            view.findNavController().navigate(HaveMachinesFragmentDirections.actionHaveMachinesFragmentToCovidTestFragment(
                    hasThermometer,
                    hasDiabetesDevice,
                    hasBloodPressureDevice,
                    temperatureReading,
                    diabetesReading,
                    bloodPressureReading))
        }


        return binding.root
    }

    private fun onMachineCheckboxClicked() {
        binding.thermometerCb.setOnClickListener { if (binding.thermometerCb.isChecked) {
            binding.thermometerEt.visibility = View.VISIBLE
            hasThermometer = true
            temperatureReading = binding.thermometerEt.text.toString()
        }else binding.thermometerEt.visibility = View.GONE
            hasThermometer = false
            temperatureReading = ""
        }

        binding.diabetesCb.setOnClickListener { if (binding.diabetesCb.isChecked) {
            binding.diabetesEt.visibility = View.VISIBLE
            hasDiabetesDevice = true
            diabetesReading = binding.diabetesEt.text.toString()
        }else binding.diabetesEt.visibility = View.GONE
            hasDiabetesDevice = false
            diabetesReading = ""
        }

        binding.bloodPressureCb.setOnClickListener { if (binding.bloodPressureCb.isChecked){
            binding.bloodPressureEt.visibility = View.VISIBLE
            hasBloodPressureDevice = true
            bloodPressureReading = binding.bloodPressureEt.text.toString()
        }else binding.bloodPressureEt.visibility = View.GONE
            hasBloodPressureDevice = false
            bloodPressureReading = ""
        }
    }
}