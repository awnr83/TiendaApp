package com.moappdev.solutions.tiendaapp

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.moappdev.solutions.tiendaapp.database.Tienda
import com.moappdev.solutions.tiendaapp.database.TiendaApplication
import com.moappdev.solutions.tiendaapp.databinding.ActivityMainBinding
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class MainActivity : AppCompatActivity(), OnClickListener, MainAux {

    private lateinit var mBinding: ActivityMainBinding
    private lateinit var mAdapter: TiendaAdapter
    private lateinit var mGridLayoutManager: GridLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mBinding.btnFab.setOnClickListener {
            goToTiendaFragment()
            hideBtnFab()//mBinding.btnFab.hide()
        }
        setupRecyclerView()
    }

//--------------Funciones------------------------
    private fun goToTiendaFragment(args: Bundle?=null){
        val fragment= TiendaFragment()
        if(args!=null) fragment.arguments=args

        hideBtnFab( )
        supportFragmentManager
            .beginTransaction()
            .add(R.id.contrainerMain, fragment)
            .addToBackStack(null)
            .commit()
    }
    private fun setupRecyclerView() {
        mAdapter= TiendaAdapter(mutableListOf(), this)

        mGridLayoutManager= GridLayoutManager(this, resources.getInteger(R.integer.dimen_recyclerView))
        getTiendas()
        mBinding.recyclerView.apply {
            setHasFixedSize(true)
            adapter=mAdapter
            layoutManager= mGridLayoutManager

        }
    }
    private fun getTiendas(){
        doAsync {
            val tiendas= TiendaApplication.database.tiendaDao().allTiendas()
            uiThread {
                mAdapter.setTiendas(tiendas)
            }
        }
    }

//---------Interface: OnClickListener---------------
    override fun onClick(idTienda: Long) {
        val args= Bundle()
        args.putLong(getString(R.string.keyArgs),idTienda)
        goToTiendaFragment(args)
    }
    override fun onFavorito(tienda: Tienda) {
        tienda.favorita= !tienda.favorita
        doAsync {
            TiendaApplication.database.tiendaDao().updateTienda(tienda)
            uiThread {
                mAdapter.update(tienda)
            }
        }
    }
    override fun onDelete(tienda: Tienda) {
        val opciones= resources.getStringArray(R.array.dialog_options)
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.ad_title )
            .setItems(opciones, DialogInterface.OnClickListener { dialog, which ->
                when(which){
                    opciones.indexOf("Eliminar")-> dialogEliminar(tienda)
                    opciones.indexOf("Llamar")-> dialogLlamar(tienda.telefono)
                    2-> dialogWeb(tienda.web)
                }
            }).show()
    }
    private fun dialogEliminar(tienda: Tienda){
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.ad_title)
            .setPositiveButton(R.string.ad_eliminar_ok, DialogInterface.OnClickListener { dialog, which ->
                doAsync {
                    TiendaApplication.database.tiendaDao().deleteTiienda(tienda)
                    uiThread {
                        mAdapter.delete(tienda)
                    }
                }
            })
            .setNegativeButton(R.string.ad_eliminar_no,null)
            .show()
    }
    private fun dialogLlamar(telefono: String){
       if(telefono.isEmpty())
           Toast.makeText(this, "esta tienda no tiene un nro de telefono", Toast.LENGTH_SHORT).show()
        else {
           val i = Intent().apply {
               action = Intent.ACTION_DIAL
               data = Uri.parse("tel:$telefono")
           }
           dialogIntet(i)
       }
    }
    private fun dialogWeb(url: String){
        if(url.isEmpty())
            Toast.makeText(this, "esta tienda no tiene un sitio web", Toast.LENGTH_SHORT).show()
        else {
            val i = Intent().apply {
                action = Intent.ACTION_VIEW
                data = Uri.parse(url)
            }
            dialogIntet(i)
        }
    }
    private fun dialogIntet(i:Intent){
        if(i.resolveActivity(packageManager)!=null)
            startActivity(i)
        else
            Toast.makeText(this, getString(R.string.msgResolve), Toast.LENGTH_SHORT).show()
    }
//--------------MainAux---------------

    override fun hideBtnFab(visible: Boolean) {
        if(visible)
            mBinding.btnFab.show()
        else
            mBinding.btnFab.hide()
    }
    override fun addTienda(tienda: Tienda) {
        mAdapter.add(tienda)
    }
    override fun updateTienda(tienda: Tienda) {
        mAdapter.update(tienda)
    }
}