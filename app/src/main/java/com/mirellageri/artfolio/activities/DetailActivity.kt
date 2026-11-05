package com.mirellageri.artfolio.activities

import android.content.Intent
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
    var id = -1

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
        id = intent.getIntExtra("PROJECT_ID",id)

        binding.editButton.setOnClickListener {
            val intent = Intent(this, AddProjectActivity::class.java)
            intent.putExtra("PROJECT_ID", id)
            startActivity(intent)
        }
        loadData(id)
    }

    override fun onResume() {
        super.onResume()
        loadData(id)
    }
    private fun loadData(id:Int){
        val project = projectDAO.findById(id)!!
        supportActionBar?.title = project.title
        binding.titleTextView.text = project.title
        binding.categoryChip.text = project.category.name
        binding.descriptionTextView.text = project.description
        binding.startDateTextView.text = project.startDate
        if (project.endDate.isEmpty() ){
            binding.endDateTextView.text = getString(R.string.detail_without_data)
        }else{
            binding.endDateTextView.text = project.endDate
        }
        binding.toolsChip.text = project.tools
        if (project.imageUrl.startsWith("http")) {
            Glide.with(this)
                .load(project.imageUrl)
                .into(binding.projectImageView)
        } else {
            Glide.with(this)
                .load(File(project.imageUrl)) // Tu archivo copiado de la galería
                .into(binding.projectImageView)
        }
    }
}