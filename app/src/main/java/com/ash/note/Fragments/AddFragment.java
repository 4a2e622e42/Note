package com.ash.note.Fragments;





import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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
import com.ash.note.databinding.FragmentAddBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import pub.devrel.easypermissions.EasyPermissions;


public class AddFragment extends Fragment
{
    FragmentAddBinding binding;
    AppViewModel appViewModel;
    BottomSheetDialog dialog;

    Vibrator vibrator;


    String colorPicked = "3";

    private String selectedImagePath;

    boolean isPinned;
    int clickCount = 0;



    int titleCharNumber    = 0;
    int subTitleCharNumber = 0;
    int contentCharNumber  = 0;
    int totalCharNumber    = 0;


    private static final int REQUEST_CODE_STORAGE_PERMISSION = 1;
    private static final int REQUEST_CODE_SELECT_IMAGE = 2;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
    {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_add, container, false);
        appViewModel = new ViewModelProvider(this).get(AppViewModel.class);




        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, HH:mm");
        String time = simpleDateFormat.format(new Date());

        binding.time.setText(time+" | "+totalCharNumber+" Character");


       vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);




      dialog = new BottomSheetDialog(requireActivity());

       setUpDialog();
       binding.more.setOnClickListener(new View.OnClickListener()
       {
           @Override
           public void onClick(View view)
           {
               dialog.show();
           }
       });

       dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);





//-------------------------------------------------------------------------------------------------------------------------------------------------------
        //Count of Character by watching editTexts

        binding.title.addTextChangedListener(new TextWatcher()
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

            }

            @Override
            public void afterTextChanged(Editable editable)
            {
                totalCharNumber = titleCharNumber + contentCharNumber + subTitleCharNumber;
                binding.time.setText(time+" | "+totalCharNumber+" Character");

            }
        });


        binding.subtitle.addTextChangedListener(new TextWatcher()
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

            }

            @Override
            public void afterTextChanged(Editable editable)
            {

                totalCharNumber = titleCharNumber + contentCharNumber + subTitleCharNumber;
                binding.time.setText(time+" | "+totalCharNumber+" Character");
            }
        });



        binding.content.addTextChangedListener(new TextWatcher() {
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
            }

            @Override
            public void afterTextChanged(Editable editable)
            {
                totalCharNumber = titleCharNumber + contentCharNumber + subTitleCharNumber;
                binding.time.setText(time+" | "+totalCharNumber+" Character");



            }
        });



//-------------------------------------------------------------------------------------------------------------------------------------------------------


        //Undo & Redo Operation

        TextUndoRedo.TextChangeInfo textChangeInfo = () -> {};
        TextUndoRedo contentUndoRedo    =   new TextUndoRedo(binding.content ,textChangeInfo);


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


//--------------------------------------------------------------------------------------------------------------------------------------------------------





        binding.doneBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                insertData();
                Navigation.findNavController(view).navigate(R.id.action_addFragment_to_homeFragment);
            }
        });


        binding.backButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                requireActivity().onBackPressed();
                insertData();
            }
        });



        return binding.getRoot();

    }

    private void insertData()
    {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy  HH:mm:ss", Locale.getDefault());
        String noteTime = simpleDateFormat.format(new Date());

        Note note = new Note();
        note.setTitle(binding.title.getText().toString());
        note.setSubTitle(binding.subtitle.getText().toString());
        note.setContent(binding.content.getText().toString());
        note.setDate(noteTime);
        note.setBgColor(colorPicked);
        note.setCharNumber(totalCharNumber);
        note.setPinned(isPinned);
        note.setImagePath(selectedImagePath);


        appViewModel.addNotes(note);





    }
    private void setUpDialog()
    {
        View view = getLayoutInflater().inflate(R.layout.bottom_sheet_dialog,null);

        dialog.setContentView(view);
        TextView pined,addImage,deleteText,makeCopy;

        ImageView blueCircle,yellowCircle,whiteCircle,purpleCircle,greenCircle,orangeCircle,blackCircle,pinImage;
        blueCircle = view.findViewById(R.id.blueCircle);
        yellowCircle = view.findViewById(R.id.yellowCircle);
        whiteCircle = view.findViewById(R.id.whiteCircle);
        purpleCircle = view.findViewById(R.id.purpleCircle);
        greenCircle = view.findViewById(R.id.greenCircle);
        orangeCircle = view.findViewById(R.id.orangeCircle);
        blackCircle = view.findViewById(R.id.blackCircle);


        pined    =   view.findViewById(R.id.pinText);
        pinImage = view.findViewById(R.id.pinImage);
        addImage = view.findViewById(R.id.addImageText);
        deleteText = view.findViewById(R.id.deleteText);
        makeCopy = view.findViewById(R.id.makeCopyText);

        deleteText.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Toast.makeText(requireActivity()," You Can't Delete What's Already Doesn't exist",Toast.LENGTH_LONG).show();
            }
        });

        makeCopy.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                insertData();
                Toast.makeText(requireActivity()," Copy Created",Toast.LENGTH_SHORT).show();

            }
        });




        pined.setOnClickListener(new View.OnClickListener()
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
                    pined.setText("Unpin");
                    isPinned = true;
                }else
                {
                    pinImage.setImageResource(R.drawable.ic_baseline_push_pin_24);
                    pined.setText("Pin");
                    isPinned = false;
                }

                clickCount ++;

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
                binding.title.setBackgroundColor(Color.parseColor("#3369ff"));
                binding.subtitle.setBackgroundColor(Color.parseColor("#3369ff"));
                binding.content.setBackgroundColor(Color.parseColor("#3369ff"));
                binding.nestedScrollView.setBackgroundColor(Color.parseColor("#3369ff"));

                binding.title.setHintTextColor(Color.WHITE);
                binding.subtitle.setHintTextColor(Color.WHITE);
                binding.content.setHintTextColor(Color.WHITE);

                binding.title.setTextColor(Color.WHITE);
                binding.time.setTextColor(Color.WHITE);
                binding.subtitle.setTextColor(Color.WHITE);
                binding.content.setTextColor(Color.WHITE);


                binding.doneBtn.setColorFilter(Color.parseColor("#FFFFFF"));
                binding.undoBtn.setColorFilter(Color.parseColor("#FFFFFF"));
                binding.redoBtn.setColorFilter(Color.parseColor("#FFFFFF"));
                binding.backButton.setColorFilter(Color.parseColor("#FFFFFF"));
                binding.more.setColorFilter(Color.parseColor("#FFFFFF"));


                colorPicked = "1";
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
                binding.title.setBackgroundColor(Color.parseColor("#ffda47"));
                binding.subtitle.setBackgroundColor(Color.parseColor("#ffda47"));
                binding.content.setBackgroundColor(Color.parseColor("#ffda47"));
                binding.nestedScrollView.setBackgroundColor(Color.parseColor("#ffda47"));


                binding.title.setHintTextColor(Color.parseColor("#101920"));
                binding.subtitle.setHintTextColor(Color.parseColor("#101920"));
                binding.content.setHintTextColor(Color.parseColor("#101920"));

                binding.title.setTextColor(Color.parseColor("#101920"));
                binding.time.setTextColor(Color.parseColor("#101920"));
                binding.subtitle.setTextColor(Color.parseColor("#101920"));
                binding.content.setTextColor(Color.parseColor("#101920"));


                binding.doneBtn.setColorFilter(Color.parseColor("#101920"));
                binding.undoBtn.setColorFilter(Color.parseColor("#101920"));
                binding.redoBtn.setColorFilter(Color.parseColor("#101920"));
                binding.backButton.setColorFilter(Color.parseColor("#101920"));
                binding.more.setColorFilter(Color.parseColor("#101920"));




                colorPicked = "2";
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

                binding.title.setBackgroundColor(Color.parseColor("#FFFFFF"));
                binding.subtitle.setBackgroundColor(Color.parseColor("#FFFFFF"));
                binding.content.setBackgroundColor(Color.parseColor("#FFFFFF"));
                binding.nestedScrollView.setBackgroundColor(Color.parseColor("#FFFFFF"));

                binding.title.setHintTextColor(Color.parseColor("#6c7589"));
                binding.subtitle.setHintTextColor(Color.parseColor("#6c7589"));
                binding.content.setHintTextColor(Color.parseColor("#6c7589"));

                binding.title.setTextColor(Color.parseColor("#202020"));
                binding.time.setTextColor(Color.parseColor("#202020"));
                binding.subtitle.setTextColor(Color.parseColor("#202020"));
                binding.content.setTextColor(Color.parseColor("#202020"));


                binding.doneBtn.setColorFilter(Color.parseColor("#3369ff"));
                binding.undoBtn.setColorFilter(Color.parseColor("#3369ff"));
                binding.redoBtn.setColorFilter(Color.parseColor("#3369ff"));
                binding.backButton.setColorFilter(Color.parseColor("#3369ff"));
                binding.more.setColorFilter(Color.parseColor("#3369ff"));







                colorPicked = "3";
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

                binding.title.setBackgroundColor(Color.parseColor("#ae3b76"));
                binding.subtitle.setBackgroundColor(Color.parseColor("#ae3b76"));
                binding.content.setBackgroundColor(Color.parseColor("#ae3b76"));
                binding.nestedScrollView.setBackgroundColor(Color.parseColor("#ae3b76"));

                binding.title.setHintTextColor(Color.parseColor("#FFFFFF"));
                binding.subtitle.setHintTextColor(Color.parseColor("#FFFFFF"));
                binding.content.setHintTextColor(Color.parseColor("#FFFFFF"));

                binding.title.setTextColor(Color.parseColor("#FFFFFF"));
                binding.time.setTextColor(Color.parseColor("#FFFFFF"));
                binding.subtitle.setTextColor(Color.parseColor("#FFFFFF"));
                binding.content.setTextColor(Color.parseColor("#FFFFFF"));


                binding.doneBtn.setColorFilter(Color.parseColor("#FFFFFF"));
                binding.undoBtn.setColorFilter(Color.parseColor("#FFFFFF"));
                binding.redoBtn.setColorFilter(Color.parseColor("#FFFFFF"));
                binding.backButton.setColorFilter(Color.parseColor("#FFFFFF"));
                binding.more.setColorFilter(Color.parseColor("#FFFFFF"));






                colorPicked = "4";
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


                binding.title.setBackgroundColor(Color.parseColor("#0aebaf"));
                binding.subtitle.setBackgroundColor(Color.parseColor("#0aebaf"));
                binding.content.setBackgroundColor(Color.parseColor("#0aebaf"));
                binding.nestedScrollView.setBackgroundColor(Color.parseColor("#0aebaf"));

                binding.title.setHintTextColor(Color.parseColor("#202020"));
                binding.subtitle.setHintTextColor(Color.parseColor("#202020"));
                binding.content.setHintTextColor(Color.parseColor("#202020"));

                binding.title.setTextColor(Color.parseColor("#202020"));
                binding.time.setTextColor(Color.parseColor("#202020"));
                binding.subtitle.setTextColor(Color.parseColor("#202020"));
                binding.content.setTextColor(Color.parseColor("#202020"));


                binding.doneBtn.setColorFilter(Color.parseColor("#202020"));
                binding.undoBtn.setColorFilter(Color.parseColor("#202020"));
                binding.redoBtn.setColorFilter(Color.parseColor("#202020"));
                binding.backButton.setColorFilter(Color.parseColor("#202020"));
                binding.more.setColorFilter(Color.parseColor("#202020"));







                colorPicked = "5";
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

                binding.title.setBackgroundColor(Color.parseColor("#ff7746"));
                binding.subtitle.setBackgroundColor(Color.parseColor("#ff7746"));
                binding.content.setBackgroundColor(Color.parseColor("#ff7746"));
                binding.nestedScrollView.setBackgroundColor(Color.parseColor("#ff7746"));

                binding.title.setHintTextColor(Color.parseColor("#FFFFFF"));
                binding.subtitle.setHintTextColor(Color.parseColor("#FFFFFF"));
                binding.content.setHintTextColor(Color.parseColor("#FFFFFF"));

                binding.title.setTextColor(Color.parseColor("#FFFFFF"));
                binding.time.setTextColor(Color.parseColor("#FFFFFF"));
                binding.subtitle.setTextColor(Color.parseColor("#FFFFFF"));
                binding.content.setTextColor(Color.parseColor("#FFFFFF"));


                binding.doneBtn.setColorFilter(Color.parseColor("#FFFFFF"));
                binding.undoBtn.setColorFilter(Color.parseColor("#FFFFFF"));
                binding.redoBtn.setColorFilter(Color.parseColor("#FFFFFF"));
                binding.backButton.setColorFilter(Color.parseColor("#FFFFFF"));
                binding.more.setColorFilter(Color.parseColor("#FFFFFF"));



                colorPicked = "6";
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

                binding.title.setBackgroundColor(Color.parseColor("#0e121b"));
                binding.subtitle.setBackgroundColor(Color.parseColor("#0e121b"));
                binding.content.setBackgroundColor(Color.parseColor("#0e121b"));
                binding.nestedScrollView.setBackgroundColor(Color.parseColor("#0e121b"));

                binding.title.setHintTextColor(Color.parseColor("#FFFFFF"));
                binding.subtitle.setHintTextColor(Color.parseColor("#FFFFFF"));
                binding.content.setHintTextColor(Color.parseColor("#FFFFFF"));

                binding.title.setTextColor(Color.parseColor("#FFFFFF"));
                binding.time.setTextColor(Color.parseColor("#FFFFFF"));
                binding.subtitle.setTextColor(Color.parseColor("#FFFFFF"));
                binding.content.setTextColor(Color.parseColor("#FFFFFF"));


                binding.doneBtn.setColorFilter(Color.parseColor("#FFFFFF"));
                binding.undoBtn.setColorFilter(Color.parseColor("#FFFFFF"));
                binding.redoBtn.setColorFilter(Color.parseColor("#FFFFFF"));
                binding.backButton.setColorFilter(Color.parseColor("#FFFFFF"));
                binding.more.setColorFilter(Color.parseColor("#FFFFFF"));







                colorPicked = "7";
            }
        });


        addImage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
              dialog.dismiss();
              if(EasyPermissions.hasPermissions(requireActivity(),Manifest.permission.READ_EXTERNAL_STORAGE))
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
            Log.e("TAG","Path:  "+selectedImagePath);
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


    @Override
    public void onDestroy()
    {
        super.onDestroy();
        insertData();
        Log.e("TAG","onDestroy");
    }



}