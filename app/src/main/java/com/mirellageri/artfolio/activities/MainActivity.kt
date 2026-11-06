package com.mirellageri.artfolio.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.mirellageri.artfolio.R
import com.mirellageri.artfolio.adapters.ProjectAdapter
import com.mirellageri.artfolio.data.Category
import com.mirellageri.artfolio.data.CategoryDAO
import com.mirellageri.artfolio.data.Project
import com.mirellageri.artfolio.data.ProjectDAO
import com.mirellageri.artfolio.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var adapter: ProjectAdapter
    lateinit var projectDAO: ProjectDAO
    lateinit var categoryDAO: CategoryDAO
    var projectList: List<Project> = emptyList()
    var filteredProjectList: List<Project> = emptyList()
    var isFilter = false
    //podemos filtrar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }
        //inicializar
        projectDAO = ProjectDAO(this)
        categoryDAO = CategoryDAO(this)
        adapter = ProjectAdapter(projectList, ::onItemClick)
        binding.recyclerViewMain.adapter = adapter
        binding.recyclerViewMain.layoutManager = GridLayoutManager(this, 2)
        supportActionBar?.title = getString(R.string.main_title)
        binding.addProjectButton.setOnClickListener {
            val intent = Intent(this, AddProjectActivity::class.java)
            startActivity(intent)
        }
        getNavigationView()
    }

    override fun onResume() {
        super.onResume()
        reloadData()
    }
    private fun reloadData(){
        projectList = projectDAO.findAll()
        if (!isFilter) {
            adapter.updateData(projectList)
        }else{
            adapter.updateData(filteredProjectList)
        }
    }
    private fun onItemClick(position: Int){
        //val project = projectList[position]
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra("PROJECT_ID",position)
        startActivity(intent)
    }
    private fun getNavigationView(){
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when(item.itemId){
                R.id.menu_home -> {
                    supportActionBar?.title = getString(R.string.main_title)
                    isFilter = false
                    binding.addProjectButton.show()
                    reloadData()
                    true
                }
                R.id.menu_category -> {
                    val popup = PopupMenu(this, binding.bottomNavigationView)
                    val listCategory = categoryDAO.findAll()
                    listCategory.forEachIndexed { index, categoria ->
                        popup.menu.add(Menu.NONE, categoria.id, index, categoria.name)
                    }
                    // categoria elegida
                    popup.setOnMenuItemClickListener { menuItem ->
                        val idSelected = menuItem.itemId
                        val nameSelected = menuItem.title.toString()
                        binding.addProjectButton.hide()
                        filteredProjectList = projectDAO.findAllByCategory(Category(idSelected,nameSelected))
                        supportActionBar?.title = nameSelected
                        isFilter = true
                        // Actualizas tu adaptador clásico de proyectos con la lista filtrada
                        adapter.updateData(filteredProjectList)
                        true
                    }
                    popup.show() // Mostramos el menú hacia arriba
                    false
                }R.id.menu_user-> {
                true
            }
                else -> false
            }
        }
    }
}