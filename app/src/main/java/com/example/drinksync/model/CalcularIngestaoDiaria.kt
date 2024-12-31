package com.example.drinksync.model

class CalcularIngestaoDiaria {
    private val ML_JOVEM = 40.0
    private val ML_ADULTO = 35.0
    private val ML_VELHO = 30.0
    private val ML_IDOSO = 25.0

    private var resultadoMl = 0.0
    private var resultado_total_ml = 0.0

    fun CalcularTotalMl(peso: Double, idade: Int){

        if (idade <= 17){
        resultadoMl = peso * ML_JOVEM
        resultado_total_ml = resultadoMl
        }else if (idade <= 55){
            resultadoMl = peso * ML_ADULTO
            resultado_total_ml = resultadoMl
        }else if (idade <= 65) {
            resultadoMl = peso * ML_VELHO
            resultado_total_ml = resultadoMl
        }else{
            (idade <= ML_IDOSO)
            resultadoMl = peso * ML_ADULTO
            resultado_total_ml = resultadoMl

        }
    }

    fun ResultadoMl(): Double {
        return resultado_total_ml
    }
}