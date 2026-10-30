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
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream

class AddProjectActivity : AppCompatActivity() {
    lateinit var binding: ActivityAddProjectBinding
    lateinit var projectDAO: ProjectDAO
    lateinit var listCategory : ArrayList<String>
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
                Toast.makeText(this, "Error al procesar la imagen de la galería", Toast.LENGTH_SHORT).show()
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
        //inicializar DAO
        projectDAO = ProjectDAO(this)
        listCategory = arrayListOf(getString(R.string.category_crochet),getString(R.string.category_origami),getString(R.string.category_art2d))
        adapterCategory = ArrayAdapter(this,android.R.layout.simple_dropdown_item_1line,listCategory)
        binding.categoryACTextView.setAdapter(adapterCategory)
        binding.addCategoryButton.setOnClickListener {
            showCategoryDialog()
        }
        binding.galleryButton.setOnClickListener {
            openGalleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
        showImageLink()
        binding.saveButton.setOnClickListener {
            val project = Project(-1,"","","","")
            saveProject(project)
        }
    }
    private fun saveProject(project: Project){
        val title = binding.titleTextField.text.toString().trim()
        val description = binding.descriptionTextField.text.toString().trim()
        val category = binding.categoryACTextView.text.toString().trim()
        if (title.isEmpty() || description.isEmpty() || uriImage.isEmpty()){
            Toast.makeText(this, "Por favor, completa todos los campos obligatorios", Toast.LENGTH_SHORT).show()
            return
        }
        project.title = title
        project.description = description
        project.category = category
        project.imageUrl = uriImage
        try {
            projectDAO.openDB()
            projectDAO.save(project)
            finish()
        }catch (e : Exception){
            Log.i("FORM", e.localizedMessage)
        }finally {
            projectDAO.closeDB()
        }
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
                if (!listCategory.contains(newCategory)){
                    listCategory.add(newCategory)
                    adapterCategory.notifyDataSetChanged()
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
            Log.e("FORM","Error to copy File to PhotoPicker")
            null
        }
    }
}