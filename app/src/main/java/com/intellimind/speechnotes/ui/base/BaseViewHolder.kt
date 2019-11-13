package com.intellimind.speechnotes.ui.base

import androidx.databinding.ViewDataBinding
import androidx.databinding.library.baseAdapters.BR
import androidx.recyclerview.widget.RecyclerView
import com.intellimind.speechnotes.ui.speech.SpeechViewModel

class BaseViewHolder(val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root) {


    fun bind(obj: Any, handler: BaseHandler<*>?, baseViewModel: SpeechViewModel?) {
        binding.setVariable(BR.obj, obj)
        binding.setVariable(BR.handlers, handler)
        binding.setVariable(BR.viewModel, baseViewModel)
        binding.executePendingBindings()

    }
}