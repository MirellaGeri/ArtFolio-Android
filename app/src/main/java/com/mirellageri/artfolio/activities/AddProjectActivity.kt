package com.mirellageri.artfolio.activities

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.mirellageri.artfolio.R
import com.mirellageri.artfolio.data.Project
import com.mirellageri.artfolio.data.ProjectDAO
import com.mirellageri.artfolio.databinding.ActivityAddProjectBinding
import com.mirellageri.artfolio.databinding.DialogCategoryBinding
import com.bumptech.glide.Glide
import com.google.android.material.datepicker.MaterialDatePicker
import com.mirellageri.artfolio.data.Category
import com.mirellageri.artfolio.data.CategoryDAO
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class AddProjectActivity : AppCompatActivity() {
    lateinit var binding: ActivityAddProjectBinding
    lateinit var project: Project
    lateinit var projectDAO: ProjectDAO
    lateinit var category: Category
    lateinit var categoryDAO: CategoryDAO
    lateinit var listCategory : MutableList<Category>
    private lateinit var listCategoryNames: MutableList<String>
    lateinit var adapterCategory: ArrayAdapter<String>
    private var uriImage:String = ""
    private val openGalleryLauncher = registerForActivityResult(ActivityResultContracts.PickVisualMedia()){
        uri: Uri? ->
        if (uri != null){
            val secureUri = copyImageToInternalStorage(uri)
            if (secureUri != null) {
                uriImage = secureUri
                binding.imageUrlTextField.setText("")
                binding.previewImageView.setImageURI(uri)
            }else{
                Toast.makeText(this, getString(R.string.error_process_gallery_image), Toast.LENGTH_SHORT).show()
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAddProjectBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        //inicializar DAOs
        categoryDAO = CategoryDAO(this)
        projectDAO = ProjectDAO(this)
        val id = intent.getIntExtra("PROJECT_ID", -1)
        //category = categoryDAO.find(categoryId)!!
        if (id != -1) {
           // Edit
            project = projectDAO.findById(id)!!
            supportActionBar?.title = getString(R.string.edit_project)
            binding.titleTextField.editText?.setText(project.title)
            binding.descriptionTextField.editText?.setText(project.description)
            binding.categoryTextField.editText?.setText(project.category.name)
            binding.startDateTextField.editText?.setText(project.startDate)
            binding.endDateTextField.editText?.setText(project.endDate)
            binding.toolsInputLayout.editText?.setText(project.tools)
            if (project.imageUrl.startsWith("http")) {
                Glide.with(this)
                    .load(project.imageUrl)
                    .into(binding.previewImageView)
                binding.imageUrlTextField.setText(project.imageUrl)
            } else {
                Glide.with(this)
                    .load(File(project.imageUrl)) // Tu archivo copiado de la galería
                    .into(binding.previewImageView)
            }
            uriImage = project.imageUrl
        } else {
           // Crear
            category = Category(-1,"")
            project = Project(-1,"",category,"","","","","")
           supportActionBar?.title = getString(R.string.new_project)
        }

        listCategory = categoryDAO.findAll().toMutableList()
        listCategoryNames = listCategory.map { it.name }.toMutableList()
        adapterCategory = ArrayAdapter(this,android.R.layout.simple_dropdown_item_1line,listCategoryNames)
        binding.categoryACTextView.setAdapter(adapterCategory)
        binding.addCategoryButton.setOnClickListener {
            showCategoryDialog()
        }
        binding.galleryButton.setOnClickListener {
            openGalleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
        showImageLink()
        binding.startDateEditText.setOnClickListener {
            showDataPicker(binding.startDateEditText,getString(R.string.title_start_date),"START_PICKER")
        }
        binding.startDateTextField.setEndIconOnClickListener {
            showDataPicker(binding.startDateEditText,getString(R.string.title_start_date),"START_PICKER")
        }
        binding.endDateEditText.setOnClickListener {
            showDataPicker(binding.endDateEditText,getString(R.string.title_end_date),"END_PICKER")
        }
        binding.endDateTextField.setEndIconOnClickListener {
            showDataPicker(binding.endDateEditText,getString(R.string.title_end_date),"END_PICKER")
        }
        //Button save all
        binding.saveButton.setOnClickListener {
            saveProject(project)
        }
    }
    private fun saveProject(project: Project){
        val title = binding.titleTextField.editText?.text.toString().trim()
        val description = binding.descriptionTextField.editText?.text.toString().trim()
        val categoryName = binding.categoryACTextView.text.toString().trim()
        val startDate = binding.startDateEditText.text.toString().trim()
        val endDate = binding.endDateEditText.text.toString().trim()
        val tools = binding.toolsEditText.text.toString().trim()

        if (title.isEmpty() || description.isEmpty() || uriImage.isEmpty() || startDate.isEmpty() || tools.isEmpty()){
            Toast.makeText(this, getString(R.string.toast_validation_text_view), Toast.LENGTH_SHORT).show()
            return
        }

        if (startDate.isNotEmpty() && endDate.isNotEmpty()) {
            try {
                val dateFormat = SimpleDateFormat(getString(R.string.date_format), Locale.getDefault())
                val start = dateFormat.parse(binding.startDateEditText.text.toString())
                val end = dateFormat.parse(binding.endDateEditText.text.toString())
                if (start != null && end != null && end.before(start)) {
                    Toast.makeText(this, getString(R.string.toast_validation_date), Toast.LENGTH_SHORT).show()
                    return
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        val categoryDB = categoryDAO.findIdxName(categoryName)
        if (categoryDB  == null) {
            Toast.makeText(this, getString(R.string.toast_validation_category), Toast.LENGTH_SHORT).show()
            return
        }
        category = categoryDB
        project.title = title
        project.description = description
        project.category = category
        project.imageUrl = uriImage
        project.startDate = startDate
        project.endDate = endDate
        project.tools = tools
        projectDAO.save(project)
        finish()
    }
    private fun showCategoryDialog(){
        val dialogBinding = DialogCategoryBinding.inflate(layoutInflater)
        val builder = MaterialAlertDialogBuilder(this)
            .setMessage(getString(R.string.title_hint_dialog))
            .setView(dialogBinding.root)
            .setPositiveButton(getString(R.string.bottom_save_label)){_,_ ->
            }
            .setNegativeButton(getString(R.string.bottom_cancel_label)){_,_ -> }
        val dialog = builder.create()
        dialog.show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val newCategory = dialogBinding.categoryTextField.editText!!.text.toString()
            if (newCategory.isEmpty()){
                dialogBinding.categoryTextField.error = getString(R.string.warning_category_input)
            }else{
                if (!listCategoryNames.contains(newCategory)) {

                    // 1. Crear el objeto intermedio y guardarlo en la base de datos física
                    val newCategoryObject = Category(-1,newCategory)
                    categoryDAO.insert(newCategoryObject)

                    // 2. Buscarlo de nuevo para recuperar el ID que SQLite le asignó automáticamente
                    val savedCategory = categoryDAO.findIdxName(newCategory)

                    if (savedCategory != null) {
                        // Actualizamos nuestra lista de objetos global
                        listCategory.add(savedCategory)

                        // Actualizamos la lista de nombres visuales y notificamos al adaptador
                        listCategoryNames.add(newCategory)
                        adapterCategory.notifyDataSetChanged()
                    }
                }
                binding.categoryACTextView.setText(newCategory,false)
                Toast.makeText(this, getString(R.string.toast_new_category_message), Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
        }
    }
    private fun showImageLink(){
        binding.imageUrlTextField.setOnFocusChangeListener { _, hasFocus ->
            // Si hasFocus es false, significa que el usuario ya terminó de escribir en este campo
            if (!hasFocus) {
                val urlTexting = binding.imageUrlTextField.text.toString().trim()
                // Validamos que sea un enlace web real y que termine en formato de imagen (.jpg, .png)
                if (urlTexting.isNotEmpty() && urlTexting.startsWith("http") &&
                    (urlTexting.endsWith(".jpg") || urlTexting.endsWith(".png") || urlTexting.endsWith(".jpeg"))) {
                    uriImage = urlTexting
                    binding.imageUrlLayout.error = null
                    // Cargamos la vista previa de forma segura
                    Glide.with(this)
                        .load(uriImage)
                        .placeholder(R.drawable.bg_image_placeholder) // Imagen temporal mientras descarga
                        .error(R.drawable.ic_launcher_background)               // Imagen si el link está roto
                        .into(binding.previewImageView)
                } else if (urlTexting.isNotEmpty()) {
                    binding.imageUrlLayout.error = getString(R.string.error_view_image_preview)
                    binding.previewImageView.setImageResource(R.drawable.bg_image_placeholder)
                }else{
                    binding.imageUrlLayout.error = null
                }
            }
        }
    }
    private fun copyImageToInternalStorage(uri: Uri): String?{
        return try {
            val nameFile = "project_${System.currentTimeMillis()}.jpg"
            val destineFile = File(filesDir,nameFile)
            //open and close to read or write
            val inputStream : InputStream? = contentResolver.openInputStream(uri)
            val outputStream = FileOutputStream(destineFile)
            inputStream?.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
            destineFile.absolutePath
        }catch (e : Exception){
            Log.e("FORM",getString(R.string.error_copy_image_storage))
            null
        }
    }
    private fun showDataPicker(
        editTextTarget: com.google.android.material.textfield.TextInputEditText,
        title: String,
        tag: String
    ) {
        val datePickerBuilder = MaterialDatePicker.Builder.datePicker()
            .setTitleText(title)
        //  Lee la fecha del EditText actual para abrir el calendario en ese día
        if (editTextTarget.text.toString().isNotEmpty()) {
            try {
                val dateFormat = SimpleDateFormat(getString(R.string.date_format), Locale.getDefault())
                dateFormat.timeZone = TimeZone.getTimeZone(getString(R.string.time_zone))
                val parsedDate = dateFormat.parse(editTextTarget.text.toString())
                parsedDate?.let {
                    datePickerBuilder.setSelection(it.time)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        val datePicker = datePickerBuilder.build()
        // Al pulsar Aceptar, escribe el resultado en el EditText correspondiente
        datePicker.addOnPositiveButtonClickListener { selection ->
            val dateFormat = SimpleDateFormat(getString(R.string.date_format), Locale.getDefault())
            dateFormat.timeZone = TimeZone.getTimeZone(getString(R.string.time_zone))
            val formattedDate = dateFormat.format(Date(selection))
            editTextTarget.setText(formattedDate)
        }
        datePicker.addOnNegativeButtonClickListener { editTextTarget.clearFocus() }
        datePicker.addOnDismissListener { editTextTarget.clearFocus() }
        datePicker.show(supportFragmentManager, tag)
    }
}