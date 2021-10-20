package com.escom.talkapp.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.escom.talkapp.R;

public class singleCallFragment extends BackHandledFragment {


    public singleCallFragment() {
        // Required empty public constructor
    }


    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_single_call, container, false);
    }
}