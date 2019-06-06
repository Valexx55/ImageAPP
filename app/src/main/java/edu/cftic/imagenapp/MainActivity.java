package edu.cftic.imagenapp;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final String []PERMISOS = {Manifest.permission.CAMERA};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityCompat.requestPermissions(this, PERMISOS, 100);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0]== PackageManager.PERMISSION_GRANTED)
        {
            //va bien
        }
    }

/*
    private void cargarFoto ()
    {
        String str_foto = Preferencias.leerFoto(this);
        if (str_foto!=null)
        {
            try{
                Uri uri = Uri.parse(str_foto);
                //getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);//.takePersistableUriPermission(uri,Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION)), uri);
                ImageView imageView = (ImageView)findViewById(R.id.imageView);
                imageView.setImageBitmap(bitmap);
            }catch (Throwable t)
            {
                Log.e("MIAPP", "ERROR AL CARGAR LA FOTO", t);
            }

        }
    }

    */


    public void tomarFoto (View v)
    {
        Log.d("MIAPP", "QUIERO TOMAR UNA FOTO");
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, 500);
        }
    }

    public void seleccionarFoto (View v)
    {
        Log.d("MIAPP", "QUIERO TOMAR UNA FOTO");
        Intent intentpidefoto = new Intent ();
        intentpidefoto.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        intentpidefoto.setAction(Intent.ACTION_OPEN_DOCUMENT);
        intentpidefoto.setType("image/*");//TIPO MIME

        startActivityForResult(intentpidefoto, 30);


    }

    private void setearImagenCam (int resultCode, Intent data)
    {
        switch (resultCode)
        {
            case RESULT_OK:Log.d("MIAPP", "Tiró la foto bien");
                try {
                    Uri uri = data.getData();
                    //Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                    ImageView im = (ImageView) findViewById(R.id.imageView);
                    im.setImageURI(uri);
                    //im.setImageBitmap(thumbnail);
                }catch (Throwable t)
                {
                    Log.e("MIAPP", "ERROR AL SETEAR LA FOTO", t);
                }
                break;

            case RESULT_CANCELED:Log.d("MIAPP", "Canceló la foto");
                break;

        }
    }

    private void setearImagenDeArchivo (int resultCode, Intent data)
    {
        switch (resultCode)
        {
            case RESULT_OK:Log.d("MIAPP", "Seleccionó foto ok");
                Uri uri = data.getData();
                Log.d("MIAPP", "URI = " +data.getData().toString());

                try {

                    if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT) {
                        int takeFlags = data.getFlags() & Intent.FLAG_GRANT_READ_URI_PERMISSION;
                        getContentResolver().takePersistableUriPermission(uri, takeFlags);//obtenemos permisos para leer de esa URI. Android se apunta que nosotros podemos leer desde esta actividad esta URI
                    }
                    Bitmap  bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    ImageView imageView = (ImageView)findViewById(R.id.imageView);
                    imageView.setImageBitmap(bitmap);

                    //Preferencias.guardarFoto(uri.toString(), this);

                } catch (Exception e) {
                    Log.e("MIAPP", "ERROR AL CARGAR LA FOTO", e);
                }
                break;

            case RESULT_CANCELED:Log.d("MIAPP", "Canceló la foto");
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode==500)//viene de mi petición de tirar mi foto
        {
            setearImagenCam(resultCode, data);
        } else if (requestCode==30)
        {
            setearImagenDeArchivo(resultCode, data);
        }

    }

    /**
     * Recibimos el evento sobre una opcion del menú superior
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //TODO permitir cambiar de nombre al usuario
        switch (item.getItemId())
        {
            case android.R.id.home:
                Log.d("MIAPP", "Tocó ir hacia atrás");
                super.onBackPressed();
                break;
        }
        return true;
    }

    //Escuchar cuando toque una opción del menú contextual
   /* @Override
    public boolean onContextItemSelected(MenuItem item) {
        Log.d("MIAPP", "TOCADO MENUCONTEXT " + item.getTitle() +
                " " +item.getOrder());


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("¿Desea eliminar la foto")
                .setTitle("Confirme por favor");

        builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                ImageView imageView = findViewById(R.id.imageView);
                imageView.setImageResource(R.drawable.descarga);

            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

        return super.onContextItemSelected(item);
    }

     @Override
    public void onContextMenuClosed(Menu menu) {
        Log.d("MIAPP", "Context MENU CLOSED");
        super.onContextMenuClosed(menu);
    }

//Inflo o creo el menú contextual
    /*@Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater mi = getMenuInflater();
        mi.inflate(R.menu.menu_contextual, menu);
        menu.setHeaderTitle("Seleccione opción");

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults[0]==PackageManager.PERMISSION_GRANTED) {
            //así dibujo la flecha de navegación estandar atrás
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            ImageView foto_circular = findViewById(R.id.imageView);//Obtengo el elemento sobre el que quiero definir el menú contextual
            registerForContextMenu(foto_circular);//le digo que ahí va el menú
            //  foto_circular.setOnLongClickListener();
            cargarFoto();
        } else {
            Toast.makeText(this, "NO SE PUEDE EJECUTAR ESTA ACTIVIDAD ",Toast.LENGTH_LONG );
            finish();
        }
    }*/



}
