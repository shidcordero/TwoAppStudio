package com.sample.testapplication.ui.activity.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.sample.testapplication.R
import com.sample.testapplication.databinding.ItemUrlBinding
import com.sample.testapplication.model.Url
import com.sample.testapplication.viewmodel.UrlItemViewModel
import kotlinx.android.synthetic.main.item_url.view.*
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.util.*


class UrlAdapter : RecyclerView.Adapter<UrlAdapter.UrlViewHolder?>(), KoinComponent {
    private var urlList: MutableList<Url>

    init {
        urlList = ArrayList()
    }

    fun updateList(itemModelList: List<Url>, changeSort: Boolean) {
        if (itemModelList.isNotEmpty()) {
            if (changeSort) {
                urlList.clear()
                urlList.addAll(0, itemModelList)
                notifyDataSetChanged()
            } else {
                val previousSize: Int = urlList.size
                urlList.addAll(previousSize, itemModelList)
                notifyItemRangeChanged(previousSize, urlList.size)
            }
        }
    }

    fun addUrl(url: Url) {
        this.urlList.add(0, url)
        notifyItemInserted(0)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): UrlViewHolder {
        val itemUrlBinding: ItemUrlBinding = DataBindingUtil.inflate(
            LayoutInflater.from(viewGroup.context),
            R.layout.item_url,
            viewGroup,
            false
        )
        return UrlViewHolder(itemUrlBinding)
    }

    override fun onBindViewHolder(holder: UrlViewHolder, position: Int) {
        val url: Url = urlList[position]

        val urlItemViewModel: UrlItemViewModel by inject()
        urlItemViewModel.url = url
        holder.itemUrlBinding.urlItemViewModel = urlItemViewModel
        holder.itemUrlBinding.root.isLongClickable = true
    }

    override fun getItemCount(): Int {
        return urlList.size
    }

    fun removeList(ids: ArrayList<String>) {
        urlList = urlList.filter { !ids.contains(it.id) }.map { it }.toMutableList()
        notifyDataSetChanged()
    }

    class UrlViewHolder(internal val itemUrlBinding: ItemUrlBinding) : RecyclerView.ViewHolder(itemUrlBinding.root)
}
