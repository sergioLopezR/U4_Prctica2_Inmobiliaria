package mx.edu.ittepic.u4_prctica2_inmobiliaria;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BaseDatos extends SQLiteOpenHelper {

    public BaseDatos(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version); //SQLiteOpenHelper es el equibalente al phpMyAdmin
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Se ejecuta cuando la aplicacion se ejecuta en el celular
        //Sirve para construir en el SQLite que esta en celular las tablas que la APP requiere para funcionar.

        db.execSQL("CREATE TABLE PROPIETARIO(IDP INTEGER PRIMARY KEY NOT NULL, NOMBRE VARCHAR(200), DOMICILIO VARCHAR(500), TELEFONO VARCHAR(50))");//execSQL-----> Funciona para insert, create table, delete, update
        db.execSQL("CREATE TABLE INMUEBLE(IDINMUEBLE INTEGER PRIMARY KEY NOT NULL, DOMICILIO VARCHAR(200), PRECIOVENTA FLOAT, PRECIORENTA FLOAT, FECHATRANSACCION DATE, IDP INTEGER, FOREIGN KEY(IDP) REFERENCES PROPIETARIO(IDP))");
        //IDP INTEGER, FOREIGN KEY(IDP) REFERENCES PROPIETARIO(IDP)
        //IDP INTEGER CONSTRAINT FK_IDP_PRO REFERENCES PROPIETARIO(IDP)
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { //

    }
}
