package com.luoye.phoneinfo.gl;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

/**
 * Created by xiake on 18-5-17.
 */

public class GLSurfaceViewDemo extends GLSurfaceView {
    OpenGLRenderer mRenderer;
    public GLSurfaceViewDemo(Context context) {
        super(context);
        init(context);
    }

    public  GLSurfaceViewDemo(Context context, AttributeSet attr){
        super(context,attr);
        init(context);
    }

    private  void init(Context context){
        setEGLConfigChooser(8, 8, 8, 8, 0, 0);
        mRenderer = new OpenGLRenderer(context);
        setRenderer(mRenderer);
    }
}
