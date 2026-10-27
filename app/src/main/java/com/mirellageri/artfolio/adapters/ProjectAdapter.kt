package com.mirellageri.artfolio.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mirellageri.artfolio.R
import com.mirellageri.artfolio.data.Project
import com.mirellageri.artfolio.databinding.ItemProjectBinding

class ProjectAdapter(
    val items : List<Project>,
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
        holder.binding.projectCard.setOnClickListener {
            onItemCLick(position)
        }
    }
    override fun getItemCount(): Int = items.size
}

class ProjectViewHolder(val binding: ItemProjectBinding) : RecyclerView.ViewHolder(binding.root){
    fun render(project: Project){
        binding.projectTitleTextView.text = project.title
        binding.categoryTextView.text = project.category
        binding.projectImageView.setImageResource(R.drawable.bg_image_placeholder)
    }
}