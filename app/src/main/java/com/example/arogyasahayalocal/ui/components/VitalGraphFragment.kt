package com.example.arogyasahayalocal.ui.components

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.arogyasahayalocal.R
import com.example.arogyasahayalocal.data.entity.VitalLog
import com.example.arogyasahayalocal.viewmodels.VitalViewModel
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import java.util.ArrayList
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.data.Entry
import com.example.arogyasahayalocal.R.layout.fragment_vital_graph
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
class VitalGraphFragment : Fragment() {

    private val viewModel: VitalViewModel by viewModels()
    private lateinit var barChart: BarChart
    private lateinit var sugarLevelTextView: TextView
    private lateinit var vitalChart: LineChart
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_vital_graph, container, false)

         barChart = view.findViewById(R.id.barChart)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.latestVitals.observe(viewLifecycleOwner) { vitalList ->
            if (vitalList != null && vitalList.isNotEmpty()) {
                setupMultiBarChart(barChart, vitalList)
            }
        }
        setupGraph(view)
    }
    private fun setupGraph(view: View) {
        try {
            // Sample Data: Replace this later with data from your Repository / Database
            val entries = ArrayList<Entry>()
            entries.add(Entry(1f, 95f))   // (Day/Time 1, 95 mg/dL)
            entries.add(Entry(2f, 120f))  // (Day/Time 2, 120 mg/dL)
            entries.add(Entry(3f, 110f))  // (Day/Time 3, 110 mg/dL)
            entries.add(Entry(4f, 145f))  // (Day/Time 4, 145 mg/dL)

            val dataSet = LineDataSet(entries, "Blood Sugar Levels (mg/dL)")

            dataSet.color = resources.getColor(android.R.color.holo_blue_dark, null)
            dataSet.valueTextSize = 12f
            dataSet.lineWidth = 2.5f
            dataSet.setCircleColor(resources.getColor(android.R.color.holo_blue_light, null))
            dataSet.circleRadius = 5f

            val lineData = LineData(dataSet)
            vitalChart.data = lineData

             vitalChart.description.text = "Vitals History"
            vitalChart.animateX(1000) // Animates the graph drawing over 1 second

            vitalChart.invalidate()

            sugarLevelTextView.text = "Latest Sugar Level: 145 mg/dL"

        } catch (e: Exception) {
            Log.e("VitalGraphFragment", "Error drawing graph: ${e.message}")
        }
    }
    fun setupMultiBarChart(barChart: BarChart, vitalList: List<VitalLog>) {
        val entriesSystolic = ArrayList<BarEntry>()
        val entriesDiastolic = ArrayList<BarEntry>()
        val entriesSugar = ArrayList<BarEntry>()
        val dateLabels = ArrayList<String>()

        vitalList.forEachIndexed { index, vital ->
            entriesSystolic.add(BarEntry(index.toFloat(), vital.systolicBP.toFloat()))
            entriesDiastolic.add(BarEntry(index.toFloat(), vital.diastolicBP.toFloat()))
           // entriesSugar.add(BarEntry(index.toFloat(), vital.sugarLevel.toFloat()))

            val shortDate = try {
                val parser = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                val formatter = java.text.SimpleDateFormat("dd MMM", java.util.Locale.getDefault())
                parser.parse(vital.dateText)?.let { formatter.format(it) } ?: vital.dateText
            } catch (e: Exception) {
                e.printStackTrace()
                vital.dateText
            }
            dateLabels.add(shortDate)
        }

        val set1 = BarDataSet(entriesSystolic, "Systolic BP").apply { color = Color.RED; valueTextSize = 12f }
        val set2 = BarDataSet(entriesDiastolic, "Diastolic BP").apply { color = Color.BLUE; valueTextSize = 12f }
        val set3 = BarDataSet(entriesSugar, "Sugar Level").apply { color = Color.DKGRAY; valueTextSize = 12f }

        val data = BarData(set1, set2, set3)
        barChart.data = data

        val groupSpace = 0.08f
        val barSpace = 0.04f
        val barWidth = 0.26f
        barChart.barData.barWidth = barWidth

        val xAxis = barChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.valueFormatter = IndexAxisValueFormatter(dateLabels)
        xAxis.granularity = 1f
        xAxis.isGranularityEnabled = true
        xAxis.textSize = 14f
        xAxis.textColor = Color.BLACK
        xAxis.setAvoidFirstLastClipping(true)

        barChart.groupBars(0f, groupSpace, barSpace)
        barChart.setFitBars(true)

        xAxis.axisMinimum = 0f
        xAxis.axisMaximum = 0f + barChart.barData.getGroupWidth(groupSpace, barSpace) * vitalList.size
        xAxis.setCenterAxisLabels(true)

        val legend = barChart.legend
        legend.isEnabled = true
        legend.textSize = 14f
        legend.textColor = Color.BLACK
        legend.form = Legend.LegendForm.SQUARE
        legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        legend.orientation = Legend.LegendOrientation.HORIZONTAL
        legend.setDrawInside(false)
        legend.yOffset = 10f

        barChart.description.isEnabled = false
        barChart.invalidate()
    }
}