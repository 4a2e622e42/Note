package com.ash.note;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.ash.note.Data.Note;
import com.ash.note.databinding.RecyclerRowItemBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder>
{
    List<Note> noteList;
    LayoutInflater layoutInflater;


    public NoteAdapter(List<Note> noteList)
    {
        this.noteList = noteList;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        if(layoutInflater == null)
        {
            layoutInflater = LayoutInflater.from(parent.getContext());
        }
        RecyclerRowItemBinding binding = DataBindingUtil.inflate(layoutInflater,R.layout.recycler_row_item,parent,false);
        return new NoteViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, @SuppressLint("RecyclerView") int position)
    {


        holder.bind(noteList.get(position));


        //Change CardView Color Randomly
        ArrayList<String> colorList = new ArrayList<>();
        colorList.add("#ffda47");
        colorList.add("#ae3b76");
        colorList.add("#0aebaf");
        colorList.add("#171c26");
        colorList.add("#3369ff");

        holder.binding.linearLayout.setBackgroundColor(Color.parseColor(colorList.get(new Random().nextInt(colorList.size()))));


        holder.binding.con.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Bundle bundle = new Bundle();
                bundle.putParcelable("note",noteList.get(position));



                Navigation.findNavController(view).navigate(R.id.action_homeFragment_to_updateNoteFragment,bundle);
            }
        });







    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }

    public void updateData(List<Note> list)
    {
        noteList.addAll(list);
        notifyDataSetChanged();
    }

    public class NoteViewHolder extends RecyclerView.ViewHolder
    {
        RecyclerRowItemBinding binding;

        public NoteViewHolder(@NonNull RecyclerRowItemBinding binding)
        {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Note note)
        {
            binding.title.setText(note.getTitle());
            binding.content.setText(note.getContent());
            binding.date.setText(note.getDate());

            binding.executePendingBindings();
        }


    }
}
