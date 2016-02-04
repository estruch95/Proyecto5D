package com.example.estruch18.proyecto5d;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Calendar;

public class AddEvento extends ActionBarActivity {
    //Atributos de la clase
    private EditText titulo, descripcion, fInicio, fFin, hInicio, hFin;
    private Button btnAddEvento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_evento);

        //Ejecución de métodos
        declaracionViews();
    }

    public void declaracionViews(){
        titulo = (EditText)findViewById(R.id.etTitulo);
        descripcion = (EditText)findViewById(R.id.etDescripcion);
        fInicio = (EditText)findViewById(R.id.etFinicio);
        fFin = (EditText)findViewById(R.id.etFfinal);
        hInicio = (EditText)findViewById(R.id.etHinicial);
        hFin = (EditText)findViewById(R.id.etHfinal);
        btnAddEvento = (Button)findViewById(R.id.btnAddEvento);
    }

    //Método encargado de obtener el ID del calendario en caso de existir
    public long getIdCalendar(){
        //Pasos explicados anteriormente 
        String[] projection = new String[]{CalendarContract.Calendars._ID};
        String[] selArgs = new String[]{"Ivan", CalendarContract.ACCOUNT_TYPE_LOCAL};
        String selection = " ((" + CalendarContract.Calendars.ACCOUNT_NAME + " = ?) AND (" + CalendarContract.Calendars.ACCOUNT_TYPE + " = ?))";
        Cursor cursor = getContentResolver().query(CalendarContract.Calendars.CONTENT_URI, projection, selection, selArgs, null);
        if(cursor.moveToFirst()){
            return cursor.getLong(0);
        }
        else{
            return -1;
        }
    }

    /*Método cuya función es añadir un evento a partir de los datos recibidos por parámetro en caso de existir un 
    calendario existente, de lo contrario muestra un Toast*/
    public void añadirEvento(String titulo, String descripcion, long fechaInicio, long fechaFin){
        if(getIdCalendar() == -1){
            Toast.makeText(getApplicationContext(), "No existen calendarios", Toast.LENGTH_SHORT).show();
        }
        else{
            //Creación de un "bundle" ContentValues
            ContentValues values = new ContentValues();

            //Añadimos las características del evento que deseamos añadir a nuestro calendario
            //Le indicamos que el ID del nuevo evento va a ser el del calendario existente
            values.put(CalendarContract.Events.CALENDAR_ID, getIdCalendar());
            //Zona horaria del evento
            values.put(CalendarContract.Events.EVENT_TIMEZONE, "Europe/Madrid");
            //Título del evento
            values.put(CalendarContract.Events.TITLE, titulo);
            //Descripción del evento
            values.put(CalendarContract.Events.DESCRIPTION, descripcion);
            //Fecha inicial del evento
            values.put(CalendarContract.Events.DTSTART, fechaInicio);
            //Fecha final del evento
            values.put(CalendarContract.Events.DTEND, fechaFin);

            //Añadimos los valores anteriores a la dirección del ContentProvider de eventos
            Uri.Builder eventBuilder = CalendarContract.Events.CONTENT_URI.buildUpon();
            //Append tipo de cuenta
            eventBuilder.appendQueryParameter(CalendarContract.Events.ACCOUNT_TYPE, CalendarContract.ACCOUNT_TYPE_LOCAL);
            //Insertar el nuevo evento creado en nuestro calendario existente
            Uri uri = getContentResolver().insert(eventBuilder.build(), values);

            //Información
            Toast.makeText(getApplicationContext(), "Se ha creado un evento", Toast.LENGTH_SHORT).show();

            Intent actMain = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(actMain);
        }
    }

    //Listener del botón addEvento
    public void accionBtnAddEvento(View v){
        //Obtenemos la instancia del calendario ya existente
        Calendar start = Calendar.getInstance();
        
        long startMillis;
        long endMillis;

        //Capturamos los datos de los campos de texto y posteriormente llamaremos al método añadirEvento();
        //TÍTULO
        String tituloStr = titulo.getText().toString();

        //DESCRIPCIÓN
        String descripcionStr = descripcion.getText().toString();

        //FECHA INICIO/FINAL (ANYO, MES, DIA)
        String[] fechaInicioArray  = fInicio.getText().toString().split("/");
        String[] fechaFinalArray  = fFin.getText().toString().split("/");

        //HORA INICIO/FIN (HORA-MINUTO)
        String[] horaYminutosInicio = hInicio.getText().toString().split(":");
        String[] horaYminutosFinal = hFin.getText().toString().split(":");

        //Recogemos el DIA, MES Y AÑO de FECHA INICIO, realizamos un casting a INTEGER y lo montamos en formato de fecha 
        start.set(Integer.parseInt(fechaInicioArray[2]), Integer.parseInt(fechaInicioArray[1]), Integer.parseInt(fechaInicioArray[0]), Integer.parseInt(horaYminutosInicio[0]), Integer.parseInt(horaYminutosInicio[1]));
        startMillis = start.getTimeInMillis();

        Calendar end = Calendar.getInstance();
        //Recogemos el DIA, MES Y AÑO de FECHA FINAL, realizamos un casting a INTEGER y lo montamos en formato de fecha 
        end.set(Integer.parseInt(fechaFinalArray[2]), Integer.parseInt(fechaFinalArray[1]), Integer.parseInt(fechaFinalArray[0]), Integer.parseInt(horaYminutosFinal[0]), Integer.parseInt(horaYminutosFinal[1]));
        endMillis = end.getTimeInMillis();

        //Llamada al método añadirEvento() pasandole los datos capturados
        añadirEvento(tituloStr, descripcionStr, startMillis, endMillis);
    }

}
