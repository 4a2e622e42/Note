package com.ash.note.Fragments;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.GridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;

import com.ash.note.AppViewModel;
import com.ash.note.Data.Note;
import com.ash.note.NoteAdapter;
import com.ash.note.R;
import com.ash.note.databinding.FragmentHomeBinding;
import com.ash.note.databinding.RecyclerRowItemBinding;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.plugins.RxAndroidPlugins;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.plugins.RxJavaPlugins;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class HomeFragment extends Fragment
{
    FragmentHomeBinding binding;
    AppViewModel appViewModel;
    NoteAdapter noteAdapter;

    List<Note> list;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
    {
       binding = DataBindingUtil.inflate(inflater,R.layout.fragment_home, container, false);
       appViewModel = new ViewModelProvider(this).get(AppViewModel.class);


       binding.floatingActionButton.setOnClickListener(new View.OnClickListener()
       {
           @Override
           public void onClick(View view)
           {
               Navigation.findNavController(view).navigate(R.id.action_homeFragment_to_addFragment);
           }
       });

        binding.recycler.setLayoutManager(new GridLayoutManager(requireActivity(),2));


        appViewModel.readAllNote.observe(getViewLifecycleOwner(), new Observer<List<Note>>()
        {
            @Override
            public void onChanged(List<Note> notes)
            {
                list = notes;
                if(binding.recycler.getAdapter() != null)
                {
                    noteAdapter = (NoteAdapter) binding.recycler.getAdapter();
                    noteAdapter.updateData(notes);
                }else
                {
                    noteAdapter = new NoteAdapter(notes);
                    binding.recycler.setAdapter(noteAdapter);
                }

            }
        });








        return binding.getRoot();
    }



}