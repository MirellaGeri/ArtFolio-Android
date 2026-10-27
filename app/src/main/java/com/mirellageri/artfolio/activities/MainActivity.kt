package com.mirellageri.artfolio.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mirellageri.artfolio.R
import com.mirellageri.artfolio.adapters.ProjectAdapter
import com.mirellageri.artfolio.data.Project
import com.mirellageri.artfolio.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var adapter: ProjectAdapter
    lateinit var bottomNavigationView : BottomNavigationView
    var projectList: List<Project> = emptyList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bottomNavigationView = binding.bottomNavigationView
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        adapter = ProjectAdapter(projectList, ::onItemClick)
        binding.recyclerViewMain.adapter = adapter
        binding.recyclerViewMain.layoutManager = GridLayoutManager(this, 2)
    }
    private fun onItemClick(position: Int){
        val project = projectList[position]
        //val intent = Intent
    }
    private fun getNavigationView(){
        bottomNavigationView.setOnItemSelectedListener { item ->
            when(item.itemId){
                R.id.menu_home -> {
                    true
                }
                R.id.menu_category -> {
                    //showFavoriteGames()
                    true
                }R.id.menu_user-> {
                true
            }
                else -> false
            }
        }
    }
}