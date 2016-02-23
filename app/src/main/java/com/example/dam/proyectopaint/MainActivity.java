package com.example.dam.proyectopaint;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.pes.androidmaterialcolorpickerdialog.ColorPicker;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;
import java.util.UUID;

public class MainActivity extends Activity {
    private Vista vista;
    private ImageView iv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
//        setTitle("");

        vista = (Vista) findViewById(R.id.lienzo);
        iv=(ImageView)findViewById(R.id.colorActual);
        iv.setBackgroundColor(Color.BLUE);
    }

    public void circulo(View v) {
        vista.setForma("circulo");
    }

    public void rectangulo(View v) {
        vista.setForma("rectangulo");
    }

    public void recta(View v) {
        vista.setForma("rectaPoligonal");
    }

    public void clear(View v) {

        AlertDialog.Builder dialogoLimpiar = new AlertDialog.Builder(this);
        dialogoLimpiar.setTitle("Nuevo lienzo");
        dialogoLimpiar.setMessage("¿Estas seguro de borrar el lienzo?");
        dialogoLimpiar.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                vista.borraPantalla();
            }
        });
        dialogoLimpiar.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        dialogoLimpiar.show();
    }

    public void r(View v) {
        iv.setBackgroundColor(Color.RED);
        vista.setColor(Color.RED);
    }

    public void g(View v) {
        iv.setBackgroundColor(Color.GREEN);
        vista.setColor(Color.GREEN);
    }

    public void b(View v) {
        iv.setBackgroundColor(Color.BLUE);
        vista.setColor(Color.BLUE);
    }

    public void borrar(View v) {
        vista.setBorrar(true);
    }

    public void picker(View v){
        final ColorPicker cp = new ColorPicker(this, 100, 63, 196);
        cp.show();

        Button okColor = (Button)cp.findViewById(R.id.okColorButton);
        okColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                selectedColorR = cp.getRed();
//                selectedColorG = cp.getGreen();
//                selectedColorB = cp.getBlue();

                vista.setColor(cp.getColor());
                iv.setBackgroundColor(cp.getColor());
                cp.dismiss();
            }
        });
    }

    public void guarda(View v){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Guardando img");

        alert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Bitmap  b = Bitmap.createBitmap( vista.getWidth(), vista.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(b);
                vista.draw(canvas);
                guardaImg(b);
                Toast.makeText(getApplicationContext(), "La foto ha sido guardada en myAppDir/imagen",
                        Toast.LENGTH_LONG).show();
            }
        });

        alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        alert.show();
    }

    public String nombreAleatorio(int longitud){
        String s = "";
        long milis = new java.util.GregorianCalendar().getTimeInMillis();
        Random r = new Random(milis);
        int i = 0;
        while ( i < longitud){
            char c = (char)r.nextInt(255);
            if ( (c >= '0' && c <='9') || (c >='A' && c <='Z') ){
                s += c;
                i ++;
            }
        }
        return s;
    }

    public void cargarArchivo(View v) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        if (intent.resolveActivity(getPackageManager()) != null)
            startActivityForResult(intent, 1);
    }
    String miDirectorio= "/proyectoPaint/imagenes/";
    private boolean guardaImg(Bitmap img) {
       String ruta = Environment.getExternalStorageDirectory() + miDirectorio;
        System.out.println("RUTA: "+ruta);
       File dir = new File(ruta);
       String nombreFichero=nombreAleatorio(7)+".JPEG";

        dir.mkdirs();
        try {
            String filePath = dir.toString() +"/"+ nombreFichero;
            FileOutputStream fileOutputStream = new FileOutputStream(filePath);
            img.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();

        } catch (FileNotFoundException e) {
        } catch (IOException e) {}

        return true;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 1) {
            Uri uri = data.getData();
            if (uri != null) { //Obtiene la Uri de la imagen
                Uri imageUri = data.getData();
                Bitmap b = null;
                try {
                    b = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                vista.setBitmap(b);
            }
        }
    }
    public void tam(View v) {
        AlertDialog.Builder dialogoTam = new AlertDialog.Builder(this);
        dialogoTam.setTitle("Tamaño pincel");
        dialogoTam.setPositiveButton("Pequeño", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                vista.setTamPincel(getResources().getInteger(R.integer.small_size));
            }
        });
        dialogoTam.setNeutralButton("Mediano", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                vista.setTamPincel(getResources().getInteger(R.integer.medium_size));
            }
        });
        dialogoTam.setNegativeButton("Grande", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                vista.setTamPincel(getResources().getInteger(R.integer.large_size));
            }
        });
        dialogoTam.show();
    }
}