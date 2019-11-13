package com.intellimind.speechnotes.ui.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.intellimind.speechnotes.R
import com.intellimind.speechnotes.ui.speech.SpeechViewModel
import java.util.ArrayList

open class BaseRecyclerAdapter<T : BaseModel> : RecyclerView.Adapter<BaseViewHolder>{
    private var layoutResource = 0
    var list: MutableList<T> = ArrayList()
    var handler: BaseHandler<*>? = null
    private var viewModel: SpeechViewModel? = null
    private var isAnimationEnabled: Int? = null

    constructor(layoutResource: Int, viewModel: SpeechViewModel?, handler: BaseHandler<*>?, anim: Int? = null) {
        this.layoutResource = layoutResource
        this.viewModel = viewModel
        this.handler = handler
        this.isAnimationEnabled = anim
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<ViewDataBinding>(
            layoutInflater, viewType, parent, false)
        return BaseViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }
    /* *
    * adding data for the first time in the list and scroll if required to last position
    * @param li
    */
    open fun updateList(li: List<T>?) {
        if (li != null) {
            this.list.clear()
            this.list = ArrayList(li)
            notifyDataSetChanged()
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val obj = list[position]
        holder.bind(obj, handler, viewModel)
    }

    override fun getItemViewType(position: Int): Int {
            return R.layout.lyt_speech_suggestion
    }
}