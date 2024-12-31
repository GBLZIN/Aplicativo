package com.example.drinksync

import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.AlarmClock
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.drinksync.databinding.ActivityMainBinding
import com.example.drinksync.model.CalcularIngestaoDiaria
import java.text.NumberFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var calc = CalcularIngestaoDiaria()
    private var result = 0.0

    var hour = -1
    var minute = -1
    var isCalculated = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        supportActionBar?.hide()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btCalcular.setOnClickListener {
            calcular()
        }

        binding.btDefinirLembrete.setOnClickListener {
            definirHorario()
        }

        binding.btDefinirAlarme.setOnClickListener {
            criarAlarme()
        }

        binding.icRedefinir.setOnClickListener {
            resetarTudo()
        }

        binding.editPeso.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
                if (binding.editIdade.text.isNullOrEmpty()) {
                    binding.editIdade.requestFocus()
                } else {
                    calcular()
                }
                true
            } else {
                false
            }
        }

        binding.editIdade.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
                if (binding.editPeso.text.isNullOrEmpty()) {
                    binding.editPeso.requestFocus()
                } else {
                    calcular()
                }
                true
            } else {
                false
            }
        }
    }

    private fun calcular() {
        val peso = binding.editPeso.text.toString()
        val idade = binding.editIdade.text.toString()

        if (peso.isEmpty()) {
            Toast.makeText(this, "Por favor, insira seu peso!", Toast.LENGTH_SHORT).show()
            return
        }
        if (idade.isEmpty()) {
            Toast.makeText(this, "Por favor, insira sua idade!", Toast.LENGTH_SHORT).show()
            return
        }

        val pesoDouble = peso.toDouble()
        val idadeInt = idade.toInt()
        calc.CalcularTotalMl(pesoDouble, idadeInt)
        result = calc.ResultadoMl()

        val formatador = NumberFormat.getNumberInstance(Locale("pt", "BR"))
        binding.txtResultadoMl.text = formatador.format(result) + " ml"
        closeKeyboard()

        isCalculated = true
    }

    private fun definirHorario() {
        val calendario = Calendar.getInstance()
        hour = calendario.get(Calendar.HOUR_OF_DAY)
        minute = calendario.get(Calendar.MINUTE)

        TimePickerDialog(this, { _, h, m ->
            hour = h
            minute = m
            binding.txtHora.text = if (h < 10) "0$h" else "$h"
            binding.txtMinutos.text = if (m < 10) "0$m" else "$m"
        }, hour, minute, true).show()
    }

    private fun criarAlarme() {
        if (!isCalculated) {
            Toast.makeText(this, "Por favor, preencha o seu peso e sua idade!", Toast.LENGTH_SHORT).show()
            return
        }

        if (hour == -1 || minute == -1) {
            Toast.makeText(this, "Defina o horário clicando no botão 'Lembrete'!", Toast.LENGTH_SHORT).show()
            return
        }

        val intent = Intent(AlarmClock.ACTION_SET_ALARM).apply {
            putExtra(AlarmClock.EXTRA_HOUR, hour)
            putExtra(AlarmClock.EXTRA_MINUTES, minute)
            putExtra(AlarmClock.EXTRA_MESSAGE, "Hora de beber água!")
        }

        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else {
            Toast.makeText(this, "Nenhum app de alarme foi encontrado!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun resetarTudo() {
        AlertDialog.Builder(this)
            .setTitle("Redefinir")
            .setMessage("Tem certeza que deseja limpar todos os campos?")
            .setPositiveButton("Sim") { _, _ ->
                binding.editPeso.setText("")
                binding.editIdade.setText("")
                binding.txtHora.text = "00"
                binding.txtMinutos.text = "00"
                binding.txtResultadoMl.text = ""
                hour = -1
                minute = -1
                isCalculated = false
                Toast.makeText(this, "Tudo foi apagado!", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Não", null)
            .show()
    }
    private fun closeKeyboard() {
        val view = currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
            view.clearFocus()
        }
    }
}
