package com.example.qrcode;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class PhotoColorPicker extends AppCompatActivity {
    private ImageView imageView;
    Bitmap photo;
    private int color;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_color_picker);
        Intent intent = getIntent();
        imageView = findViewById(R.id.bitmapView);
        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int x = (int) event.getX();
                int y = (int) event.getY();
                int color = photo.getPixel(x,y);
                setColor(color);
                return false;
            }
        });
        getImage();
        if(photo == null)
        {
           Log.d("[ColorPicker]", "Bitmap Not Found !!!") ;
        }
        imageView.setImageBitmap(photo);


    }

    private void getImage()
    {
        photo = CameraPreview.getFinalImage();
    }

    public void setColor(int c)
    {
        color = c;
        setText();
    }

    private void setText() {
        int[] compo = ColorToRGB(this.color);
        TextView t = findViewById(R.id.textRed);
        t.setText("Red : " + Integer.toString(compo[0]));
        t = findViewById(R.id.textGreen);
        t.setText("Green : " + Integer.toString(compo[1]));
        t = findViewById(R.id.textBlue);
        t.setText("Blue : " + Integer.toString(compo[2]));

    }

    static private int[] ColorToRGB(int color)
    {
        // Renvoie les 4 composantes du pixel
        int r,g,b;
        r = (color >> 16) & 0xff;
        g = (color >> 8) & 0xff;
        b = (color ) & 0xff;

        return new int[]{r,g,b};

    }
}
