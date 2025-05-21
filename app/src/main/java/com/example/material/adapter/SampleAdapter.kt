package com.example.material.adapter

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.animation.R
import com.example.material.activity.AnimationsActivity
import com.example.material.activity.RevealActivity
import com.example.material.activity.SharedElementActivity
import com.example.material.activity.TransitionActivity
import com.example.material.bean.SampleBean
import com.example.material.utils.TransitionHelper
import com.example.material.utils.TransitionHelper.toAndroidXPairs
import com.example.material.utils.function.orZero
import java.lang.ref.WeakReference

class SampleAdapter(private val activity: Activity, private val mList: List<SampleBean>) : RecyclerView.Adapter<SampleAdapter.ViewHolder>() {
    private val mActivity by lazy { WeakReference(activity) }  //弱应用activity

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_sample, parent, false))
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val entity = mList[position]
        holder.sample_icon?.drawable?.let { DrawableCompat.setTint(it, entity.color.orZero) }
        holder.sample_name?.text = entity.name
        holder.sample_container?.setOnClickListener {
            when (holder.absoluteAdapterPosition) {
                0 -> transitionToActivity(TransitionActivity::class.java, entity)
                1 -> transitionToActivity(SharedElementActivity::class.java, holder, entity)
                2 -> transitionToActivity(AnimationsActivity::class.java, entity)
                3 -> transitionToActivity(RevealActivity::class.java, holder, entity, R.string.transition_reveal1)
            }
        }
    }

    private fun transitionToActivity(target: Class<*>, sampleEntity: SampleBean) {
        mActivity.get()?.let {
            val pairs = TransitionHelper.createSafeTransitionParticipants(it, true)
            startActivity(target, pairs, sampleEntity)
        }
    }

    private fun transitionToActivity(target: Class<*>, holder: ViewHolder, sampleEntity: SampleBean, transitionName: Int) {
        mActivity.get()?.let {
            val pairs = TransitionHelper.createSafeTransitionParticipants(it, false, Pair(holder.sample_icon as View, it.getString(transitionName)))
            startActivity(target, pairs, sampleEntity)
        }
    }

    private fun transitionToActivity(target: Class<*>, holder: ViewHolder, sampleEntity: SampleBean) {
        mActivity.get()?.let {
            val pairs = TransitionHelper.createSafeTransitionParticipants(it, false, Pair(holder.sample_icon as View, "square_blue"), Pair(holder.sample_name as View, "sample_blue_title"))
            startActivity(target, pairs, sampleEntity)
        }
    }

    //统一跳转方法
    private fun startActivity(target: Class<*>, pairs: Array<Pair<View, String>>, sampleEntity: SampleBean) {
        mActivity.get()?.let {
            val intent = Intent(mActivity.get(), target)
            val transitionActivityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(it, *pairs.toAndroidXPairs())
            val bundle = Bundle()
            bundle.putParcelable("sample", sampleEntity)
            intent.putExtras(bundle)
            it.startActivity(intent, transitionActivityOptions.toBundle())
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var sample_icon: ImageView? = null //左侧圆球
        var sample_name: TextView? = null //右侧文字
        var sample_container: LinearLayout? = null //整体文字点击

        init {
            sample_icon = itemView.findViewById(R.id.sample_icon)
            sample_name = itemView.findViewById(R.id.sample_name)
            sample_container = itemView.findViewById(R.id.sample_container)
        }
    }

}