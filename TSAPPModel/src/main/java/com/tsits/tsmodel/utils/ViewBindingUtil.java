package com.tsits.tsmodel.utils;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewbinding.ViewBinding;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author： YY
 * @date： 2020/8/12
 */

public class ViewBindingUtil {

    private ViewBindingUtil() {}

    public static <T extends ViewBinding> T inflate(@NonNull Class<?> mClass, @NonNull LayoutInflater layoutInflater){
        Type superClass = mClass.getGenericSuperclass();
        //如果泛型类是Class的实例，不包含泛型
        if(superClass instanceof Class<?>) {
            throw new RuntimeException("缺少泛型实现");
        }
        //获取泛型类型数组，作为className
        Class<?> aClass = (Class<?>) ((ParameterizedType) superClass).getActualTypeArguments()[0];
        try {
            Method method = aClass.getDeclaredMethod("inflate", LayoutInflater.class);
            ViewBinding viewBinding = (T) method.invoke(null, layoutInflater);
            return (T) viewBinding;
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }


    public static <T extends ViewBinding> T inflate(@NonNull Class<?> mClass, @NonNull LayoutInflater layoutInflater, ViewGroup container, boolean value){
        Type superClass = mClass.getGenericSuperclass();
        //如果泛型类是Class的实例，不包含泛型
        if(superClass instanceof Class<?>) {
            throw new RuntimeException("缺少泛型实现");
        }
        //获取泛型类型数组，作为className
        Class<T> aClass = (Class<T>) ((ParameterizedType) superClass).getActualTypeArguments()[0];
        try {
            Method method = aClass.getDeclaredMethod("inflate", LayoutInflater.class , ViewGroup.class, boolean.class);
            ViewBinding viewBinding = (T) method.invoke(null, layoutInflater, container, value);
            return (T) viewBinding;
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }
}
