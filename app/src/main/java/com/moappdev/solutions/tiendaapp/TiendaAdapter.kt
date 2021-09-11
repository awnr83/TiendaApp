package com.moappdev.solutions.tiendaapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.moappdev.solutions.tiendaapp.database.Tienda
import com.moappdev.solutions.tiendaapp.databinding.ItemBinding


class TiendaAdapter(private var tiendas: MutableList<Tienda>,
                    private var listener: OnClickListener):
    RecyclerView.Adapter<TiendaAdapter.ViewHolder>() {

    private lateinit var mContext: Context

    inner class ViewHolder(view: View):RecyclerView.ViewHolder(view){
        val mBinding= ItemBinding.bind(view)

        fun setListener(tienda: Tienda){
            with(mBinding.root) {
                setOnClickListener {
                    listener.onClick(tienda.id)
                }
                setOnLongClickListener {
                    listener.onDelete(tienda)
                    true
                }
            }
            mBinding.cbFav.setOnClickListener { listener.onFavorito(tienda) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        mContext= parent.context
        val layoutInflater= LayoutInflater.from(mContext).inflate(R.layout.item, parent, false)

        return ViewHolder(layoutInflater)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val tienda= tiendas.get(position)
        with(holder) {
            setListener(tienda)
            mBinding.apply{
                tvName.text = tienda.nombre
                cbFav.isChecked = tienda.favorita

                Glide.with(mBinding.root)
                    .load(tienda.imagen)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .centerCrop()
                    .circleCrop()
                    .into(imgLogo)
            }
        }
    }

    override fun getItemCount():Int= tiendas.size

//-----------------funciones
    fun setTiendas(tiendas: MutableList<Tienda>) {
        this.tiendas=tiendas
        notifyDataSetChanged()
    }
    fun add(tienda: Tienda) {
        if(!tiendas.contains(tienda)) {
            tiendas.add(tienda)
            notifyDataSetChanged()
        }
    }
    fun update(tienda: Tienda) {
        val pos= tiendas.indexOf(tienda)
        if(pos!=-1) {
            tiendas[pos] = tienda
            notifyItemChanged(pos)
        }
    }
    fun delete(tienda: Tienda) {
        val pos= tiendas.indexOf(tienda)
        if(pos!=-1) {
            tiendas.removeAt(pos)
            notifyItemRemoved(pos)
        }
    }
}