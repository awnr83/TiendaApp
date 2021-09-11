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
import com.google.android.material.textfield.TextInputLayout
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
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
                   with(mBinding) {
                        etName.setText(mTiendaEntity!!.nombre)
                        etTelefono.setText(mTiendaEntity!!.telefono)
                        etDireccion.setText(mTiendaEntity!!.direccion)
                        etWeb.setText(mTiendaEntity!!.web)
                        etImg.setText(mTiendaEntity!!.imagen)
                    }
                }
            }
        }else {
            mActivity.title = getString(R.string.fragment_name)
        }

        mBinding.etImg.addTextChangedListener {
            validateDatos(mBinding.tilImg)
            Glide.with(mActivity)
                .load(mBinding.etImg.text.toString())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .into(mBinding.imgPhoto)
        }
        mBinding.etName.addTextChangedListener { validateDatos(mBinding.tilName) }
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

//------------menu--------------
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
                if(validateDatos(mBinding.tilImg, mBinding.tilName)) {
                    with(mBinding) {
                        if (mTiendaEntity == null)
                            mTiendaEntity = Tienda(
                                nombre = "",
                                telefono = "",
                                direccion = "",
                                web = "",
                                imagen = ""
                            )

                        with(mTiendaEntity!!) {
                            nombre = etName.text.toString().trim()
                            direccion = etDireccion.text.toString().trim()
                            telefono = etTelefono.text.toString().trim()
                            web = etWeb.text.toString().trim()
                            imagen = etImg.text.toString().trim()
                        }
                        doAsync {
                            ocultarTeclado()
                            if (mEditMode) {
                                TiendaApplication.database.tiendaDao().updateTienda(mTiendaEntity!!)
                                uiThread {
                                    mActivity.updateTienda(mTiendaEntity!!)
                                    Toast.makeText(
                                        mActivity,
                                        getString(R.string.msgModificado),
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()            //Snackbar.make(mBinding.root,getString(R.string.msgModificado),Snackbar.LENGTH_SHORT).show()
                                    //mActivity.onBackPressed()
                                }
                            } else {
                                mTiendaEntity!!.id = TiendaApplication.database.tiendaDao()
                                    .addTienda(mTiendaEntity!!)
                                uiThread {
                                    mActivity.addTienda(mTiendaEntity!!)
                                    Toast.makeText(
                                        mActivity,
                                        getString(R.string.msgGuardado),
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()       //Snackbar.make(mBinding.root,getString(R.string.msgGuardado),Snackbar.LENGTH_SHORT).show()
                                    mActivity.onBackPressed()
                                }
                            }
                        }
                    }
                }else
                    Toast.makeText(mActivity, getString(R.string.msgDatos),Toast.LENGTH_SHORT).show()
                true
            }
            else-> return super.onOptionsItemSelected(item)
        }
    }

//---------funciones------------
    private fun ocultarTeclado(){
    val imm = mActivity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
    if (view != null)
        imm?.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
    }
    private fun validateDatos(vararg til: TextInputLayout): Boolean {
        var ok=true
        til.forEach {
            if(it.editText?.text.isNullOrEmpty()){
                it.error=getString(R.string.helper_requerido)
                it.editText?.requestFocus()
                ok=false
            }else  it.isErrorEnabled=false
        }
        return ok
    }
}