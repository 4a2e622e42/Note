package com.ash.note.Fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ash.note.AppViewModel;
import com.ash.note.Data.Note;
import com.ash.note.R;
import com.ash.note.TextUndoRedo;
import com.ash.note.databinding.FragmentUpdateNoteBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import pub.devrel.easypermissions.EasyPermissions;

public class UpdateNoteFragment extends Fragment
{
    FragmentUpdateNoteBinding binding;
    AppViewModel appViewModel;
    String bgColor;
    BottomSheetDialog dialog;
    public Note note;

    Vibrator vibrator;

    int uid;
    int clickCount = 0;

    boolean isPinned;

    int titleCharNumber;
    int subTitleCharNumber;
    int contentCharNumber;
    int totalCharNumber ;

    String selectedImagePath;

    private static final int REQUEST_CODE_STORAGE_PERMISSION = 1;
    private static final int REQUEST_CODE_SELECT_IMAGE = 2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
    {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_update_note, container, false);
        appViewModel = new ViewModelProvider(this).get(AppViewModel.class);

        note = getArguments().getParcelable("note");


        selectedImagePath = note.getImagePath();

        uid = note.getUid();


        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, HH:mm");
        String time = simpleDateFormat.format(new Date());


        //Init Vibrator
        vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);

        totalCharNumber = note.getCharNumber();


        binding.upDatedTime.setText(time+" | "+(totalCharNumber)+" Character");



        binding.upDatedTitle.setText(note.getTitle().toString());
        binding.upDatedSubtitle.setText(note.getSubTitle().toString());
        binding.upDatedContent.setText(note.getContent().toString());


        bgColor = note.getBgColor();



        bgSetUp(bgColor);



        dialog = new BottomSheetDialog(requireActivity());
        moreButton(note.isPinned);

        binding.more.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                dialog.show();
            }
        });

        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);



//---------------------------------------------------------------------------------------------------------------------------------------------
        //Get Image
        Log.e("TAG","selctedPath:  "+selectedImagePath);
        Log.e("TAG","GetFromNote:  "+note.getImagePath());


        if(note.getImagePath() != null)
        {
            binding.noteImage.setImageBitmap(BitmapFactory.decodeFile(note.getImagePath()));
            binding.noteImage.setVisibility(View.VISIBLE);
        }else
        {
            binding.noteImage.setVisibility(View.GONE);
        }








//---------------------------------------------------------------------------------------------------------------------------------------------
        //Count of Character by watching editTexts

        binding.upDatedTitle.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {
                titleCharNumber = i + i2;
                binding.undoBtn.setVisibility(View.INVISIBLE);
                binding.redoBtn.setVisibility(View.INVISIBLE);
                Log.e("TAG","Title:  "+"i:  "+i+"    i1:   "+i1+"     i2:   "+i2);


            }

            @Override
            public void afterTextChanged(Editable editable)
            {

                totalCharNumber = titleCharNumber + subTitleCharNumber + contentCharNumber;
                binding.upDatedTime.setText(time+" | "+(totalCharNumber)+" Character");


            }
        });


        binding.upDatedSubtitle.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {
                subTitleCharNumber = i + i2;
                binding.undoBtn.setVisibility(View.INVISIBLE);
                binding.redoBtn.setVisibility(View.INVISIBLE);
                Log.e("TAG","SubTitle:  "+"i:  "+i+"    i1:   "+i1+"     i2:   "+i2);


            }

            @Override
            public void afterTextChanged(Editable editable)
            {
                totalCharNumber = titleCharNumber + subTitleCharNumber + contentCharNumber;
                binding.upDatedTime.setText(time+" | "+(totalCharNumber)+" Character");

            }
        });



        binding.upDatedContent.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {
                contentCharNumber = i + i2;
                binding.undoBtn.setVisibility(View.VISIBLE);
                binding.redoBtn.setVisibility(View.VISIBLE);
                Log.e("TAG","Content:  "+"i:  "+i+"    i1:   "+i1+"     i2:   "+i2);

            }

            @Override
            public void afterTextChanged(Editable editable)
            {

                totalCharNumber = titleCharNumber + subTitleCharNumber + contentCharNumber;
                binding.upDatedTime.setText(time+" | "+(totalCharNumber)+" Character");

            }
        });


//---------------------------------------------------------------------------------------------------------------------------------------------
        TextUndoRedo.TextChangeInfo textChangeInfo = () -> {};
        TextUndoRedo contentUndoRedo    =   new TextUndoRedo(binding.upDatedContent ,textChangeInfo);


        //Undo
        binding.undoBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(contentUndoRedo.canUndo())
                {
                    contentUndoRedo.exeUndo();
                }
            }
        });

        //Redo
        binding.redoBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(contentUndoRedo.canRedo())
                {
                    contentUndoRedo.exeRedo();
                }
            }
        });



//---------------------------------------------------------------------------------------------------------------------------------------------

        binding.doneBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
             updateNote(note);
             Navigation.findNavController(view).navigate(R.id.action_updateNoteFragment_to_homeFragment);

            }
        });


        binding.backButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                updateNote(note);
                requireActivity().onBackPressed();
            }
        });





        return binding.getRoot();
    }


    private void updateNote(Note note)
    {

        Note updateNote   =  note;
        updateNote.uid    =  note.getUid();
        updateNote.setTitle(binding.upDatedTitle.getText().toString());
        updateNote.setSubTitle(binding.upDatedSubtitle.getText().toString());
        updateNote.setContent(binding.upDatedContent.getText().toString());
        updateNote.setCharNumber(totalCharNumber);
        updateNote.setBgColor(bgColor);
        updateNote.setPinned(isPinned);
        updateNote.setImagePath(selectedImagePath);


        appViewModel.updateNote(updateNote);


    }

    private void bgSetUp(String bgColor)
    {
        View view = LayoutInflater.from(requireActivity()).inflate(R.layout.bottom_sheet_dialog,binding.getRoot().findViewById(R.id.sheetContainer));

        ImageView blueCircle,yellowCircle,whiteCircle,purpleCircle,greenCircle,orangeCircle,blackCircle;

        blueCircle = view.findViewById(R.id.blueCircle);
        yellowCircle = view.findViewById(R.id.yellowCircle);
        whiteCircle = view.findViewById(R.id.whiteCircle);
        purpleCircle = view.findViewById(R.id.purpleCircle);
        greenCircle = view.findViewById(R.id.greenCircle);
        orangeCircle = view.findViewById(R.id.orangeCircle);
        blackCircle = view.findViewById(R.id.blackCircle);




        switch (bgColor)
        {
            case "1":
                blueCircle.setImageResource(R.drawable.ic_baseline_done_24);
                yellowCircle.setImageResource(0);
                whiteCircle.setImageResource(0);
                purpleCircle.setImageResource(0);
                greenCircle.setImageResource(0);
                orangeCircle.setImageResource(0);
                blackCircle.setImageResource(0);

                binding.addFragmentCon.setBackgroundColor(Color.parseColor("#3369ff"));
                binding.upDatedTitle.setBackgroundColor(Color.parseColor("#3369ff"));
                binding.upDatedSubtitle.setBackgroundColor(Color.parseColor("#3369ff"));
                binding.upDatedContent.setBackgroundColor(Color.parseColor("#3369ff"));
                binding.nestedScrollView.setBackgroundColor(Color.parseColor("#3369ff"));

                binding.upDatedTitle.setHintTextColor(Color.WHITE);
                binding.upDatedSubtitle.setHintTextColor(Color.WHITE);
                binding.upDatedContent.setHintTextColor(Color.WHITE);

                binding.upDatedTitle.setTextColor(Color.WHITE);
                binding.upDatedTime.setTextColor(Color.WHITE);
                binding.upDatedSubtitle.setTextColor(Color.WHITE);
                binding.upDatedContent.setTextColor(Color.WHITE);


                binding.doneBtn.setColorFilter(Color.parseColor("#FFFFFF"));
                binding.undoBtn.setColorFilter(Color.parseColor("#FFFFFF"));
                binding.redoBtn.setColorFilter(Color.parseColor("#FFFFFF"));
                binding.backButton.setColorFilter(Color.parseColor("#FFFFFF"));
                binding.more.setColorFilter(Color.parseColor("#FFFFFF"));
                break;

            case "2":
                blueCircle.setImageResource(0);
                yellowCircle.setImageResource(R.drawable.ic_baseline_done_24);
                whiteCircle.setImageResource(0);
                purpleCircle.setImageResource(0);
                greenCircle.setImageResource(0);
                orangeCircle.setImageResource(0);
                blackCircle.setImageResource(0);

                binding.addFragmentCon.setBackgroundColor(Color.parseColor("#ffda47"));
                binding.upDatedTitle.setBackgroundColor(Color.parseColor("#ffda47"));
                binding.upDatedSubtitle.setBackgroundColor(Color.parseColor("#ffda47"));
                binding.upDatedContent.setBackgroundColor(Color.parseColor("#ffda47"));
                binding.nestedScrollView.setBackgroundColor(Color.parseColor("#ffda47"));


                binding.upDatedTitle.setHintTextColor(Color.parseColor("#101920"));
                binding.upDatedSubtitle.setHintTextColor(Color.parseColor("#101920"));
                binding.upDatedContent.setHintTextColor(Color.parseColor("#101920"));

                binding.upDatedTitle.setTextColor(Color.parseColor("#101920"));
                binding.upDatedTime.setTextColor(Color.parseColor("#101920"));
                binding.upDatedSubtitle.setTextColor(Color.parseColor("#101920"));
                binding.upDatedContent.setTextColor(Color.parseColor("#101920"));


                binding.doneBtn.setColorFilter(Color.parseColor("#101920"));
                binding.undoBtn.setColorFilter(Color.parseColor("#101920"));
                binding.redoBtn.setColorFilter(Color.parseColor("#101920"));
                binding.backButton.setColorFilter(Color.parseColor("#101920"));
                binding.more.setColorFilter(Color.parseColor("#101920"));
                break;
            case "3":
                blueCircle.setImageResource(0);
                yellowCircle.setImageResource(0);
                whiteCircle.setImageResource(R.drawable.ic_baseline_done_24);
                purpleCircle.setImageResource(0);
                greenCircle.setImageResource(0);
                orangeCircle.setImageResource(0);
                blackCircle.setImageResource(0);

                binding.addFragmentCon.setBackgroundColor(Color.parseColor("#FFFFFF"));

                binding.upDatedTitle.setBackgroundColor(Color.parseColor("#FFFFFF"));
                binding.upDatedSubtitle.setBackgroundColor(Color.parseColor("#FFFFFF"));
                binding.upDatedContent.setBackgroundColor(Color.parseColor("#FFFFFF"));
                binding.nestedScrollView.setBackgroundColor(Color.parseColor("#FFFFFF"));

                binding.upDatedTitle.setHintTextColor(Color.parseColor("#6c7589"));
                binding.upDatedSubtitle.setHintTextColor(Color.parseColor("#6c7589"));
                binding.upDatedContent.setHintTextColor(Color.parseColor("#6c7589"));

                binding.upDatedTitle.setTextColor(Color.parseColor("#202020"));
                binding.upDatedTime.setTextColor(Color.parseColor("#202020"));
                binding.upDatedSubtitle.setTextColor(Color.parseColor("#202020"));
                binding.upDatedContent.setTextColor(Color.parseColor("#202020"));


                binding.doneBtn.setColorFilter(Color.parseColor("#3369ff"));
                binding.undoBtn.setColorFilter(Color.parseColor("#3369ff"));
                binding.redoBtn.setColorFilter(Color.parseColor("#3369ff"));
                binding.backButton.setColorFilter(Color.parseColor("#3369ff"));
                binding.more.setColorFilter(Color.parseColor("#3369ff"));
                break;

            case "4":
                blueCircle.setImageResource(0);
                yellowCircle.setImageResource(0);
                whiteCircle.setImageResource(0);
                purpleCircle.setImageResource(R.drawable.ic_baseline_done_24);
                greenCircle.setImageResource(0);
                orangeCircle.setImageResource(0);
                blackCircle.setImageResource(0);

                binding.addFragmentCon.setBackgroundColor(Color.parseColor("#ae3b76"));

                binding.upDatedTitle.setBackgroundColor(Color.parseColor("#ae3b76"));
                binding.upDatedSubtitle.setBackgroundColor(Color.parseColor("#ae3b76"));
                binding.upDatedContent.setBackgroundColor(Color.parseColor("#ae3b76"));
                binding.nestedScrollView.setBackgroundColor(Color.parseColor("#ae3b76"));

                binding.upDatedTitle.setHintTextColor(Color.parseColor("#FFFFFF"));
                binding.upDatedSubtitle.setHintTextColor(Color.parseColor("#FFFFFF"));
                binding.upDatedContent.setHintTextColor(Color.parseColor("#FFFFFF"));

                binding.upDatedTitle.setTextColor(Color.parseColor("#FFFFFF"));
                binding.upDatedTime.setTextColor(Color.parseColor("#FFFFFF"));
                binding.upDatedSubtitle.setTextColor(Color.parseColor("#FFFFFF"));
                binding.upDatedContent.setTextColor(Color.parseColor("#FFFFFF"));


                binding.doneBtn.setColorFilter(Color.parseColor("#FFFFFF"));
                binding.undoBtn.setColorFilter(Color.parseColor("#FFFFFF"));
                binding.redoBtn.setColorFilter(Color.parseColor("#FFFFFF"));
                binding.backButton.setColorFilter(Color.parseColor("#FFFFFF"));
                binding.more.setColorFilter(Color.parseColor("#FFFFFF"));
                break;


            case "5":
                blueCircle.setImageResource(0);
                yellowCircle.setImageResource(0);
                whiteCircle.setImageResource(0);
                purpleCircle.setImageResource(0);
                greenCircle.setImageResource(R.drawable.ic_baseline_done_24);
                orangeCircle.setImageResource(0);
                blackCircle.setImageResource(0);

                binding.addFragmentCon.setBackgroundColor(Color.parseColor("#0aebaf"));


                binding.upDatedTitle.setBackgroundColor(Color.parseColor("#0aebaf"));
                binding.upDatedSubtitle.setBackgroundColor(Color.parseColor("#0aebaf"));
                binding.upDatedContent.setBackgroundColor(Color.parseColor("#0aebaf"));
                binding.nestedScrollView.setBackgroundColor(Color.parseColor("#0aebaf"));

                binding.upDatedTitle.setHintTextColor(Color.parseColor("#202020"));
                binding.upDatedSubtitle.setHintTextColor(Color.parseColor("#202020"));
                binding.upDatedContent.setHintTextColor(Color.parseColor("#202020"));

                binding.upDatedTitle.setTextColor(Color.parseColor("#202020"));
                binding.upDatedTime.setTextColor(Color.parseColor("#202020"));
                binding.upDatedSubtitle.setTextColor(Color.parseColor("#202020"));
                binding.upDatedContent.setTextColor(Color.parseColor("#202020"));


                binding.doneBtn.setColorFilter(Color.parseColor("#202020"));
                binding.undoBtn.setColorFilter(Color.parseColor("#202020"));
                binding.redoBtn.setColorFilter(Color.parseColor("#202020"));
                binding.backButton.setColorFilter(Color.parseColor("#202020"));
                binding.more.setColorFilter(Color.parseColor("#202020"));
                break;

            case "6":
                blueCircle.setImageResource(0);
                yellowCircle.setImageResource(0);
                whiteCircle.setImageResource(0);
                purpleCircle.setImageResource(0);
                greenCircle.setImageResource(0);
                orangeCircle.setImageResource(R.drawable.ic_baseline_done_24);
                blackCircle.setImageResource(0);

                binding.addFragmentCon.setBackgroundColor(Color.parseColor("#ff7746"));

                binding.upDatedTitle.setBackgroundColor(Color.parseColor("#ff7746"));
                binding.upDatedSubtitle.setBackgroundColor(Color.parseColor("#ff7746"));
                binding.upDatedContent.setBackgroundColor(Color.parseColor("#ff7746"));
                binding.nestedScrollView.setBackgroundColor(Color.parseColor("#ff7746"));

                binding.upDatedTitle.setHintTextColor(Color.parseColor("#FFFFFF"));
                binding.upDatedSubtitle.setHintTextColor(Color.parseColor("#FFFFFF"));
                binding.upDatedContent.setHintTextColor(Color.parseColor("#FFFFFF"));

                binding.upDatedTitle.setTextColor(Color.parseColor("#FFFFFF"));
                binding.upDatedTime.setTextColor(Color.parseColor("#FFFFFF"));
                binding.upDatedSubtitle.setTextColor(Color.parseColor("#FFFFFF"));
                binding.upDatedContent.setTextColor(Color.parseColor("#FFFFFF"));


                binding.doneBtn.setColorFilter(Color.parseColor("#FFFFFF"));
                binding.undoBtn.setColorFilter(Color.parseColor("#FFFFFF"));
                binding.redoBtn.setColorFilter(Color.parseColor("#FFFFFF"));
                binding.backButton.setColorFilter(Color.parseColor("#FFFFFF"));
                binding.more.setColorFilter(Color.parseColor("#FFFFFF"));
                break;

            case "7":
                blueCircle.setImageResource(0);
                yellowCircle.setImageResource(0);
                whiteCircle.setImageResource(0);
                purpleCircle.setImageResource(0);
                greenCircle.setImageResource(0);
                orangeCircle.setImageResource(0);
                blackCircle.setImageResource(R.drawable.ic_baseline_done_24);

                binding.addFragmentCon.setBackgroundColor(Color.parseColor("#0e121b"));

                binding.upDatedTitle.setBackgroundColor(Color.parseColor("#0e121b"));
                binding.upDatedSubtitle.setBackgroundColor(Color.parseColor("#0e121b"));
                binding.upDatedContent.setBackgroundColor(Color.parseColor("#0e121b"));
                binding.nestedScrollView.setBackgroundColor(Color.parseColor("#0e121b"));

                binding.upDatedTitle.setHintTextColor(Color.parseColor("#FFFFFF"));
                binding.upDatedSubtitle.setHintTextColor(Color.parseColor("#FFFFFF"));
                binding.upDatedContent.setHintTextColor(Color.parseColor("#FFFFFF"));

                binding.upDatedTitle.setTextColor(Color.parseColor("#FFFFFF"));
                binding.upDatedTime.setTextColor(Color.parseColor("#FFFFFF"));
                binding.upDatedSubtitle.setTextColor(Color.parseColor("#FFFFFF"));
                binding.upDatedContent.setTextColor(Color.parseColor("#FFFFFF"));


                binding.doneBtn.setColorFilter(Color.parseColor("#FFFFFF"));
                binding.undoBtn.setColorFilter(Color.parseColor("#FFFFFF"));
                binding.redoBtn.setColorFilter(Color.parseColor("#FFFFFF"));
                binding.backButton.setColorFilter(Color.parseColor("#FFFFFF"));
                binding.more.setColorFilter(Color.parseColor("#FFFFFF"));
                break;

        }



    }
    private void moreButton(boolean pin)
    {
        View view = getLayoutInflater().inflate(R.layout.bottom_sheet_dialog,null);

        dialog.setContentView(view);

        TextView deleteNote,pinText,addImage,makeCopy;

        ImageView blueCircle,yellowCircle,whiteCircle,purpleCircle,greenCircle,orangeCircle,blackCircle,pinImage;
        blueCircle = view.findViewById(R.id.blueCircle);
        yellowCircle = view.findViewById(R.id.yellowCircle);
        whiteCircle = view.findViewById(R.id.whiteCircle);
        purpleCircle = view.findViewById(R.id.purpleCircle);
        greenCircle = view.findViewById(R.id.greenCircle);
        orangeCircle = view.findViewById(R.id.orangeCircle);
        blackCircle = view.findViewById(R.id.blackCircle);


        deleteNote = view.findViewById(R.id.deleteText);
        pinText = view.findViewById(R.id.pinText);
        pinImage = view.findViewById(R.id.pinImage);
        addImage = view.findViewById(R.id.addImageText);
        makeCopy = view.findViewById(R.id.makeCopyText);






        deleteNote.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                appViewModel.deleteNote(note);
                dialog.dismiss();
                requireActivity().onBackPressed();
            }
        });

        if(pin)
        {
            isPinned = true;
            clickCount = 1;
            pinImage.setImageResource(R.drawable.ic_red_push_pin);
            pinText.setText("Unpin");
        }else
        {
            isPinned = false;
            clickCount = 0;
            pinImage.setImageResource(R.drawable.ic_baseline_push_pin_24);
            pinText.setText("Pin");

        }

        pinText.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                {
                    vibrator.vibrate(VibrationEffect.createOneShot(100, 1));
                } else
                {
                    vibrator.vibrate(100);
                }
                if(isEven(clickCount))
                {
                    pinImage.setImageResource(R.drawable.ic_red_push_pin);
                    pinText.setText("Unpin");
                    isPinned = true;
                }else
                {
                    pinImage.setImageResource(R.drawable.ic_baseline_push_pin_24);
                    pinText.setText("Pin");
                    isPinned = false;
                }

                clickCount ++;
            }
        });


        makeCopy.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                updateNote(note);
                Toast.makeText(requireActivity()," Copy Created",Toast.LENGTH_SHORT).show();

            }
        });











        blueCircle.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                blueCircle.setImageResource(R.drawable.ic_baseline_done_24);
                yellowCircle.setImageResource(0);
                whiteCircle.setImageResource(0);
                purpleCircle.setImageResource(0);
                greenCircle.setImageResource(0);
                orangeCircle.setImageResource(0);
                blackCircle.setImageResource(0);

                binding.addFragmentCon.setBackgroundColor(Color.parseColor("#3369ff"));
                binding.upDatedTitle.setBackgroundColor(Color.parseColor("#3369ff"));
                binding.upDatedSubtitle.setBackgroundColor(Color.parseColor("#3369ff"));
                binding.upDatedContent.setBackgroundColor(Color.parseColor("#3369ff"));
                binding.nestedScrollView.setBackgroundColor(Color.parseColor("#3369ff"));

                binding.upDatedTitle.setHintTextColor(Color.WHITE);
                binding.upDatedSubtitle.setHintTextColor(Color.WHITE);
                binding.upDatedContent.setHintTextColor(Color.WHITE);

                binding.upDatedTitle.setTextColor(Color.WHITE);
                binding.upDatedTime.setTextColor(Color.WHITE);
                binding.upDatedSubtitle.setTextColor(Color.WHITE);
                binding.upDatedContent.setTextColor(Color.WHITE);


                binding.doneBtn.setColorFilter(Color.parseColor("#FFFFFF"));
                binding.undoBtn.setColorFilter(Color.parseColor("#FFFFFF"));
                binding.redoBtn.setColorFilter(Color.parseColor("#FFFFFF"));
                binding.backButton.setColorFilter(Color.parseColor("#FFFFFF"));
                binding.more.setColorFilter(Color.parseColor("#FFFFFF"));

                bgColor = "1";
            }

        });


        yellowCircle.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                blueCircle.setImageResource(0);
                yellowCircle.setImageResource(R.drawable.ic_baseline_done_24);
                whiteCircle.setImageResource(0);
                purpleCircle.setImageResource(0);
                greenCircle.setImageResource(0);
                orangeCircle.setImageResource(0);
                blackCircle.setImageResource(0);

                binding.addFragmentCon.setBackgroundColor(Color.parseColor("#ffda47"));
                binding.upDatedTitle.setBackgroundColor(Color.parseColor("#ffda47"));
                binding.upDatedSubtitle.setBackgroundColor(Color.parseColor("#ffda47"));
                binding.upDatedContent.setBackgroundColor(Color.parseColor("#ffda47"));
                binding.nestedScrollView.setBackgroundColor(Color.parseColor("#ffda47"));


                binding.upDatedTitle.setHintTextColor(Color.parseColor("#101920"));
                binding.upDatedSubtitle.setHintTextColor(Color.parseColor("#101920"));
                binding.upDatedContent.setHintTextColor(Color.parseColor("#101920"));

                binding.upDatedTitle.setTextColor(Color.parseColor("#101920"));
                binding.upDatedTime.setTextColor(Color.parseColor("#101920"));
                binding.upDatedSubtitle.setTextColor(Color.parseColor("#101920"));
                binding.upDatedContent.setTextColor(Color.parseColor("#101920"));


                binding.doneBtn.setColorFilter(Color.parseColor("#101920"));
                binding.undoBtn.setColorFilter(Color.parseColor("#101920"));
                binding.redoBtn.setColorFilter(Color.parseColor("#101920"));
                binding.backButton.setColorFilter(Color.parseColor("#101920"));
                binding.more.setColorFilter(Color.parseColor("#101920"));


                bgColor = "2";

            }
        });


        whiteCircle.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                blueCircle.setImageResource(0);
                yellowCircle.setImageResource(0);
                whiteCircle.setImageResource(R.drawable.ic_baseline_done_24);
                purpleCircle.setImageResource(0);
                greenCircle.setImageResource(0);
                orangeCircle.setImageResource(0);
                blackCircle.setImageResource(0);

                binding.addFragmentCon.setBackgroundColor(Color.parseColor("#FFFFFF"));

                binding.upDatedTitle.setBackgroundColor(Color.parseColor("#FFFFFF"));
                binding.upDatedSubtitle.setBackgroundColor(Color.parseColor("#FFFFFF"));
                binding.upDatedContent.setBackgroundColor(Color.parseColor("#FFFFFF"));
                binding.nestedScrollView.setBackgroundColor(Color.parseColor("#FFFFFF"));

                binding.upDatedTitle.setHintTextColor(Color.parseColor("#6c7589"));
                binding.upDatedSubtitle.setHintTextColor(Color.parseColor("#6c7589"));
                binding.upDatedContent.setHintTextColor(Color.parseColor("#6c7589"));

                binding.upDatedTitle.setTextColor(Color.parseColor("#202020"));
                binding.upDatedTime.setTextColor(Color.parseColor("#202020"));
                binding.upDatedSubtitle.setTextColor(Color.parseColor("#202020"));
                binding.upDatedContent.setTextColor(Color.parseColor("#202020"));


                binding.doneBtn.setColorFilter(Color.parseColor("#3369ff"));
                binding.undoBtn.setColorFilter(Color.parseColor("#3369ff"));
                binding.redoBtn.setColorFilter(Color.parseColor("#3369ff"));
                binding.backButton.setColorFilter(Color.parseColor("#3369ff"));
                binding.more.setColorFilter(Color.parseColor("#3369ff"));

                bgColor = "3";

            }
        });

        purpleCircle.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                blueCircle.setImageResource(0);
                yellowCircle.setImageResource(0);
                whiteCircle.setImageResource(0);
                purpleCircle.setImageResource(R.drawable.ic_baseline_done_24);
                greenCircle.setImageResource(0);
                orangeCircle.setImageResource(0);
                blackCircle.setImageResource(0);

                binding.addFragmentCon.setBackgroundColor(Color.parseColor("#ae3b76"));

                binding.upDatedTitle.setBackgroundColor(Color.parseColor("#ae3b76"));
                binding.upDatedSubtitle.setBackgroundColor(Color.parseColor("#ae3b76"));
                binding.upDatedContent.setBackgroundColor(Color.parseColor("#ae3b76"));
                binding.nestedScrollView.setBackgroundColor(Color.parseColor("#ae3b76"));

                binding.upDatedTitle.setHintTextColor(Color.parseColor("#FFFFFF"));
                binding.upDatedSubtitle.setHintTextColor(Color.parseColor("#FFFFFF"));
                binding.upDatedContent.setHintTextColor(Color.parseColor("#FFFFFF"));

                binding.upDatedTitle.setTextColor(Color.parseColor("#FFFFFF"));
                binding.upDatedTime.setTextColor(Color.parseColor("#FFFFFF"));
                binding.upDatedSubtitle.setTextColor(Color.parseColor("#FFFFFF"));
                binding.upDatedContent.setTextColor(Color.parseColor("#FFFFFF"));


                binding.doneBtn.setColorFilter(Color.parseColor("#FFFFFF"));
                binding.undoBtn.setColorFilter(Color.parseColor("#FFFFFF"));
                binding.redoBtn.setColorFilter(Color.parseColor("#FFFFFF"));
                binding.backButton.setColorFilter(Color.parseColor("#FFFFFF"));
                binding.more.setColorFilter(Color.parseColor("#FFFFFF"));

                bgColor = "4";

            }
        });

        greenCircle.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                blueCircle.setImageResource(0);
                yellowCircle.setImageResource(0);
                whiteCircle.setImageResource(0);
                purpleCircle.setImageResource(0);
                greenCircle.setImageResource(R.drawable.ic_baseline_done_24);
                orangeCircle.setImageResource(0);
                blackCircle.setImageResource(0);

                binding.addFragmentCon.setBackgroundColor(Color.parseColor("#0aebaf"));


                binding.upDatedTitle.setBackgroundColor(Color.parseColor("#0aebaf"));
                binding.upDatedSubtitle.setBackgroundColor(Color.parseColor("#0aebaf"));
                binding.upDatedContent.setBackgroundColor(Color.parseColor("#0aebaf"));
                binding.nestedScrollView.setBackgroundColor(Color.parseColor("#0aebaf"));

                binding.upDatedTitle.setHintTextColor(Color.parseColor("#202020"));
                binding.upDatedSubtitle.setHintTextColor(Color.parseColor("#202020"));
                binding.upDatedContent.setHintTextColor(Color.parseColor("#202020"));

                binding.upDatedTitle.setTextColor(Color.parseColor("#202020"));
                binding.upDatedTime.setTextColor(Color.parseColor("#202020"));
                binding.upDatedSubtitle.setTextColor(Color.parseColor("#202020"));
                binding.upDatedContent.setTextColor(Color.parseColor("#202020"));


                binding.doneBtn.setColorFilter(Color.parseColor("#202020"));
                binding.undoBtn.setColorFilter(Color.parseColor("#202020"));
                binding.redoBtn.setColorFilter(Color.parseColor("#202020"));
                binding.backButton.setColorFilter(Color.parseColor("#202020"));
                binding.more.setColorFilter(Color.parseColor("#202020"));

                bgColor = "5";

            }
        });

        orangeCircle.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                blueCircle.setImageResource(0);
                yellowCircle.setImageResource(0);
                whiteCircle.setImageResource(0);
                purpleCircle.setImageResource(0);
                greenCircle.setImageResource(0);
                orangeCircle.setImageResource(R.drawable.ic_baseline_done_24);
                blackCircle.setImageResource(0);

                binding.addFragmentCon.setBackgroundColor(Color.parseColor("#ff7746"));

                binding.upDatedTitle.setBackgroundColor(Color.parseColor("#ff7746"));
                binding.upDatedSubtitle.setBackgroundColor(Color.parseColor("#ff7746"));
                binding.upDatedContent.setBackgroundColor(Color.parseColor("#ff7746"));
                binding.nestedScrollView.setBackgroundColor(Color.parseColor("#ff7746"));

                binding.upDatedTitle.setHintTextColor(Color.parseColor("#FFFFFF"));
                binding.upDatedSubtitle.setHintTextColor(Color.parseColor("#FFFFFF"));
                binding.upDatedContent.setHintTextColor(Color.parseColor("#FFFFFF"));

                binding.upDatedTitle.setTextColor(Color.parseColor("#FFFFFF"));
                binding.upDatedTime.setTextColor(Color.parseColor("#FFFFFF"));
                binding.upDatedSubtitle.setTextColor(Color.parseColor("#FFFFFF"));
                binding.upDatedContent.setTextColor(Color.parseColor("#FFFFFF"));


                binding.doneBtn.setColorFilter(Color.parseColor("#FFFFFF"));
                binding.undoBtn.setColorFilter(Color.parseColor("#FFFFFF"));
                binding.redoBtn.setColorFilter(Color.parseColor("#FFFFFF"));
                binding.backButton.setColorFilter(Color.parseColor("#FFFFFF"));
                binding.more.setColorFilter(Color.parseColor("#FFFFFF"));

                bgColor = "6";

            }
        });

        blackCircle.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                blueCircle.setImageResource(0);
                yellowCircle.setImageResource(0);
                whiteCircle.setImageResource(0);
                purpleCircle.setImageResource(0);
                greenCircle.setImageResource(0);
                orangeCircle.setImageResource(0);
                blackCircle.setImageResource(R.drawable.ic_baseline_done_24);

                binding.addFragmentCon.setBackgroundColor(Color.parseColor("#0e121b"));

                binding.upDatedTitle.setBackgroundColor(Color.parseColor("#0e121b"));
                binding.upDatedSubtitle.setBackgroundColor(Color.parseColor("#0e121b"));
                binding.upDatedContent.setBackgroundColor(Color.parseColor("#0e121b"));
                binding.nestedScrollView.setBackgroundColor(Color.parseColor("#0e121b"));

                binding.upDatedTitle.setHintTextColor(Color.parseColor("#FFFFFF"));
                binding.upDatedSubtitle.setHintTextColor(Color.parseColor("#FFFFFF"));
                binding.upDatedContent.setHintTextColor(Color.parseColor("#FFFFFF"));

                binding.upDatedTitle.setTextColor(Color.parseColor("#FFFFFF"));
                binding.upDatedTime.setTextColor(Color.parseColor("#FFFFFF"));
                binding.upDatedSubtitle.setTextColor(Color.parseColor("#FFFFFF"));
                binding.upDatedContent.setTextColor(Color.parseColor("#FFFFFF"));


                binding.doneBtn.setColorFilter(Color.parseColor("#FFFFFF"));
                binding.undoBtn.setColorFilter(Color.parseColor("#FFFFFF"));
                binding.redoBtn.setColorFilter(Color.parseColor("#FFFFFF"));
                binding.backButton.setColorFilter(Color.parseColor("#FFFFFF"));
                binding.more.setColorFilter(Color.parseColor("#FFFFFF"));


                bgColor = "7";

            }
        });



        addImage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                dialog.dismiss();
                if(EasyPermissions.hasPermissions(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE))
                {
                    selectImage();
                }else
                {
                    EasyPermissions.requestPermissions(requireActivity(),"In case you want to add image need to grant permission",REQUEST_CODE_STORAGE_PERMISSION,
                            Manifest.permission.READ_EXTERNAL_STORAGE);

                }
            }
        });






    }



    private void selectImage()
    {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        if(intent.resolveActivity(getActivity().getPackageManager())!= null)
        {
            startActivityForResult(intent,REQUEST_CODE_SELECT_IMAGE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(data != null)
        {
            Uri selectedImageUri = data.getData();

            try
            {
                InputStream inputStream = getActivity().getContentResolver().openInputStream(selectedImageUri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                binding.noteImage.setImageBitmap(bitmap);
                binding.noteImage.setVisibility(View.VISIBLE);

            }catch (Exception exception)
            {

            }

            selectedImagePath = getImagePathFromUri(selectedImageUri);
            Log.e("TAG","OnactivityResult:  "+selectedImagePath);
        }


    }

    private String getImagePathFromUri(Uri uri)
    {
        String filePath;

        Cursor cursor = requireActivity().getContentResolver().query(uri,null,null,null,null);

        if(cursor == null)
        {
            filePath = uri.getPath();
        }else
        {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex("_data");
            filePath = cursor.getString(index);
            cursor.close();

        }

        return filePath;
    }







    private boolean isEven(int number)
    {
        return number % 2 == 0;
    }












}