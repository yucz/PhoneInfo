package com.luoye.phoneinfo.gl;

import android.content.Context;
import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by zyw on 18-5-17.
 */

public class OpenGLRenderer implements GLSurfaceView.Renderer  {
    public  static  final  String ACTION_GL_INFO="gl_info";
    private Context context;
    public  OpenGLRenderer(Context context){
        this.context=context;
    }
    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
//        Log.d("GPUInfo", "GL_RENDERER = " +gl10.glGetString(GL10.GL_RENDERER));
//        Log.d("GPUInfo", "GL_VENDOR = " + gl10.glGetString(GL10.GL_VENDOR));
//        Log.d("GPUInfo", "GL_VERSION = " + gl10.glGetString(GL10.GL_VERSION));
//        Log.i("GPUInfo", "GL_EXTENSIONS = " + gl10.glGetString(GL10.GL_EXTENSIONS));
        Intent intent=new Intent(ACTION_GL_INFO);
        intent.putExtra("GL_RENDERER",gl10.glGetString(GL10.GL_RENDERER));
        intent.putExtra("GL_VENDOR",gl10.glGetString(GL10.GL_VENDOR));
        intent.putExtra("GL_VERSION",gl10.glGetString(GL10.GL_VERSION));
        intent.putExtra("GL_EXTENSIONS",gl10.glGetString(GL10.GL_EXTENSIONS));
        context.sendBroadcast(intent);
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int i, int i1) {

    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        gl10.glClearColor(1.0f,0.1f,0.2f,0.3f);
        gl10.glClear(GL10.GL_COLOR_BUFFER_BIT);
    }
}
