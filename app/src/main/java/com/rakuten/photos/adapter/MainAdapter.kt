package com.rakuten.photos.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.rakuten.photos.R
import com.rakuten.photos.model.Photo
import com.rakuten.photoss.utils.Constants.Companion.PHOTO_BASE_URL
import com.squareup.picasso.Picasso

class MainAdapter(private val context: Context,
                  private val onItemClicked: (Photo) -> Unit) : BaseAdapter() {

    var photoList = emptyList<Photo>()

    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return photoList.size
    }

    override fun getItem(position: Int): Any {
        return photoList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View
        val holder: ViewHolder

        if (convertView == null) {

            view = inflater.inflate(R.layout.list_item_photo, parent, false)

            holder = ViewHolder()
            holder.image = view.findViewById(R.id.image) as ImageView
            holder.title = view.findViewById(R.id.title) as TextView

            view.tag = holder
        } else {
            view = convertView
            holder = convertView.tag as ViewHolder
        }

        val title = holder.title
        val image = holder.image
        image.setOnClickListener{ onItemClicked(photoList[position]) }

        val photo = getItem(position) as Photo

        title.text = photo.title
        Picasso.with(context)
            .load(PHOTO_BASE_URL + photo.server + "/" + listOf(photo.id, photo.secret, "s").joinToString("_") + ".jpg")
            .placeholder(R.mipmap.ic_launcher)
            .into(image)

        return view
    }

    fun setData(photos: List<Photo>) {
        this.photoList = photos
        notifyDataSetChanged()
    }

    private class ViewHolder {
        lateinit var title: TextView
        lateinit var image: ImageView
    }
}