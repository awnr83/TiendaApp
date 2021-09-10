package com.moappdev.solutions.tiendaapp

import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.moappdev.solutions.tiendaapp.database.Tienda
import com.moappdev.solutions.tiendaapp.database.TiendaApplication
import com.moappdev.solutions.tiendaapp.databinding.FragmentTiendaBinding
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class TiendaFragment : Fragment() {

    private lateinit var mBinding: FragmentTiendaBinding
    private lateinit var mActivity:MainActivity
    private var mEditMode: Boolean= false
    private var mTiendaEntity: Tienda?=null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding= FragmentTiendaBinding.inflate(inflater, container, false)

        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mActivity = activity as MainActivity
        mActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setHasOptionsMenu(true)

        val id= arguments?.getLong(getString(R.string.keyArgs),0)

        if (id!=null && id != 0L) {
            mEditMode=true
            mActivity.title = getString(R.string.fragment_name_edit)
            doAsync {
                mTiendaEntity= TiendaApplication.database.tiendaDao().getTienda(id)
                uiThread {
                    mBinding.apply {
                        etName.setText(mTiendaEntity!!.nombre)
                        etTelefono.setText(mTiendaEntity!!.telefono)
                        etDireccion.setText(mTiendaEntity!!.direccion)
                        etWeb.setText(mTiendaEntity!!.web)
                        etImg.setText(mTiendaEntity!!.imagen)
                        Glide.with(mActivity)
                            .load(mBinding.etImg.text.toString())
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .centerCrop()
                            .into(mBinding.imgPhoto)
                    }
                }
            }
        }else {
            mActivity.title = getString(R.string.fragment_name)

            mBinding.etImg.addTextChangedListener {
                Glide.with(mActivity)
                    .load(mBinding.etImg.text.toString())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .centerCrop()
                    .into(mBinding.imgPhoto)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.menu_guardar, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            android.R.id.home -> {
                ocultarTeclado()
                mActivity.onBackPressed()
                true
            }
            R.id.btnMnuGuardar->{
                with(mBinding){
                    var tienda=Tienda(
                        nombre= etName.text.toString().trim(),
                        direccion = etDireccion.text.toString().trim(),
                        telefono = etTelefono.text.toString().trim(),
                        web = etWeb.text.toString().trim(),
                        imagen = etImg.toString().trim())

                    doAsync {
                        if (mEditMode){
                            TiendaApplication.database.tiendaDao().updateTienda(tienda)
                            uiThread {
                                mActivity.updateTienda(tienda)
                                //Snackbar.make(mBinding.root,getString(R.string.msgModificado),Snackbar.LENGTH_SHORT).show()
                                Toast.makeText(mActivity, getString(R.string.msgModificado), Toast.LENGTH_SHORT).show()
                                mActivity.onBackPressed()
                            }
                        }else{
                            tienda.id = TiendaApplication.database.tiendaDao().addTienda(tienda)
                            uiThread {
                                mActivity.addTienda(tienda)
                                //Snackbar.make(mBinding.root,getString(R.string.msgGuardado),Snackbar.LENGTH_SHORT).show()
                                Toast.makeText(mActivity, getString(R.string.msgGuardado), Toast.LENGTH_SHORT).show()
                                mActivity.onBackPressed()
                            }
                        }
                    }
                }
                true
            }
            else-> return super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroyView() {
        ocultarTeclado()

        super.onDestroyView()
    }

    override fun onDestroy() {
        mActivity.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        mActivity.title=getString(R.string.app_name)
        mActivity.hideBtnFab(true)
        setHasOptionsMenu(false)
        ocultarTeclado()

        super.onDestroy()
    }

//---------funciones------------
    private fun ocultarTeclado(){
    val imm = mActivity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
    if (view != null)
        imm?.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
    }
}
