package com.ash.note;

import android.annotation.SuppressLint;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.ash.note.Data.Note;
import com.ash.note.databinding.PinedRecyclerItemBinding;

import java.util.ArrayList;

public class PinedAdapter extends RecyclerView.Adapter<PinedAdapter.PinnedViewHolder>
{
    ArrayList<Note> notes = new ArrayList<>();
    LayoutInflater layoutInflater;

    public PinedAdapter(ArrayList<Note> notes)
    {
        this.notes = notes;
    }

    @NonNull
    @Override
    public PinnedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        if(layoutInflater == null)
        {
            layoutInflater = LayoutInflater.from(parent.getContext());
        }
        PinedRecyclerItemBinding binding = DataBindingUtil.inflate(layoutInflater,R.layout.pined_recycler_item,parent,false);

        return new PinnedViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PinnedViewHolder holder, @SuppressLint("RecyclerView") int position)
    {

        holder.bind(notes.get(position));

        holder.binding.con.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Bundle bundle = new Bundle();
                bundle.putParcelable("note",notes.get(position));
                Navigation.findNavController(view).navigate(R.id.action_homeFragment_to_updateNoteFragment,bundle);
            }
        });


    }

    @Override
    public int getItemCount()
    {
        return notes.size();
    }

    public class PinnedViewHolder extends RecyclerView.ViewHolder
    {
        PinedRecyclerItemBinding binding;

        public PinnedViewHolder(@NonNull PinedRecyclerItemBinding binding)
        {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Note note)
        {
            binding.title.setText(note.getTitle());
            binding.content.setText(note.getContent());
            binding.date.setText(note.getDate());

            if(note.getImagePath() != null)
            {
                binding.imageNote.setVisibility(View.VISIBLE);
                binding.imageNote.setImageBitmap(BitmapFactory.decodeFile(note.getImagePath()));
            }else
            {
                binding.imageNote.setVisibility(View.GONE);

            }


            binding.executePendingBindings();
        }
    }

}
