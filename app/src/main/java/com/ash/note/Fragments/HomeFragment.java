package com.ash.note.Fragments;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ash.note.R;
import com.ash.note.databinding.FragmentHomeBinding;


public class HomeFragment extends Fragment
{
    FragmentHomeBinding binding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
    {
       binding = DataBindingUtil.inflate(inflater,R.layout.fragment_home, container, false);






       return binding.getRoot();
    }
}