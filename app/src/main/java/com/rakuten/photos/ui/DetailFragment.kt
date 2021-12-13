package com.rakuten.photos.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.rakuten.photos.R
import com.rakuten.photos.model.Photo
import com.rakuten.photos.viewmodel.DetailViewModel
import com.rakuten.photos.viewmodel.MainViewModel
import com.rakuten.photoss.utils.Constants
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_detail.*

class DetailFragment: Fragment() {

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
        val view = inflater.inflate(R.layout.fragment_detail, container, false)
        detailViewModel.photo.observe(viewLifecycleOwner, { photo ->
            setPhoto(photo)
            mainViewModel.setTitle(photo.title)
        })
        return view
    }

    private fun setPhoto(photo: Photo) {
        Picasso.with(context)
                .load(Constants.PHOTO_BASE_URL + photo.server + "/" + listOf(photo.id, photo.secret, "s").joinToString("_") + ".jpg")
                .into(photo_image)
        photo_id.text = photo.id
        photo_owner.text = photo.owner
        photo_secret.text = photo.secret
        photo_server.text = photo.server
        photo_farm.text = photo.farm.toString()
        photo_title.text = photo.title
        is_public.text = photo.ispublic.toString()
        is_friend.text = photo.isfriend.toString()
        is_family.text = photo.isfamily.toString()
    }

    companion object {
        fun newInstance() = DetailFragment()
    }
}