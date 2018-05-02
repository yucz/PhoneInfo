package com.example.xiake.myapplication;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.luoye.phoneinfo", appContext.getPackageName());
    }

    @Test
    public void invokeBuild() {
        try {
            Class clazz=Class.forName("android.os.Build");
            Method updateProperties=clazz.getDeclaredMethod("updateProperties");
            updateProperties.invoke(null);

        } catch (ClassNotFoundException e) {
            System.out.println("找不到类");
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            System.out.println("参数错误");
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            System.out.println("目标错误");
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            System.out.println("找不到方法");
            e.printStackTrace();
        }
    }
}
