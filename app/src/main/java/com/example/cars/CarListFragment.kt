package com.example.cars

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.*
import com.example.cars.*


private const val TAG = "CarListFragment"

class CarListFragment : Fragment() {

    interface Callbacks {
        fun onCarSelected(carId: UUID)
    }

    private lateinit var markAdapter: ArrayAdapter<String>
    private lateinit var modelAdapter: ArrayAdapter<String>
    private lateinit var markFilter: Spinner
    private lateinit var modelFilter: Spinner


    private var callbacks: Callbacks? = null
    private lateinit var carRecyclerView: RecyclerView
    private var adapter: CarAdapter? = CarAdapter(emptyList())

    private val carListViewModel: CarListViewModel by lazy {
        ViewModelProviders.of(this).get(CarListViewModel::class.java)
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks?
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_car_list, container, false)

        carRecyclerView =
            view.findViewById(R.id.car_recycler_view) as RecyclerView
        carRecyclerView.layoutManager = LinearLayoutManager(context)
        carRecyclerView.adapter = adapter

        val marks = listOf("Toyota", "Nissan", "Honda")
        val toyota = listOf("Corolla", "Corolla Alex", "Corolla Axio", "Corolla Runx", "Corolla Fielder")
        val nissan = listOf("Skyline", "Silvia", "Serena", "Sentra", "Sunny")
        val honda = listOf("Civic", "Fit", "Accord", "Inspire", "Odyssey")

        markFilter = view.findViewById(R.id.mark_filter)
        modelFilter = view.findViewById(R.id.model_filter)


        markAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, marks)
        markAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        markFilter.setAdapter(markAdapter)

        markFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {


                when(markFilter.selectedItem as String){
                    "Nissan" -> modelAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, nissan)
                    "Toyota" -> modelAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, toyota)
                    "Honda" ->  modelAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, honda)
                }

                modelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                modelFilter.setAdapter(modelAdapter);
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        carListViewModel.carListLiveData.observe(
            viewLifecycleOwner,
            Observer { cars ->
                cars?.let {
                    updateUI(cars)
                }
            })
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_car_list, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.new_car -> {
                val car = Car()
                carListViewModel.addCar(car)
                callbacks?.onCarSelected(car.id)
                true
            }
            R.id.price_sort ->{
                carListViewModel.carListLiveData.observe(
                    viewLifecycleOwner,
                    Observer { cars ->
                        cars?.let {
                            val sorted = cars.sortedBy { it.price }
                            updateUI(sorted)
                        }
                    })
                true
            }
            R.id.filter -> {
                carListViewModel.carListLiveData.observe(
                    viewLifecycleOwner,
                    Observer { cars ->
                        cars?.let {
                            val sorted = ArrayList<Car>()
                            cars.forEach {
                                val mark = markFilter.selectedItem.toString()
                                val model = modelFilter.selectedItem.toString()
                                if(it.model == model && it.mark == mark)
                                    sorted.add(it)
                            }
                            updateUI(sorted)
                        }
                    })
                true
            }
            else -> return super.onOptionsItemSelected(item)
        }

    }

    companion object {
        fun newInstance(): CarListFragment {
            return CarListFragment()
        }
    }

    private fun updateUI(cars: List<Car>) {

        adapter = CarAdapter(cars)
        carRecyclerView.adapter = adapter

    }

    private inner class AutoHolder(view: View)
        : RecyclerView.ViewHolder(view), View.OnClickListener {

        private lateinit var car: Car

        private val markTextView: TextView = itemView.findViewById(R.id.car_mark)
        private val modelTextView: TextView = itemView.findViewById(R.id.car_model)
        private val priceTextView: TextView = itemView.findViewById(R.id.car_price)
        private val yearTextView: TextView = itemView.findViewById(R.id.car_year)

        init {
            itemView.setOnClickListener(this)
        }

        @SuppressLint("SetTextI18n")
        fun bind(car: Car) {
            this.car = car
            markTextView.text = this.car.mark
            modelTextView.text = this.car.model
            priceTextView.text = "$${this.car.price}"
            yearTextView.text = this.car.year.toString()

        }

        override fun onClick(v: View) {
            callbacks?.onCarSelected(car.id)
        }
    }

    private inner class CarAdapter(var cars: List<Car>)
        : RecyclerView.Adapter<AutoHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AutoHolder {

            val view = layoutInflater.inflate(R.layout.list_item_car, parent, false)
            return AutoHolder(view)
        }

        override fun onBindViewHolder(holder: AutoHolder, position: Int) {
            val car = cars[position]
            holder.bind(car)
        }


        override fun getItemCount() = cars.size
    }

}