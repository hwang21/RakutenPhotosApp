package com.rakuten.photos.ui

import android.os.Build
import android.os.Build.VERSION
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.TranslateAnimation
import android.widget.AbsListView
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.rakuten.photos.R
import com.rakuten.photos.adapter.MainAdapter
import com.rakuten.photos.utils.*
import com.rakuten.photos.viewmodel.DetailViewModel
import com.rakuten.photos.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.fragment_main.view.*
import kotlinx.android.synthetic.main.fragment_main.view.title
import kotlinx.android.synthetic.main.header.view.*
import kotlin.math.min

class MainFragment : Fragment() {

    private var cachedVerticalScrollRange = 0
    private var quickReturnViewHeight = 0
    private var state = STATE_ONSCREEN
    private var scrollY = 0
    private var minRawY = 0

    private val mainViewModel by lazy {
        ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
    }

    private val detailViewModel by lazy {
        ViewModelProvider(requireActivity()).get(DetailViewModel::class.java)
    }


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_main, container, false)
        val quickReturnView = view.quickReturnView
        val listView = view.listView
        val adapter = MainAdapter(requireContext()) { photo ->
            detailViewModel.setPhoto(photo)
            requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment, DetailFragment.newInstance())
                    .addToBackStack(null)
                    .commit()
        }
        listView.adapter  = adapter

        mainViewModel.title.observe(viewLifecycleOwner, { title ->
            view?.title?.text = title
        })
        mainViewModel.getData().observe(viewLifecycleOwner, {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        listView.visibility = View.VISIBLE
                        progressBar.visibility = View.GONE
                        adapter.apply {
                            setData(resource.data!!)
                        }
                        val header = inflater.inflate(R.layout.header, container, false)
                        setupListView(listView, header, quickReturnView)
                    }
                    Status.ERROR -> {
                        listView.visibility = View.VISIBLE
                        progressBar.visibility = View.GONE
                        Toast.makeText(activity, it.message, Toast.LENGTH_LONG).show()
                    }
                    Status.LOADING -> {
                        progressBar.visibility = View.VISIBLE
                        listView.visibility = View.GONE
                    }
                }
            }
        })
        return view
    }

    private fun setupListView(listView: ListView, header: View, quickReturnView: View) {
        val params = AbsListView.LayoutParams(
                AbsListView.LayoutParams.MATCH_PARENT,
                AbsListView.LayoutParams.MATCH_PARENT
        )
        header.layoutParams = params
        listView.addHeaderView(header)
        listView.viewTreeObserver.addOnGlobalLayoutListener {
            quickReturnViewHeight = quickReturnView.height
            listView.computeScrollY()
            cachedVerticalScrollRange = listView.getListHeight()

        }
        listView.setOnScrollListener(object : AbsListView.OnScrollListener {
            override fun onScroll(
                    view: AbsListView, firstVisibleItem: Int,
                    visibleItemCount: Int, totalItemCount: Int) {
                scrollY = 0
                var translationY = 0
                if (listView.isScrollYComputed()) {
                    scrollY = listView.getComputedScrollY()
                }
                val placeHolder = header.placeHolder
                val rawY: Int = placeHolder.top - min(cachedVerticalScrollRange - listView.height, scrollY)
                when (state) {
                    STATE_OFFSCREEN -> {
                        if (rawY <= minRawY) {
                            minRawY = rawY
                        } else {
                            state = STATE_RETURNING
                        }
                        translationY = rawY
                    }
                    STATE_ONSCREEN -> {
                        if (rawY < -quickReturnViewHeight) {
                            state = STATE_OFFSCREEN
                            minRawY = rawY
                        }
                        translationY = rawY
                    }
                    STATE_RETURNING -> {
                        translationY = rawY - minRawY - quickReturnViewHeight
                        if (translationY > 0) {
                            translationY = 0
                            minRawY = rawY - quickReturnViewHeight
                        }
                        if (rawY > 0) {
                            state = STATE_ONSCREEN
                            translationY = rawY
                        }
                        if (translationY < -quickReturnViewHeight) {
                            state = STATE_OFFSCREEN
                            minRawY = rawY
                        }
                    }
                }
                if (VERSION.SDK_INT <= Build.VERSION_CODES.HONEYCOMB) {
                    val anim = TranslateAnimation(
                            0F,
                            0F,
                            translationY.toFloat(),
                            translationY.toFloat()
                    ).apply {
                        fillAfter = true
                        duration = 0
                    }
                    quickReturnView.startAnimation(anim)
                } else {
                    quickReturnView.translationY = translationY.toFloat()
                }
            }

            override fun onScrollStateChanged(view: AbsListView, scrollState: Int) {}
        })
    }

    companion object {
        private const val STATE_ONSCREEN = 0
        private const val STATE_OFFSCREEN = 1
        private const val STATE_RETURNING = 2

        fun newInstance() = MainFragment()
    }
}


