package com.example.estruch18.proyecto5d;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends ActionBarActivity {
    //Atributos de la clase
    private Button btnAddEvento;
    private ListView listaEventos;
    private ArrayList<String> eventos;
    private ArrayAdapter<String> adaptador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Ejecución de métodos
        declaracionViews();
        crearCalendario();
    }

    public void declaracionViews(){
        listaEventos = (ListView)findViewById(R.id.lvEventos);
        btnAddEvento = (Button)findViewById(R.id.btnAddEvento);
        eventos = loadEventos();
        adaptador = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, eventos);
        listaEventos.setAdapter(adaptador);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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

    //Método cuya finalidad es la creación de un calendario
    public void crearCalendario(){
        //Si getIdCalendar devuelve -1, no existe el calendario, por tanto, se procede a la creación.
        if(this.getIdCalendar() == -1){
            //Creación de un "bundle" ContentValues
            ContentValues values = new ContentValues();
            //Añadimos las características del calendario
            values.put(CalendarContract.Calendars.ACCOUNT_NAME, "Ivan");
            values.put(CalendarContract.Calendars.ACCOUNT_TYPE, CalendarContract.ACCOUNT_TYPE_LOCAL);
            values.put(CalendarContract.Calendars.NAME, "Prueba de calendario");
            values.put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, "Prueba de calendario");
            values.put(CalendarContract.Calendars.CALENDAR_COLOR, 0xffff0000);
            values.put(CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL, CalendarContract.Calendars.CAL_ACCESS_OWNER);
            values.put(CalendarContract.Calendars.OWNER_ACCOUNT, "estruch95.b@gmail.com");
            values.put(CalendarContract.Calendars.CALENDAR_TIME_ZONE, "Europe/Madrid");
            values.put(CalendarContract.Calendars.SYNC_EVENTS, 1);

            Uri.Builder calendarBuilder = CalendarContract.Calendars.CONTENT_URI.buildUpon();
            calendarBuilder.appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, "Ivan");
            calendarBuilder.appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, CalendarContract.ACCOUNT_TYPE_LOCAL);
            calendarBuilder.appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true");
            Uri uri = getContentResolver().insert(calendarBuilder.build(), values);

            //Información
            Toast.makeText(getApplicationContext(), "Se ha creado un calendario", Toast.LENGTH_SHORT).show();
        }
        else{
            loadEventos();
        }
    }

    //Método encargado de leer eventos
    public ArrayList<String> loadEventos(){
        //ArrayList de eventos cargados
        ArrayList<String> eventos = new ArrayList<String>();
        //Datos que queremos cargar sobre dichos eventos
        String[] projection = new String[]{CalendarContract.Events.TITLE, CalendarContract.Events.DESCRIPTION};
        //Condiciones de búsqueda o query
        String[] selArgs = new String[]{"Ivan", CalendarContract.ACCOUNT_TYPE_LOCAL};
        //Implementación
        String selection = " ((" + CalendarContract.Calendars.ACCOUNT_NAME + " = ?) AND (" + CalendarContract.Calendars.ACCOUNT_TYPE + " = ?))";

        //Cursor y recorrido
        Cursor cursor = getContentResolver().query(CalendarContract.Events.CONTENT_URI, projection, selection, selArgs, null);
        if(cursor != null && cursor.moveToFirst()){
            do{
                String titulo = cursor.getString(0);
                String descripcion= cursor.getString(1);

                String evento = "Nombre: "+titulo+"    Descripción: "+descripcion;
                eventos.add(evento);
            }while (cursor.moveToNext());
            cursor.close();
        }
        return eventos;
    }

    //Listener
    public void accionBtnAddEvento(View v){
        Intent actAddEvento = new Intent(getApplicationContext(), AddEvento.class);
        startActivity(actAddEvento);
    }
}
