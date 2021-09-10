package com.moappdev.solutions.tiendaapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.GridLayoutManager
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

        supportFragmentManager
            .beginTransaction()
            .add(R.id.contrainerMain, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun setupRecyclerView() {
        mAdapter= TiendaAdapter(mutableListOf(), this)
        mGridLayoutManager= GridLayoutManager(this, 2)
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
        doAsync {
            TiendaApplication.database.tiendaDao().deleteTiienda(tienda)
            uiThread {
                mAdapter.delete(tienda)
            }
        }
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

    }
}