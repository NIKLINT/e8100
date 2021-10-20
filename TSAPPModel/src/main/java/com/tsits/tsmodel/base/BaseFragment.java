package com.tsits.tsmodel.base;

import android.graphics.Point;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.ListPopupWindow;
import androidx.fragment.app.Fragment;
import androidx.viewbinding.ViewBinding;

import com.tsits.tsmodel.utils.ViewBindingUtil;

import java.util.List;


/**
 * @author： YY
 * @date： 2020/8/12
 */

public abstract class BaseFragment<T extends ViewBinding> extends Fragment {

    protected T mViewBinding;

    protected ListPopupWindow listPopupWindow;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mViewBinding = ViewBindingUtil.inflate(getClass(), getLayoutInflater(), container,false);
        return mViewBinding.getRoot();
    }

    protected ListPopupWindow setListPoPuWindow(View anchorView, List<String> data,
                                                onPopupWindowListener popupWindowListener){
        Point point=new Point();
        getActivity().getWindowManager().getDefaultDisplay().getSize(point);
        listPopupWindow = new ListPopupWindow(getContext());
        listPopupWindow.setAdapter(new ArrayAdapter<>(getContext()
                , android.R.layout.simple_list_item_1, data));
        listPopupWindow.setAnchorView(anchorView);
        listPopupWindow.setWidth(point.x);
        listPopupWindow.setVerticalOffset(getView().getBottom() - anchorView.getTop());
        listPopupWindow.setDropDownGravity(Gravity.CENTER);
        listPopupWindow.setOnItemClickListener((parent, view1, position, id) -> {
            popupWindowListener.onPopupWindowItemClick(parent, view1, position, id);
            listPopupWindow.dismiss();
        });
        return listPopupWindow;
    }

    protected ListPopupWindow setListPoPuWindow(View anchorView, String[] data,
                                                onPopupWindowListener popupWindowListener){
        Point point=new Point();
        getActivity().getWindowManager().getDefaultDisplay().getSize(point);
        listPopupWindow = new ListPopupWindow(getContext());
        listPopupWindow.setAdapter(new ArrayAdapter<>(getContext()
                , android.R.layout.simple_list_item_1, data));
        listPopupWindow.setAnchorView(anchorView);
        listPopupWindow.setWidth(point.x);
        listPopupWindow.setVerticalOffset(getView().getBottom() - anchorView.getTop());
        listPopupWindow.setDropDownGravity(Gravity.CENTER);
        listPopupWindow.setOnItemClickListener((parent, view1, position, id) -> {
            popupWindowListener.onPopupWindowItemClick(parent, view1, position, id);
            listPopupWindow.dismiss();
        });
        return listPopupWindow;
    }

    protected interface onPopupWindowListener {
        void onPopupWindowItemClick(AdapterView<?> parent, View view, int position, long id);
    }

}
