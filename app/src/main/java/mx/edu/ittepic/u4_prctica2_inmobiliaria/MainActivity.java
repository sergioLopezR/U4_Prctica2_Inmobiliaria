package mx.edu.ittepic.u4_prctica2_inmobiliaria;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    EditText identificacion, nombre, domicilio, telefono;
    Button guardar, consultar, eliminar, modificar, inmuebles;
    BaseDatos base;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        identificacion = findViewById(R.id.identificacion);
        nombre = findViewById(R.id.nombre);
        domicilio = findViewById(R.id.domicilio);
        telefono = findViewById(R.id.telefono);

        guardar = findViewById(R.id.guardar);
        consultar = findViewById(R.id.consultar);
        eliminar = findViewById(R.id.eliminar);
        modificar = findViewById(R.id.modificar);
        inmuebles = findViewById(R.id.inmuebles);

        //Asignarle memoria (new) y configuracion
        base = new BaseDatos(this, "inmobiliaria", null, 1);

        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (identificacion.length() == 0 || nombre.length()==0 || domicilio.length() == 0 || telefono.length() == 0){
                    Toast.makeText(MainActivity.this, "Favor de llenar todos los campos", Toast.LENGTH_LONG).show();
                }else{
                    guardarDatos();
                }
            }
        });

        consultar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pedirID(1);//Contendra el AlertDialog
            }
        });

        eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pedirID(2);
            }
        });

        modificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (modificar.getText().toString().startsWith("CONFIRMAR ACTUALIZACION")){
                    invocarConfirmacionActualizacion();
                }else{
                    pedirID(3);
                }
            }
        });

        inmuebles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pantalla = new Intent(MainActivity.this, Main2Activity.class);
                startActivity(pantalla);
            }
        });
    }

    private void guardarDatos() {
        try{
            SQLiteDatabase tabla = base.getWritableDatabase();

            String SQL = "INSERT INTO PROPIETARIO VALUES(%1, '%2', '%3', '%4')";
            SQL = SQL.replace("%1", identificacion.getText().toString());
            SQL = SQL.replace("%2", nombre.getText().toString());
            SQL = SQL.replace("%3", domicilio.getText().toString());
            SQL = SQL.replace("%4", telefono.getText().toString());

            tabla.execSQL(SQL);

            Toast.makeText(this, "Se guardo exitosamente su registro con el id: "+identificacion.getText().toString(), Toast.LENGTH_LONG).show();

            identificacion.setText("");
            nombre.setText("");
            domicilio.setText("");
            telefono.setText("");

            tabla.close();

        }catch (SQLiteException e){
            Toast.makeText(this, "ERROR: No se pudo guardar su registro", Toast.LENGTH_LONG).show();
        }
    }


    private void pedirID(final int origen) {

        final EditText pidoID = new EditText(this);
        pidoID.setInputType(InputType.TYPE_CLASS_NUMBER);
        pidoID.setHint("VALOR ENTERO MAYOR DE 0");

        String mensaje = "";
        String mensajeBoton = "";

        if (origen==1){
            mensaje = "ESCRIBA EL ID A BUSCAR";
            mensajeBoton = "BUSCAR";
        }
        if (origen==2){
            mensaje = "ESCRIBA EL ID QUE SE DESEA ELIMINAR";
            mensajeBoton = "ELIMINAR";
        }
        if (origen==3){
            mensaje = "ESCRIBA EL ID A MODIFICAR";
            mensajeBoton = "MODIFICAR";
        }

        AlertDialog.Builder alerta = new AlertDialog.Builder(this);
        alerta.setTitle("ATENCION").setMessage(mensaje).setView(pidoID).setPositiveButton(mensajeBoton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (pidoID.getText().toString().isEmpty()){
                    Toast.makeText(MainActivity.this, "DEBES ESCRIBIR VALOR", Toast.LENGTH_LONG).show();
                    return;
                }
                buscarDato(pidoID.getText().toString(), origen);
                dialog.dismiss();
            }
        }).setNegativeButton("CANCELAR", null).show();

    }


    private void buscarDato(String idABuscar, int origen){
        try{
            SQLiteDatabase tabla = base.getReadableDatabase();

            String SQL = "SELECT * FROM PROPIETARIO WHERE IDP="+idABuscar;

            Cursor resultado = tabla.rawQuery(SQL, null);

            if (resultado.moveToFirst()){
                //Si hay resultado
                if (origen==2){
                    //Esto significa que se consulto para borrar
                    String datos = idABuscar+"&"+resultado.getString(1)+"&"+resultado.getString(2)+"&"+resultado.getString(3);
                    invocarConfirmacionEliminacion(datos);
                    return;
                }

                identificacion.setText(resultado.getString(0));
                nombre.setText(resultado.getString(1));
                domicilio.setText(resultado.getString(2));
                telefono.setText(resultado.getString(3));

                if (origen==3){
                    //MODIFICAR
                    guardar.setEnabled(false);
                    consultar.setEnabled(false);
                    eliminar.setEnabled(false);
                    inmuebles.setEnabled(false);
                    modificar.setText("CONFIRMAR ACTUALIZACION");
                    identificacion.setEnabled(false);
                }
            }else{
                //No hay!
                Toast.makeText(this, "ERROR: No se pudo buscar el registro", Toast.LENGTH_LONG).show();
            }
            tabla.close();

        }catch (SQLiteException e){
            Toast.makeText(this, "ERROR: NO SE ENCONTRO RESULTADO", Toast.LENGTH_LONG).show();
        }

    }

    private void invocarConfirmacionEliminacion(String datos) {

        String cadenaDatos[] = datos.split("&");
        final String id = cadenaDatos[0];
        String nombre = cadenaDatos[1];
        String domicilio = cadenaDatos[2];
        String telefono = cadenaDatos[3];

        AlertDialog.Builder alerta = new AlertDialog.Builder(this);
        alerta.setTitle("ATENCION").setMessage("Deseas eliminar \nUsuario: "+id+"\nNombre: "+nombre+" \nDomicilio: "+domicilio+" \nTelefono: "+telefono+" ?").setPositiveButton("SI", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                eliminarDato(id);
                dialog.dismiss();
            }
        }).setNegativeButton("NO", null).show();
    }

    private void eliminarDato(String id) {
        try{
            SQLiteDatabase tabla = base.getWritableDatabase();

            String SQL = "DELETE FROM PROPIETARIO WHERE IDP="+id;

            tabla.execSQL(SQL);

            identificacion.setText("");
            nombre.setText("");
            domicilio.setText("");
            telefono.setText("");

            Toast.makeText(this, "Eliminado correctamente", Toast.LENGTH_LONG).show();
            tabla.close();

        }catch (SQLiteException e){
            Toast.makeText(this, "ERROR: NO SE PUDO ELIMINAR", Toast.LENGTH_LONG).show();
        }
    }

    private void invocarConfirmacionActualizacion() {
        AlertDialog.Builder confir = new AlertDialog.Builder(this);

        confir.setTitle("IMPORTANTE").setMessage("¿Estas seguro que deseas aplicar los cambios?").setPositiveButton("SI", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                aplicarActualizacion();
                dialog.dismiss();
            }
        }).setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                habilitarBotonesYLimpiarCampos();
                dialog.cancel();
            }
        }).show();
    }

    private void aplicarActualizacion(){
        try{
            SQLiteDatabase tabla = base.getWritableDatabase();
            String SQL = "UPDATE PROPIETARIO SET NOMBRE='"+nombre.getText().toString()+"', DOMICILIO='"+domicilio.getText().toString()+"', TELEFONO='"
                    +telefono.getText().toString()+"' WHERE IDP=" +identificacion.getText().toString();
            tabla.execSQL(SQL);
            tabla.close();
            Toast.makeText(this, "Se actualizaron correctamente los datos", Toast.LENGTH_LONG).show();
        }catch (SQLiteException e){
            Toast.makeText(this, "ERROR: NO SE PUDO ACTUALIZAR", Toast.LENGTH_LONG).show();
        }

        habilitarBotonesYLimpiarCampos();
    }

    private void habilitarBotonesYLimpiarCampos(){
        identificacion.setText("");
        nombre.setText("");
        domicilio.setText("");
        telefono.setText("");
        guardar.setEnabled(true);
        consultar.setEnabled(true);
        eliminar.setEnabled(true);
        inmuebles.setEnabled(true);
        modificar.setText("ACTUALIZAR");
        identificacion.setEnabled(true);
    }
}
