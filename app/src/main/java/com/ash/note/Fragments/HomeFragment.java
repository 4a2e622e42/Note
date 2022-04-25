package com.ash.note.Fragments;

import android.os.Bundle;


import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ash.note.ViewModel.AppViewModel;
import com.ash.note.Model.Note;
import com.ash.note.Adapter.NoteAdapter;
import com.ash.note.Adapter.PinedAdapter;
import com.ash.note.R;
import com.ash.note.databinding.FragmentHomeBinding;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment
{
    FragmentHomeBinding binding;
    AppViewModel appViewModel;
    NoteAdapter noteAdapter;
    PinedAdapter pinedAdapter;


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





        //Get All Note
        appViewModel.readAllNote.observe(getViewLifecycleOwner(), new Observer<List<Note>>()
        {
            @Override
            public void onChanged(List<Note> notes)
            {
                ArrayList<Note> noteArrayList = new ArrayList<>();
                ArrayList<Note> pinnedNoteList = new ArrayList<>();


                //UpComing note SetUp
                for(Note items: notes)
                {
                    if(!items.isPinned)
                    {
                        noteArrayList.add(items);
                    }
                }

                binding.recycler.setLayoutManager(new GridLayoutManager(requireActivity(),2));
                noteAdapter = new NoteAdapter(noteArrayList);
                binding.recycler.smoothScrollToPosition(0);
                binding.recycler.setAdapter(noteAdapter);

                //Pinned Note SetUp
                for(Note items: notes)
                {
                    if(items.isPinned)
                    {
                        pinnedNoteList.add(items);
                    }
                }
                if (pinnedNoteList.isEmpty())
                {
                    binding.pinnedCon.setVisibility(View.GONE);
                } else {
                    binding.pinnedCon.setVisibility(View.VISIBLE);

                }


                binding.pinedRecycler.setLayoutManager(new LinearLayoutManager(requireActivity(),LinearLayoutManager.HORIZONTAL,false));
                pinedAdapter = new PinedAdapter(pinnedNoteList);
                binding.pinedRecycler.setAdapter(pinedAdapter);




            }
        });






        //Search in recyclerView
        binding.searchBox.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {
                noteAdapter.getFilter().filter(charSequence.toString());

            }

            @Override
            public void afterTextChanged(Editable editable)
            {
                noteAdapter.getFilter().filter(editable.toString());
            }
        });




        return binding.getRoot();
    }










}