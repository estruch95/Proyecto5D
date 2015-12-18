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

    public void añadirEvento(String titulo, String descripcion, long fechaInicio, long fechaFin){
        if(getIdCalendar() == -1){
            Toast.makeText(getApplicationContext(), "No existen calendarios", Toast.LENGTH_SHORT).show();
        }
        else{
            //Creación de un "bundle" ContentValues
            ContentValues values = new ContentValues();

            //Añadimos las características del evento
            values.put(CalendarContract.Events.CALENDAR_ID, getIdCalendar());
            values.put(CalendarContract.Events.EVENT_TIMEZONE, "Europe/Madrid");
            values.put(CalendarContract.Events.TITLE, titulo);
            values.put(CalendarContract.Events.DESCRIPTION, descripcion);
            values.put(CalendarContract.Events.DTSTART, fechaInicio);
            values.put(CalendarContract.Events.DTEND, fechaFin);

            Uri.Builder eventBuilder = CalendarContract.Events.CONTENT_URI.buildUpon();
            eventBuilder.appendQueryParameter(CalendarContract.Events.ACCOUNT_TYPE, CalendarContract.ACCOUNT_TYPE_LOCAL);
            Uri uri = getContentResolver().insert(eventBuilder.build(), values);

            //Información
            Toast.makeText(getApplicationContext(), "Se ha creado un evento", Toast.LENGTH_SHORT).show();

            Intent actMain = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(actMain);
        }
    }

    //Listener del botón addEvento
    public void accionBtnAddEvento(View v){
        //Calendario
        Calendar start = Calendar.getInstance();

        long startMillis;
        long endMillis;

        //Datos que interesa capturar
        //TÍTULO
        String tituloStr = titulo.getText().toString();

        //DESCRIPCIÓN
        String descripcionStr = descripcion.getText().toString();

        //FECHA INICIO (ANYO, MES, DIA, HORA, MINUTO)
        String[] fechaInicioArray  = fInicio.getText().toString().split("/");
        String[] fechaFinalArray  = fFin.getText().toString().split("/");

        //HORA (HORA-MINUTO)
        String[] horaYminutosInicio = hInicio.getText().toString().split(":");
        String[] horaYminutosFinal = hFin.getText().toString().split(":");

        start.set(Integer.parseInt(fechaInicioArray[2]), Integer.parseInt(fechaInicioArray[1]), Integer.parseInt(fechaInicioArray[0]), Integer.parseInt(horaYminutosInicio[0]), Integer.parseInt(horaYminutosInicio[1]));
        startMillis = start.getTimeInMillis();

        Calendar end = Calendar.getInstance();
        end.set(Integer.parseInt(fechaFinalArray[2]), Integer.parseInt(fechaFinalArray[1]), Integer.parseInt(fechaFinalArray[0]), Integer.parseInt(horaYminutosFinal[0]), Integer.parseInt(horaYminutosFinal[1]));
        endMillis = end.getTimeInMillis();

        //Llamada al método añadirEvento()
        añadirEvento(tituloStr, descripcionStr, startMillis, endMillis);
    }

}
