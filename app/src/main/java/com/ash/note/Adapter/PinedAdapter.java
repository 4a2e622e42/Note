package com.ash.note.Adapter;

import android.annotation.SuppressLint;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.ash.note.Model.Note;
import com.ash.note.R;
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
        PinedRecyclerItemBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.pined_recycler_item,parent,false);

        return new PinnedViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PinnedViewHolder holder, @SuppressLint("RecyclerView") int position)
    {

        holder.bind(notes.get(position));

        switch (notes.get(position).getBgColor())
        {
            case "1":
                holder.binding.card.setCardBackgroundColor(Color.parseColor("#3369ff"));
                holder.binding.title.setTextColor(Color.parseColor("#FFFFFF"));
                holder.binding.content.setTextColor(Color.parseColor("#FFFFFF"));
                holder.binding.date.setTextColor(Color.parseColor("#FFFFFF"));
                break;

            case "2":
                holder.binding.card.setCardBackgroundColor(Color.parseColor("#ffda47"));
                holder.binding.title.setTextColor(Color.parseColor("#101920"));
                holder.binding.content.setTextColor(Color.parseColor("#101920"));
                holder.binding.date.setTextColor(Color.parseColor("#101920"));
                break;

            case "3":
                holder.binding.card.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
                holder.binding.title.setTextColor(Color.parseColor("#202020"));
                holder.binding.content.setTextColor(Color.parseColor("#202020"));
                holder.binding.date.setTextColor(Color.parseColor("#202020"));
                break;

            case "4":
                holder.binding.card.setCardBackgroundColor(Color.parseColor("#ae3b76"));
                holder.binding.title.setTextColor(Color.parseColor("#FFFFFF"));
                holder.binding.content.setTextColor(Color.parseColor("#FFFFFF"));
                holder.binding.date.setTextColor(Color.parseColor("#FFFFFF"));
                break;

            case "5":
                holder.binding.card.setCardBackgroundColor(Color.parseColor("#0aebaf"));
                holder.binding.title.setTextColor(Color.parseColor("#202020"));
                holder.binding.content.setTextColor(Color.parseColor("#202020"));
                holder.binding.date.setTextColor(Color.parseColor("#202020"));
                break;

            case "6":
                holder.binding.card.setCardBackgroundColor(Color.parseColor("#ff7746"));
                holder.binding.title.setTextColor(Color.parseColor("#FFFFFF"));
                holder.binding.content.setTextColor(Color.parseColor("#FFFFFF"));
                holder.binding.date.setTextColor(Color.parseColor("#FFFFFF"));
                break;

            case "7":
                holder.binding.card.setCardBackgroundColor(Color.parseColor("#171c26"));
                holder.binding.title.setTextColor(Color.parseColor("#FFFFFF"));
                holder.binding.content.setTextColor(Color.parseColor("#FFFFFF"));
                holder.binding.date.setTextColor(Color.parseColor("#FFFFFF"));
                break;





        }


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
