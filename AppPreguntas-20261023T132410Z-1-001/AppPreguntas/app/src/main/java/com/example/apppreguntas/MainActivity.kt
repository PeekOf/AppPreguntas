package com.example.apppreguntas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

//Datos
data class Pregunta(
    val text: String,
    val imageResId: Int,
    val respuestaCorrecta: Boolean
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppPreguntas()
                }
            }
        }
    }
}

// Componente principal
@Composable
fun AppPreguntas() {
    // Pool de 5 preguntas
    val preguntas = listOf(
        Pregunta("El objetivo principal en una partida estándar de League of Legends es destruir el Nexo del equipo enemigo.", R.drawable.nexo, true),
        Pregunta("Derrotar al Barón Nashor otorga a tu equipo una mejora permanente de estadísticas para el resto de la partida.", R.drawable.nashor, false),
        Pregunta("El hechizo de invocador llamado \"Destello\" (Flash) permite a un campeón teletransportarse instantáneamente a cualquier estructura aliada en el mapa.", R.drawable.flash, false),
        Pregunta("En el mapa de la Grieta del Invocador, la posición de \"Jungla\" se caracteriza por obtener oro y experiencia derrotando monstruos neutrales en lugar de súbditos en los carriles.", R.drawable.jungla, true),
        Pregunta("La habilidad definitiva del campeón Teemo le permite colocar hongos venenosos e invisibles por todo el terreno de juego.", R.drawable.teemo, true)
    )

    var indice by remember { mutableStateOf(0) }
    var respuestaSeleccionada by remember { mutableStateOf<Boolean?>(null) }

    val preguntaActual = preguntas[indice]

    QuizScreen(
        pregunta = preguntaActual,
        respuestaSeleccionada = respuestaSeleccionada,
        enRespuestaSeleccionada = { respuesta -> respuestaSeleccionada = respuesta },
        onNextClicked = {
            // Avanzar y reiniciar si llega al final
            indice = (indice + 1) % preguntas.size
            // Borrar el mensaje al pasar de pregunta
            respuestaSeleccionada = null
        },
        onBackClicked = {
            // Retroceder
            if (indice >= 1){
                indice = (indice - 1)
                respuestaSeleccionada = null
            }else{
                indice = indice + (preguntas.size - 1)
                respuestaSeleccionada = null
            }
        }
    )
}

//UI de la pantalla
@Composable
fun QuizScreen(
    pregunta: Pregunta,
    respuestaSeleccionada: Boolean?,
    enRespuestaSeleccionada: (Boolean) -> Unit,
    onNextClicked: () -> Unit,
    onBackClicked: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Texto de la pregunta
        Text(text = pregunta.text, fontSize = 20.sp, modifier = Modifier.padding(top = 40.dp))

        // Imagen central
        Image(
            painter = painterResource(id = pregunta.imageResId),
            contentDescription = "Imagen de la pregunta",
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .padding(16.dp)
        )

        // Zona de botones True/False
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Mensaje de respuesta
            if (respuestaSeleccionada != null) {
                val esCorrecta = respuestaSeleccionada == pregunta.respuestaCorrecta
                val respuestaTexto = if (esCorrecta) {
                    "Has acertado, la respuesta correcta era ${pregunta.respuestaCorrecta}"
                } else {
                    "Lo siento! Has elegido $respuestaSeleccionada, pero la respuesta correcta era ${pregunta.respuestaCorrecta}"
                }
                val feedbackColor = if (esCorrecta) Color.Green else Color.Red

                Text(
                    text = respuestaTexto,
                    color = feedbackColor,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            // Botones True y False
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                BotonRespuesta(
                    text = "true",
                    respuesta = true,
                    respuestaCorrecta = pregunta.respuestaCorrecta,
                    respuestaSeleccionada = respuestaSeleccionada,
                    onClick = { enRespuestaSeleccionada(true) }
                )
                BotonRespuesta(
                    text = "false",
                    respuesta = false,
                    respuestaCorrecta = pregunta.respuestaCorrecta,
                    respuestaSeleccionada = respuestaSeleccionada,
                    onClick = { enRespuestaSeleccionada(false) }
                )
            }
        }

        // Botón Next y Back
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.SpaceEvenly
        ){
            Button(
                onClick = onNextClicked,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Icon(
                    painter = painterResource(R.drawable.baseline_arrow_forward_24),
                    contentDescription = "Siguiente"
                )
                Text("Siguiente")
            }

            Button(
                onClick = onBackClicked,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 35.dp)
            ){
                Icon(
                    painter = painterResource(R.drawable.baseline_arrow_back_24),
                    contentDescription = "Atras"
                )
                Text("Atras")
            }
        }
    }
}

// Componente para los botones de respuesta
@Composable
fun BotonRespuesta(
    text: String,
    respuesta: Boolean,
    respuestaCorrecta: Boolean,
    respuestaSeleccionada: Boolean?,
    onClick: () -> Unit
) {
    // Colores según la selección
    val buttonColor = if (respuestaSeleccionada != null) {
        if (respuesta == respuestaCorrecta) {
            Color.Green // La opción correcta siempre se pinta de verde
        } else{
            Color.Red // Si elegiste la incorrecta, se pinta de rojo
        }
    } else {
        MaterialTheme.colorScheme.primary // Color por defecto antes de elegir
    }

    Button(
        onClick = onClick,
        enabled = respuestaSeleccionada == null,// Deshabilitar después de responder
        colors = ButtonDefaults.buttonColors(
            containerColor = buttonColor,
            disabledContainerColor = buttonColor, // Mantiene el color cuando está deshabilitado
            contentColor = Color.White,
            disabledContentColor = Color.White
        )
    ) {
        Text(text)
    }
}