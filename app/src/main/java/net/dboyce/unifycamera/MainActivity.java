package net.dboyce.unifycamera;

import android.content.Context;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import me.aflak.ezcam.EZCam;
import me.aflak.ezcam.EZCamCallback;

public class MainActivity extends AppCompatActivity {

    EZCam cam;
    String picnameholder;
    TextureView textureView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cam = new EZCam(this);
        String id = cam.getCamerasList().get(CameraCharacteristics.LENS_FACING_FRONT); // should check if LENS_FACING_BACK exist before calling get()
        cam.selectCamera(id);
        textureView = (TextureView) findViewById(R.id.textureView2);
        cam.setCameraCallback(new EZCamCallback() {
            @Override
            public void onCameraReady() {
                // triggered after cam.open(...)
                // you can set capture settings for example:
                cam.setCaptureSetting(CaptureRequest.COLOR_CORRECTION_ABERRATION_MODE, CameraMetadata.COLOR_CORRECTION_ABERRATION_MODE_HIGH_QUALITY);
                cam.setCaptureSetting(CaptureRequest.CONTROL_EFFECT_MODE, CameraMetadata.CONTROL_EFFECT_MODE_NEGATIVE);

                // then start the preview
                cam.startPreview();
            }


            //This is the app internal storage, should not be acessible by outside apps etc and gets cleared when app is uninstalled
            //Would i have liked to encrypt these bad boys? you bet but i ran out of time
            @Override
            public void onPicture(Image image) {
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                picnameholder = "JPEG_" + timeStamp + "_";
                File file = new File(getFilesDir(), picnameholder); // internal storage

                try{
                    EZCam.saveImage(image, file);
                }catch (IOException e){

                }
            }

            @Override
            public void onError(String message) {
                // all errors will be passed through this methods
            }

            @Override
            public void onCameraDisconnected() {
                // camera disconnected
            }
        });
    }



    public void takepicture(View v){
        Log.d("BUTTON","we did it");
        cam.open(CameraDevice.TEMPLATE_PREVIEW, textureView); // needs Manifest.permission.CAMERA
        for(int i=0;i<10;i++){
            try {
                Thread.sleep(500);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            cam.takePicture();
        }
        cam.close();
    }
    @Override
    protected void onDestroy() {
        cam.close();
        super.onDestroy();
    }

}
