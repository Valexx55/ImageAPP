package edu.cftic.imagenapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends AppCompatActivity {


    private static final String[] PERMISOS = {
        Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private static final String PREFIJO_FOTOS = "CURSO_PIC_";
    private static final String SUFIJO_FOTOS = ".jpg";
    private String ruta_foto;//nombre fichero creado

    private static final int CODIGO_PETICION_SELECCIONAR_FOTO = 100;
    private static final int CODIGO_PETICION_PERMISOS = 150;
    private static final int CODIGO_PETICION_HACER_FOTO = 200;
    private ImageView imageView;//la imagen seleccionada o la foto
    private Uri photo_uri;//para almacenar la ruta de la imagen

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.imageView = findViewById(R.id.imageView);

        ActivityCompat.requestPermissions
                (this, PERMISOS, CODIGO_PETICION_PERMISOS );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if ((grantResults[0]== PackageManager.PERMISSION_GRANTED)
            && (grantResults[1]== PackageManager.PERMISSION_GRANTED))
        {
            Log.d("MIAPP", "ME ha concecido los permisos");
        } else {
            Log.d("MIAPP", "NO ME ha concecido los permisos");
            Toast.makeText(this,"NO PUEDES SEGUIR", Toast.LENGTH_SHORT).show();
            this.finish();
        }
    }
    //creamos el fichero donde irá la imagen
    private Uri crearFicheroImagen () {
        Uri uri_destino = null;
        String momento_actual = null;
        String nombre_fichero = null;
        File file = null;

        momento_actual = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        nombre_fichero = PREFIJO_FOTOS + momento_actual + SUFIJO_FOTOS;

        ruta_foto =
                Environment.getExternalStoragePublicDirectory
                        (Environment.DIRECTORY_PICTURES).getPath() + "/" +
                        nombre_fichero;

        Log.d("MIAPP", "RUTA FOTO " + ruta_foto);
        file = new File(ruta_foto);

        try { //INTENTA ESTO

            if (file.createNewFile()) {
                Log.d("MIAPP", "FICHERO CREADO");
            } else {
                Log.d("MIAPP", "FICHERO NO CREADO");
            }
        } catch (IOException e) { // Y SI FALLA SE METE POR AQUÍ
            Log.e("MIAPP", "Error al crear el fichero", e);
        }

        uri_destino = Uri.fromFile(file);
        Log.d("MIAPP", "URI = " + uri_destino.toString());

        return uri_destino;
    }


    private void desactivarModoEstricto ()
    {
        if (Build.VERSION.SDK_INT >= 24)
        {
            try{
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                m.invoke(null);

            }catch (Exception e)
            {

            }
        }
    }
    public void tomarFoto(View view) {

        Log.d("MIAPP", "Quiere hacer una foto");
        Intent intent_foto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        this.photo_uri = crearFicheroImagen ();
        intent_foto.putExtra(MediaStore.EXTRA_OUTPUT, this.photo_uri);
        desactivarModoEstricto();
        startActivityForResult(intent_foto, CODIGO_PETICION_HACER_FOTO);

    }

    public void seleccionarFoto(View view) {
        Log.d("MIAPP", "Quiere seleccionar una foto");
        Intent intent_pide_foto = new Intent();
        //intent_pide_foto.setAction(Intent.ACTION_PICK);//seteo la acción para galeria
        intent_pide_foto.setAction(Intent.ACTION_GET_CONTENT);//seteo la acción
        intent_pide_foto.setType("image/*");//tipo mime

        startActivityForResult(intent_pide_foto, CODIGO_PETICION_SELECCIONAR_FOTO);

    }

    private void setearImagenDesdeArchivo (int resultado, Intent data)
    {
        switch (resultado){
            case RESULT_OK:
                Log.d("MIAPP", "La foto ha sido seleccionada");
                this.photo_uri = data.getData();//obtenemos la uri de la foto seleccionada
                this.imageView.setImageURI(photo_uri);
                this.imageView.setScaleType(ImageView.ScaleType.FIT_XY);

                break;
            case RESULT_CANCELED:
                Log.d("MIAPP", "La foto NO ha sido seleccionada canceló");
                break;
        }
    }

    private void setearImagenDesdeCamara (int resultado, Intent intent)
    {
        switch (resultado)
        {
            case RESULT_OK:
                Log.d("MIAPP", "Tiró la foto bien");
                imageView.setImageURI(this.photo_uri);
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, photo_uri));
                break;
            case RESULT_CANCELED:
                Log.d("MIAPP", "Canceló la foto");
                break;
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);//no llamamos al padre
        if (requestCode==CODIGO_PETICION_SELECCIONAR_FOTO)
        {
            setearImagenDesdeArchivo (resultCode, data);
        } else if (requestCode == CODIGO_PETICION_HACER_FOTO)
            {
                setearImagenDesdeCamara (resultCode, data);
            }
    }
}
