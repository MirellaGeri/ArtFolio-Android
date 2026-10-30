package com.mirellageri.artfolio.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mirellageri.artfolio.R
import com.mirellageri.artfolio.data.Project
import com.mirellageri.artfolio.databinding.ItemProjectBinding
import com.bumptech.glide.Glide
import java.io.File

class ProjectAdapter(
    var items : List<Project>,
    val onItemCLick : (position:Int)-> Unit
): RecyclerView.Adapter<ProjectViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemProjectBinding.inflate(layoutInflater,parent,false)
        return ProjectViewHolder(binding)
    }
    override fun onBindViewHolder(holder: ProjectViewHolder, position: Int) {
        val project = items[position]
        holder.render(project)
        holder.itemView.setOnClickListener {
            onItemCLick(position)
        }
    }
    override fun getItemCount(): Int = items.size
    fun updateData(dataSet : List<Project>){
        this.items = dataSet
        notifyDataSetChanged()
    }
}

class ProjectViewHolder(val binding: ItemProjectBinding) : RecyclerView.ViewHolder(binding.root){
    fun render(project: Project){
        binding.projectTitleTextView.text = project.title
        binding.categoryTextView.text = project.category
        val uri = project.imageUrl
        Log.i("ADAPTER","ruta de image es: ${project.imageUrl}")
        if (uri.startsWith("http")){
            Glide.with(binding.root)
                .load(uri) // 👈 Aquí le pasamos el string real de SQLite (URL o Galería)
                .placeholder(R.drawable.bg_image_placeholder) // Solo se muestra MIENTRAS descarga
                .error(R.drawable.ic_launcher_background)               // Se muestra si el enlace se rompe
                .into(binding.projectImageView)
        }else{
            val uriGallery = File(uri)
            Glide.with(binding.root)
                .load(uriGallery) // 👈 Aquí le pasamos el string real de SQLite (URL o Galería)
                .placeholder(R.drawable.bg_image_placeholder) // Solo se muestra MIENTRAS descarga
                .error(R.drawable.ic_launcher_background)               // Se muestra si el enlace se rompe
                .into(binding.projectImageView)
        }
    }
}