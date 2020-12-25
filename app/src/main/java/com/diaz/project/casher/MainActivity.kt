package com.diaz.project.casher

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v4.view.ViewPager
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.diaz.project.casher.db.Hutang
import com.diaz.project.casher.db.Record
import com.diaz.project.casher.utils.CurrencyFormat
import com.diaz.project.casher.utils.PagerAdapter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.add_dialog_layout.view.*
import kotlinx.android.synthetic.main.saldo_dialog_layout.view.*
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: MainViewModel
    private lateinit var adapter: PagerAdapter

    private var jumlahPengeluaran = 0
    private var jumlahPemasukan = 0
    private var jumlahHutang = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(main_toolbar)
        supportActionBar?.title = null

        if (savedInstanceState != null) {
            jumlahPemasukan = savedInstanceState.getInt("jumlahPemasukan")
            jumlahPengeluaran = savedInstanceState.getInt("jumlahPengeluaran")
            jumlahHutang = savedInstanceState.getInt("jumlahHutang")
        }

        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        viewModel.getJumlahPengeluaran()?.observe(this, Observer {
            if (it != null) {
                main_jumlah_pengeluaran.text = CurrencyFormat.convertAndFormat(it)
                jumlahPengeluaran = it
            } else {
                main_jumlah_pengeluaran.text = CurrencyFormat.convertAndFormat(0)
            }
        })

        viewModel.getJumlahPemasukan()?.observe(this, Observer {
            if (it != null) {
                main_jumlah_pemasukan.text = CurrencyFormat.convertAndFormat(it)
                jumlahPemasukan = it
            } else {
                main_jumlah_pemasukan.text = CurrencyFormat.convertAndFormat(0)
            }
        })

        viewModel.getJumlahHutang()?.observe(this, Observer {
            if (it != null) {
                jumlahHutang = it
            }
        })

        adapter = PagerAdapter(supportFragmentManager)
        main_view_pager.adapter = adapter
        main_view_pager.offscreenPageLimit = 3
        main_tab_layout.setupWithViewPager(main_view_pager)
        main_view_pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(p0: Int) {}

            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {
                if (p0 == 1) {
                    riwayat_fab.hide()
                    hutang_fab.show()
                } else {
                    riwayat_fab.show()
                    hutang_fab.hide()
                }
            }

            override fun onPageSelected(p0: Int) {
                if (p0 == 0) {
                    riwayat_fab.hide()
                    hutang_fab.show()
                } else {
                    riwayat_fab.show()
                    hutang_fab.hide()
                }
            }

        })

        riwayat_fab.setOnClickListener { showAddDataDialog("riwayat") }
        hutang_fab.setOnClickListener { showAddDataDialog("utang") }
    }

    override fun onSaveInstanceState(outState: Bundle?, outPersistentState: PersistableBundle?) {
        outState?.putInt("jumlahPemasukan", jumlahPemasukan)
        outState?.putInt("jumlahPengeluaran", jumlahPengeluaran)
        outState?.putInt("jumlahHutang", jumlahHutang)
        super.onSaveInstanceState(outState, outPersistentState)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when {
            item.itemId == R.id.action_reset -> showDialog()
            item.itemId == R.id.action_saldo -> showSaldoDialog()
            else -> {
                val intent = Intent(this@MainActivity, GraphActivity::class.java)
                startActivity(intent)
            }
        }

        return true
    }

    fun reduceValue(key: String, amount: Int) {

        when (key) {
            "pemasukan" -> jumlahPemasukan -= amount
            "pengeluaran" -> jumlahPengeluaran -= amount
            else -> jumlahHutang -= amount
        }
    }

    private fun showSaldoDialog() {
        val dialog = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.saldo_dialog_layout, null)
        
        dialogView.dialog_main_pemasukan.text = CurrencyFormat.convertAndFormat(jumlahPemasukan)
        dialogView.dialog_main_pengeluaran.text = CurrencyFormat.convertAndFormat(jumlahPengeluaran)
        dialogView.dialog_main_hutang.text = CurrencyFormat.convertAndFormat(jumlahHutang)
        dialogView.dialog_main_saldo.text =
            CurrencyFormat.convertAndFormat(jumlahPemasukan - (jumlahPengeluaran + jumlahHutang))
        dialog.setView(dialogView)
        dialog.setTitle(R.string.dialog_title_saldo)
        dialog.setCancelable(true)
        dialog.setPositiveButton("Close", null)
        dialog.show()
    }

    private fun showDialog() {
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle(getString(R.string.perhatian))
        dialog.setMessage(R.string.dialog_message)
        dialog.setCancelable(true)
        dialog.setPositiveButton("Yes") { _, _ ->
            viewModel.deleteAllRecord()
            viewModel.deleteAllHutang()

            jumlahPemasukan = 0
            jumlahPengeluaran = 0
            jumlahHutang = 0
        }
        dialog.setNegativeButton("Cancel") { innerDialog, _ ->
            innerDialog.dismiss()
        }

        dialog.show()
    }

    private fun showAddDataDialog(key: String) {
        val dialog = this.let { AlertDialog.Builder(it) }
        val dialogView = layoutInflater.inflate(R.layout.add_dialog_layout, null)

        when (key) {
            "utang" -> dialogView.dialog_checkbox_masukan.visibility = View.GONE
        }

        dialog.setView(dialogView)
        dialog.setCancelable(true)
        dialog.setPositiveButton(R.string.dialog_simpan) { innerDialog, _ ->
            val isPemasukan = if (dialogView.dialog_checkbox_masukan.isChecked) {
                "pemasukan"
            } else {
                "pengeluaran"
            }

            if (!dialogView.dialog_judul.text.isBlank() && !dialogView.dialog_uang.text.isBlank()) {
                val jumlahPemasukan = dialogView.dialog_uang.text.toString()
                val date = SimpleDateFormat(getString(R.string.date_pattern))
                if (key == "riwayat") {
                    val record = Record(
                        0,
                        dialogView.dialog_judul.text.toString(),
                        jumlahPemasukan.toInt(),
                        date.format(Calendar.getInstance().time),
                        isPemasukan
                    )

                    viewModel.insertRecord(record)
                } else {
                    val hutang = Hutang(
                        0,
                        dialogView.dialog_judul.text.toString(),
                        jumlahPemasukan.toInt(),
                        date.format(Calendar.getInstance().time)
                    )

                    viewModel.insertHutang(hutang)
                }

                innerDialog.dismiss()
            } else {
                Toast.makeText(this, R.string.toast_isi_kolom, Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }
}
