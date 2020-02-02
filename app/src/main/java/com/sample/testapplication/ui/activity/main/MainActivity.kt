package com.sample.testapplication.ui.activity.main

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.Query
import com.sample.testapplication.R
import com.sample.testapplication.base.BaseActivity
import com.sample.testapplication.databinding.ActivityMainBinding
import com.sample.testapplication.message.DialogMessage
import com.sample.testapplication.message.UrlListMessage
import com.sample.testapplication.message.UrlMessage
import com.sample.testapplication.message.UrlRemoveMessage
import com.sample.testapplication.model.Url
import com.sample.testapplication.utils.Constants
import com.sample.testapplication.utils.RecyclerViewTouchListener
import com.sample.testapplication.utils.RecyclerViewTouchListener.ClickListener
import com.sample.testapplication.viewmodel.MainViewModel
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.concurrent.Callable


class MainActivity : BaseActivity() {

    private val mainViewModel: MainViewModel by viewModel()
    private lateinit var binding: ActivityMainBinding
    private var urlAdapter: UrlAdapter? = null
    private var isEditMode: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        binding.mainViewModel = mainViewModel

        setSupportActionBar(binding.tbMain)
        setupRecyclerViews()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        supportActionBar?.title = if (isEditMode)
                getString(R.string.delete)
            else
                getString(R.string.app_name)
        supportActionBar?.setDisplayHomeAsUpEnabled(isEditMode)
        supportActionBar?.setDisplayShowHomeEnabled(isEditMode)

        menu?.findItem(R.id.itemSort)?.isVisible = !isEditMode
        menu?.findItem(R.id.itemDelete)?.isVisible = isEditMode

        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.itemSortAscending -> {
                binding.mainViewModel?.getUrlList(Query.Direction.ASCENDING)
            }
            R.id.itemSortDescending -> {
                binding.mainViewModel?.getUrlList(Query.Direction.DESCENDING)
            }
            R.id.itemDelete -> {
                EventBus.getDefault().post(DialogMessage(
                    this,
                    title = R.string.confirm_delete,
                    message = R.string.confirm_delete_desc,
                    positiveLabel = R.string.lm_yes,
                    positiveCallback = Callable {
                        val itemsToDelete = ArrayList<String>()
                        var i = 0
                        while (i < binding.rvUrlList.childCount) {
                            val holder: UrlAdapter.UrlViewHolder =
                                binding.rvUrlList.getChildViewHolder(binding.rvUrlList.getChildAt(i))
                                        as UrlAdapter.UrlViewHolder
                            if (holder.itemUrlBinding.urlItemViewModel?.isSelected?.get()!!){
                                itemsToDelete.add(holder.itemUrlBinding.urlItemViewModel?.url?.id!!)
                            }
                            ++i
                        }
                        binding.mainViewModel?.deleteItems(itemsToDelete)
                        true
                    },
                    negativeLabel = R.string.lm_no))
            }
            android.R.id.home -> {
                if (isEditMode) {
                    backPress()
                }
            }
            else -> return super.onOptionsItemSelected(item)
        }

        return true
    }

    private fun backPress(){
        isEditMode = false

        var i = 0
        while (i < binding.rvUrlList.childCount) {
            val holder: UrlAdapter.UrlViewHolder =
                binding.rvUrlList.getChildViewHolder(binding.rvUrlList.getChildAt(i))
                        as UrlAdapter.UrlViewHolder
            holder.itemUrlBinding.urlItemViewModel?.isSelected?.set(false)
            ++i
        }
        invalidateOptionsMenu()
    }

    private fun setupRecyclerViews() {
        binding.rvUrlList.layoutManager = LinearLayoutManager(this)
        val dividerItemDecoration = DividerItemDecoration(
            binding.rvUrlList.context,
            (binding.rvUrlList.layoutManager as LinearLayoutManager).orientation
        )
        binding.rvUrlList.addItemDecoration(dividerItemDecoration)
        urlAdapter = UrlAdapter()
        binding.rvUrlList.adapter = urlAdapter

        binding.rvUrlList.addOnItemTouchListener(
            RecyclerViewTouchListener(this, binding.rvUrlList, object : ClickListener {
                override fun onClick(view: View?, position: Int) {
                    if (isEditMode) {
                        val viewHolder: UrlAdapter.UrlViewHolder? =
                            binding.rvUrlList.findViewHolderForAdapterPosition(position) as UrlAdapter.UrlViewHolder?
                        viewHolder?.itemUrlBinding?.urlItemViewModel?.isSelected?.set(!viewHolder.itemUrlBinding.urlItemViewModel?.isSelected?.get()!!)
                    }
                }

                override fun onLongClick(view: View?, position: Int){
                    if (!isEditMode) {
                        val viewHolder: UrlAdapter.UrlViewHolder? =
                            binding.rvUrlList.findViewHolderForAdapterPosition(position) as UrlAdapter.UrlViewHolder?
                        viewHolder?.itemUrlBinding?.urlItemViewModel?.isSelected?.set(true)
                        isEditMode = true
                        invalidateOptionsMenu()
                    }
                }
            })
        )
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onUrlListEvent(message: UrlListMessage){
        urlAdapter?.updateList(message.urlList, message.changeSort)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onUrlEvent(message: UrlMessage){
        urlAdapter?.addUrl(message.url)
        binding.rvUrlList.smoothScrollToPosition(0)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onUrlRemoveEvent(message: UrlRemoveMessage){
        urlAdapter?.removeList(message.ids)
        isEditMode = false
        invalidateOptionsMenu()
    }

    override fun onBackPressed() {
        if (isEditMode)
            backPress()
        else
            super.onBackPressed()
    }
}
