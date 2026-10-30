package com.mirellageri.artfolio.activities

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.mirellageri.artfolio.R
import com.mirellageri.artfolio.data.ProjectDAO
import com.mirellageri.artfolio.databinding.ActivityDetailBinding
import com.bumptech.glide.Glide
import java.io.File

class DetailActivity : AppCompatActivity() {
    lateinit var binding: ActivityDetailBinding
    lateinit var projectDAO: ProjectDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        projectDAO = ProjectDAO(this)
        val id = intent.getIntExtra("PROJECT_ID",-1)
        loadData(id)
    }
    private fun loadData(id:Int){
        try {
            projectDAO.openDB()
            val project = projectDAO.findById(id)!!
            binding.titleTextView.text = project.title
            binding.categoryChip.text = project.category
            binding.descriptionTextView.text = project.description
            if (project.imageUrl.startsWith("http")) {
                Glide.with(this)
                    .load(project.imageUrl)
                    .into(binding.projectImageView)
            } else {
                Glide.with(this)
                    .load(File(project.imageUrl)) // Tu archivo copiado de la galería
                    .into(binding.projectImageView)
            }
            binding.projectImageView

        }catch (e : Exception){
            Log.i("DETAIL", e.localizedMessage)
        }finally {
            projectDAO.closeDB()
        }
    }
}