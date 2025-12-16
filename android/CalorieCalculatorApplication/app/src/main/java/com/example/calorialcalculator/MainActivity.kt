package com.example.calorialcalculator

import android.app.AlertDialog
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.calorialcalculator.Adapters.AddedFoodsAdapter
import com.example.calorialcalculator.Adapters.SavedRationsAdapter
import com.example.calorialcalculator.Adapters.SearchResultsAdapter
import com.example.calorialcalculator.Backend.api.*
import com.example.calorialcalculator.Backend.api.RationCommands.*
import com.google.android.material.appbar.MaterialToolbar

class MainActivity : AppCompatActivity() {

    private var userId = -1

    private lateinit var toolbar: MaterialToolbar
    private lateinit var tvUserName: TextView
    private lateinit var tvUserStats: TextView

    private lateinit var etSearch: EditText
    private lateinit var searchOverlay: View
    private lateinit var rvSearchResults: RecyclerView
    private lateinit var rvAddedFoods: RecyclerView
    private lateinit var rvSavedRations: RecyclerView
    private lateinit var btnSaveRation: Button

    private lateinit var rgMode: RadioGroup
    private lateinit var rbCut: RadioButton
    private lateinit var rbBulk: RadioButton

    private lateinit var tvCaloriesLabel: TextView
    private lateinit var tvProteinLabel: TextView
    private lateinit var tvFatLabel: TextView
    private lateinit var tvCarbsLabel: TextView

    private lateinit var pbCalories: ProgressBar
    private lateinit var pbProtein: ProgressBar
    private lateinit var pbFat: ProgressBar
    private lateinit var pbCarbs: ProgressBar

    private val ninjasApi = CalorieNinjasApi()

    private val addedItems = mutableListOf<AddedFoodUi>()

    private lateinit var searchAdapter: SearchResultsAdapter
    private lateinit var addedAdapter: AddedFoodsAdapter
    private lateinit var savedAdapter: SavedRationsAdapter

    // targets
    private var targetCalories = 2200
    private var targetProtein = 160
    private var targetFat = 60
    private var targetCarbs = 200

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        userId = intent.getIntExtra("userId", -1)
        if (userId == -1) {
            finish()
            return
        }

        bindViews()
        setupToolbar()
        setupLists()
        setupMode()
        setupSearch()
        setupBack()

        loadProfile()
        loadSavedRations()
        updateProgress()

        btnSaveRation.setOnClickListener { saveCurrentRation() }
    }

    // ---------- bind ----------

    private fun bindViews() {
        toolbar = findViewById(R.id.toolbar)
        tvUserName = findViewById(R.id.tvUserName)
        tvUserStats = findViewById(R.id.tvUserStats)

        etSearch = findViewById(R.id.etSearch)
        searchOverlay = findViewById(R.id.searchOverlay)
        rvSearchResults = findViewById(R.id.rvSearchResults)
        rvAddedFoods = findViewById(R.id.rvAddedFoods)
        rvSavedRations = findViewById(R.id.rvSavedRations)
        btnSaveRation = findViewById(R.id.btnSaveRation)

        rgMode = findViewById(R.id.rgMode)
        rbCut = findViewById(R.id.rbCut)
        rbBulk = findViewById(R.id.rbBulk)

        tvCaloriesLabel = findViewById(R.id.tvCaloriesLabel)
        tvProteinLabel = findViewById(R.id.tvProteinLabel)
        tvFatLabel = findViewById(R.id.tvFatLabel)
        tvCarbsLabel = findViewById(R.id.tvCarbsLabel)

        pbCalories = findViewById(R.id.pbCalories)
        pbProtein = findViewById(R.id.pbProtein)
        pbFat = findViewById(R.id.pbFat)
        pbCarbs = findViewById(R.id.pbCarbs)
    }

    // ---------- toolbar ----------

    private fun setupToolbar() {
        toolbar.title = "Calories Calculator"
    }

    // ---------- lists ----------

    private fun setupLists() {

        searchAdapter = SearchResultsAdapter { food ->
            askGramsAndAdd(food)
        }
        rvSearchResults.layoutManager = LinearLayoutManager(this)
        rvSearchResults.adapter = searchAdapter

        addedAdapter = AddedFoodsAdapter(
            items = addedItems,
            onRemove = { index ->
                addedItems.removeAt(index)
                addedAdapter.notifyDataSetChanged()
                updateProgress()
            }
        )
        rvAddedFoods.layoutManager = LinearLayoutManager(this)
        rvAddedFoods.adapter = addedAdapter

        savedAdapter = SavedRationsAdapter(
            onClick = { openSavedRation(it.id) },
            onDelete = { deleteRation(it) }
        )
        rvSavedRations.layoutManager = LinearLayoutManager(this)
        rvSavedRations.adapter = savedAdapter
    }

    // ---------- mode ----------

    private fun setupMode() {
        rbCut.isChecked = true
        applyMode(true)

        rgMode.setOnCheckedChangeListener { _, id ->
            applyMode(id == R.id.rbCut)
            updateProgress()
        }
    }

    private fun applyMode(isCut: Boolean) {
        if (isCut) {
            targetCalories = 2200
            targetProtein = 160
            targetFat = 60
            targetCarbs = 200
        } else {
            targetCalories = 2800
            targetProtein = 170
            targetFat = 80
            targetCarbs = 320
        }

        pbCalories.max = targetCalories
        pbProtein.max = targetProtein
        pbFat.max = targetFat
        pbCarbs.max = targetCarbs
    }

    // ---------- progress ----------

    private fun updateProgress() {
        val c = addedItems.sumOf { it.calories }
        val p = addedItems.sumOf { it.protein }
        val f = addedItems.sumOf { it.fat }
        val carb = addedItems.sumOf { it.carbs }

        tvCaloriesLabel.text = "Calories: $c / $targetCalories"
        tvProteinLabel.text = "Protein: $p / $targetProtein g"
        tvFatLabel.text = "Fat: $f / $targetFat g"
        tvCarbsLabel.text = "Carbs: $carb / $targetCarbs g"

        pbCalories.progress = c.coerceAtMost(targetCalories)
        pbProtein.progress = p.coerceAtMost(targetProtein)
        pbFat.progress = f.coerceAtMost(targetFat)
        pbCarbs.progress = carb.coerceAtMost(targetCarbs)

        tintProgress(pbCalories, c, targetCalories)
        tintProgress(pbProtein, p, targetProtein)
        tintProgress(pbFat, f, targetFat)
        tintProgress(pbCarbs, carb, targetCarbs)
    }

    // ---------- profile ----------

    private fun loadProfile() {
        AuthApi.getProfile(
            userId,
            onSuccess = {
                runOnUiThread {
                    tvUserName.text = "${it.firstName} ${it.lastName}"
                    tvUserStats.text =
                        "Height: ${it.heightCm} cm   Weight: ${it.weightKg} kg   Activity: ${it.activityLevel}"
                }
            },
            onError = { runOnUiThread { toast(it) } }
        )
    }

    // ---------- search ----------

    private fun setupSearch() {

        etSearch.addTextChangedListener { s ->
            val q = s?.toString()?.trim().orEmpty()
            if (q.isEmpty()) {
                searchAdapter.submit(emptyList())
                showSearchOverlay(false)
                return@addTextChangedListener
            }

            ninjasApi.query(
                query = q,
                onSuccess = {
                    runOnUiThread {
                        searchAdapter.submit(it)
                        showSearchOverlay(true)
                    }
                },
                onError = { err ->
                    runOnUiThread { toast(err) }
                }
            )
        }

        searchOverlay.setOnClickListener {
            showSearchOverlay(false)
        }
    }

    private fun showSearchOverlay(show: Boolean) {
        searchOverlay.visibility = if (show) View.VISIBLE else View.GONE
        if (!show) hideKeyboard()
    }

    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(window.decorView.windowToken, 0)
    }

    private fun setupBack() {
        onBackPressedDispatcher.addCallback(this) {
            if (searchOverlay.visibility == View.VISIBLE) {
                showSearchOverlay(false)
            } else finish()
        }
    }

    // ---------- food ----------

    private fun askGramsAndAdd(food: FoodItem) {
        val et = EditText(this).apply {
            inputType = InputType.TYPE_CLASS_NUMBER
            hint = "grams"
        }

        AlertDialog.Builder(this)
            .setTitle(food.name)
            .setView(et)
            .setPositiveButton("Add") { _, _ ->
                val grams = et.text.toString().toIntOrNull() ?: return@setPositiveButton
                val factor = grams / 100.0

                addedItems.add(
                    AddedFoodUi(
                        food.name,
                        grams,
                        (food.calories * factor).toInt(),
                        (food.protein_g * factor).toInt(),
                        (food.fat_total_g * factor).toInt(),
                        (food.carbohydrates_total_g * factor).toInt()
                    )
                )

                addedAdapter.notifyDataSetChanged()
                updateProgress()
                showSearchOverlay(false)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    // ---------- saved rations ----------

    private fun openSavedRation(id: Int) {
        SavedRationsApi.getById(
            id,
            onSuccess = { request ->
                runOnUiThread {
                    addedItems.clear()
                    request.items.forEach {
                        addedItems.add(
                            AddedFoodUi(
                                it.foodName ?: "",
                                it.weight_g?.toInt() ?: 0,
                                it.calories?.toInt() ?: 0,
                                it.protein?.toInt() ?: 0,
                                it.fat?.toInt() ?: 0,
                                it.carbs?.toInt() ?: 0
                            )
                        )
                    }
                    addedAdapter.notifyDataSetChanged()
                    updateProgress()
                }
            },
            onError = { runOnUiThread { toast(it) } }
        )
    }

    private fun deleteRation(r: SavedRationDto) {
        SavedRationsApi.delete(
            userId,
            r.name,
            onSuccess = {
                runOnUiThread {
                    toast("Deleted")
                    loadSavedRations()
                }
            },
            onError = { runOnUiThread { toast(it) } }
        )
    }

    private fun loadSavedRations() {
        SavedRationsApi.list(
            userId,
            onSuccess = { runOnUiThread { savedAdapter.submit(it) } },
            onError = { runOnUiThread { toast(it) } }
        )
    }


    private fun tintProgress(
        pb: ProgressBar,
        current: Int,
        target: Int
    ) {
        if (target <= 0) return

        val ratio = current.toFloat() / target.toFloat()

        val color = when {
            ratio < 0.7f -> 0xFF1976D2.toInt()   // blue
            ratio < 0.9f -> 0xFF388E3C.toInt()   // green
            ratio <= 1.1f -> 0xFFFBC02D.toInt()  // yellow
            else -> 0xFFD32F2F.toInt()           // red
        }

        pb.progressTintList = ColorStateList.valueOf(color)
    }

    private fun saveCurrentRation() {
        if (addedItems.isEmpty()) {
            toast("Nothing to save")
            return
        }

        val et = EditText(this)
        AlertDialog.Builder(this)
            .setTitle("Ration name")
            .setView(et)
            .setPositiveButton("Save") { _, _ ->
                val name = et.text.toString().trim()
                if (name.isEmpty()) return@setPositiveButton

                val dto = addedItems.map {
                    SavedRationItemDto(
                        it.name,
                        it.calories.toDouble(),
                        it.protein.toDouble(),
                        it.fat.toDouble(),
                        it.carbs.toDouble(),
                        it.grams.toDouble()
                    )
                }

                SavedRationsApi.create(
                    userId,
                    name,
                    dto,
                    onSuccess = {
                        runOnUiThread {
                            toast("Saved")
                            loadSavedRations()
                        }
                    },
                    onError = { runOnUiThread { toast(it) } }
                )
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun toast(s: String) =
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show()
}

data class AddedFoodUi(
    val name: String,
    val grams: Int,
    val calories: Int,
    val protein: Int,
    val fat: Int,
    val carbs: Int
)