package com.example.cars

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.core.text.isDigitsOnly
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import java.util.*
import androidx.lifecycle.Observer


private const val TAG = "CarFragment"
private const val ARG_CAR_ID = "car_id"

class CarFragment : Fragment() {

    var years = listOf("2010", "2011", "2012", "2013", "2014", "2015", "2016", "2017", "2018", "2019", "2020", "2021", "2022", "2023")
    var marks = listOf("Toyota", "Nissan", "Honda")
    var toyota = listOf("Corolla", "Corolla Alex", "Corolla Axio", "Corolla Runx", "Corolla Fielder")
    var nissan = listOf("Skyline", "Silvia", "Serena", "Sentra", "Sunny")
    var honda = listOf("Civic", "Fit", "Accord", "Inspire", "Odyssey")

    private lateinit var car : Car
    private lateinit var yearSpinner: Spinner
    private lateinit var markSpinner: Spinner
    private lateinit var modelSpinner: Spinner
    private lateinit var priceEditText: EditText
    private lateinit var markAdapter: ArrayAdapter<String>
    private lateinit var modelAdapter: ArrayAdapter<String>
    private lateinit var yearAdapter: ArrayAdapter<String>


    private val carDetailViewModel: CarDetailViewModel by lazy {
        ViewModelProviders.of(this).get(CarDetailViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        car = Car()
        val carId: UUID = arguments?.getSerializable(ARG_CAR_ID) as UUID
        carDetailViewModel.loadCar(carId)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_car, container, false)


        markSpinner = view.findViewById(R.id.car_mark)
        modelSpinner = view.findViewById(R.id.car_model)
        yearSpinner = view.findViewById(R.id.car_year)
        priceEditText = view.findViewById(R.id.car_price)

        markAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, marks)
        markAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        markSpinner.setAdapter(markAdapter);

        modelAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, toyota)
        modelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        modelSpinner.setAdapter(modelAdapter);

        yearAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, years)
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearSpinner.setAdapter(yearAdapter);

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        carDetailViewModel.carLiveData.observe(
            viewLifecycleOwner,
            Observer { car ->
                car?.let {
                    this.car = car
                    updateUI()
                }
            })
    }

    override fun onStart() {
        super.onStart()

        markSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                car.mark = markSpinner.selectedItem as String

                when(car.mark){
                    "Nissan" -> modelAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, nissan)
                    "Toyota" -> modelAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, toyota)
                    "Honda" ->  modelAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, honda)
                }

                modelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                modelSpinner.setAdapter(modelAdapter);
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        modelSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                car.model = modelSpinner.selectedItem as String
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        yearSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                car.year = yearSpinner.selectedItem.toString().toInt()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }


        priceEditText.doAfterTextChanged {
            car.price = if (it.isNullOrEmpty()) 0
            else it.toString().toInt()
        }

    }

    override fun onStop() {
        super.onStop()
        carDetailViewModel.saveCar(car)
    }

    private fun updateUI() {
        var position = markAdapter.getPosition(car.mark)
        markSpinner.setSelection(position);
        position = modelAdapter.getPosition(car.model)
        modelSpinner.setSelection(position);
        position = yearAdapter.getPosition(car.year.toString())
        yearSpinner.setSelection(position);
        priceEditText.setText(car.price.toString())

    }

    companion object {
        fun newInstance(autoId: UUID): CarFragment {
            val args = Bundle().apply {
                putSerializable(ARG_CAR_ID, autoId)
            }
            return CarFragment().apply {
                arguments = args
            }
        }
    }
}