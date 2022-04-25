package com.ash.note.Adapter;

import android.annotation.SuppressLint;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.ash.note.Model.Note;
import com.ash.note.R;
import com.ash.note.databinding.RecyclerRowItemBinding;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> implements Filterable
{
    List<Note> noteList;
    List<Note> filteredNote;
    LayoutInflater layoutInflater;


    public NoteAdapter(List<Note> noteList)
    {
        this.noteList = noteList;

        this.filteredNote = new ArrayList<>(noteList);
    }




    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        if(layoutInflater == null)
        {
            layoutInflater = LayoutInflater.from(parent.getContext());
        }
        RecyclerRowItemBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.recycler_row_item,parent,false);
        return new NoteViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, @SuppressLint("RecyclerView") int position)
    {


        holder.bind(noteList.get(position));


        switch (noteList.get(position).getBgColor())
        {
            case "1":
                holder.binding.cardView.setCardBackgroundColor(Color.parseColor("#3369ff"));
                holder.binding.title.setTextColor(Color.parseColor("#FFFFFF"));
                holder.binding.content.setTextColor(Color.parseColor("#FFFFFF"));
                holder.binding.date.setTextColor(Color.parseColor("#FFFFFF"));
                break;

            case "2":
                holder.binding.cardView.setCardBackgroundColor(Color.parseColor("#ffda47"));
                holder.binding.title.setTextColor(Color.parseColor("#101920"));
                holder.binding.content.setTextColor(Color.parseColor("#101920"));
                holder.binding.date.setTextColor(Color.parseColor("#101920"));
                break;

            case "3":
                holder.binding.cardView.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
                holder.binding.title.setTextColor(Color.parseColor("#202020"));
                holder.binding.content.setTextColor(Color.parseColor("#202020"));
                holder.binding.date.setTextColor(Color.parseColor("#202020"));
                break;

            case "4":
                holder.binding.cardView.setCardBackgroundColor(Color.parseColor("#ae3b76"));
                holder.binding.title.setTextColor(Color.parseColor("#FFFFFF"));
                holder.binding.content.setTextColor(Color.parseColor("#FFFFFF"));
                holder.binding.date.setTextColor(Color.parseColor("#FFFFFF"));
                break;

            case "5":
                holder.binding.cardView.setCardBackgroundColor(Color.parseColor("#0aebaf"));
                holder.binding.title.setTextColor(Color.parseColor("#202020"));
                holder.binding.content.setTextColor(Color.parseColor("#202020"));
                holder.binding.date.setTextColor(Color.parseColor("#202020"));
                break;

            case "6":
                holder.binding.cardView.setCardBackgroundColor(Color.parseColor("#ff7746"));
                holder.binding.title.setTextColor(Color.parseColor("#FFFFFF"));
                holder.binding.content.setTextColor(Color.parseColor("#FFFFFF"));
                holder.binding.date.setTextColor(Color.parseColor("#FFFFFF"));
                break;

            case "7":
                holder.binding.cardView.setCardBackgroundColor(Color.parseColor("#171c26"));
                holder.binding.title.setTextColor(Color.parseColor("#FFFFFF"));
                holder.binding.content.setTextColor(Color.parseColor("#FFFFFF"));
                holder.binding.date.setTextColor(Color.parseColor("#FFFFFF"));
                break;





        }


        holder.binding.cardView.setOnClickListener(new View.OnClickListener()
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



    @Override
    public Filter getFilter()
    {
        return filter;
    }

    Filter filter = new Filter()
    {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence)
        {
            List<Note> filteringNote = new ArrayList<>();

            if(charSequence.toString().isEmpty())
            {
                filteringNote.addAll(filteredNote);
            }else
            {
                for(Note items: filteredNote)
                {
                    if(items.getTitle().toLowerCase().contains(charSequence.toString())  || items.getContent().toLowerCase().contains(charSequence.toString()))
                    {
                        filteringNote.add(items);
                    }
                }
            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = filteringNote;





            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults)
        {
            noteList.clear();
            noteList.addAll((Collection<? extends Note>) filterResults.values);
            notifyDataSetChanged();

        }
    };






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
