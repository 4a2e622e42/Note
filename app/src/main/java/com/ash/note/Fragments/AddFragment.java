package com.ash.note.Fragments;





import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
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

import com.ash.note.ViewModel.AppViewModel;
import com.ash.note.Model.Note;
import com.ash.note.R;
import com.ash.note.TextUndoRedo;
import com.ash.note.databinding.FragmentAddBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.InputStream;

import pub.devrel.easypermissions.EasyPermissions;
import saman.zamani.persiandate.PersianDate;
import saman.zamani.persiandate.PersianDateFormat;


public class AddFragment extends Fragment
{
    FragmentAddBinding binding;
    AppViewModel appViewModel;
    BottomSheetDialog dialog;

    Vibrator vibrator;


    String colorPicked = "7";

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


        PersianDate persianDate = new PersianDate();

        PersianDateFormat persianDateFormat = new PersianDateFormat("l ،  H:i", PersianDateFormat.PersianDateNumberCharacter.FARSI);
        persianDateFormat.format(persianDate);

        String time =  persianDateFormat.format(persianDate);


        binding.time.setText(time+" | "+totalCharNumber+" حرف");


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
                binding.time.setText(time+" | "+totalCharNumber+" حرف");

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
                binding.time.setText(time+" | "+totalCharNumber+" حرف");



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

        PersianDate persianDate = new PersianDate();

        PersianDateFormat persianDateFormat = new PersianDateFormat("Y/m/d  H:i:s");

        String date =  persianDateFormat.format(persianDate);


        Note note = new Note();
        note.setTitle(binding.title.getText().toString());
        note.setContent(binding.content.getText().toString());
        note.setDate(date);
        note.setBgColor(colorPicked);
        note.setCharNumber(totalCharNumber);
        note.setPinned(isPinned);
        note.setImagePath(selectedImagePath);

        if(!binding.title.getText().toString().isEmpty()  || !binding.content.getText().toString().isEmpty())
        {
            appViewModel.addNotes(note);

        }else
        {
            Toast.makeText(requireActivity(),"یادداشت شما خالی بود",Toast.LENGTH_LONG).show();
        }


    }
    private void setUpDialog()
    {
        View v = getLayoutInflater().inflate(R.layout.bottom_sheet_dialog,null);

        dialog.setContentView(v);
        TextView pined,addImage,deleteText,makeCopy,shareTxt;

        ImageView blueCircle,yellowCircle,whiteCircle,purpleCircle,greenCircle,orangeCircle,blackCircle,pinImage;
        blueCircle = v.findViewById(R.id.blueCircle);
        yellowCircle = v.findViewById(R.id.yellowCircle);
        whiteCircle = v.findViewById(R.id.whiteCircle);
        purpleCircle = v.findViewById(R.id.purpleCircle);
        greenCircle = v.findViewById(R.id.greenCircle);
        orangeCircle = v.findViewById(R.id.orangeCircle);
        blackCircle = v.findViewById(R.id.blackCircle);

        pined    =   v.findViewById(R.id.pinText);
        pinImage = v.findViewById(R.id.pinImage);
        addImage = v.findViewById(R.id.addImageText);
        deleteText = v.findViewById(R.id.deleteText);
        makeCopy = v.findViewById(R.id.makeCopyText);
        shareTxt = v.findViewById(R.id.shareText);

        deleteText.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Toast.makeText(requireActivity(),"چیزی که وجود نداره رو نمیتونی حذف کنی",Toast.LENGTH_LONG).show();
            }
        });

        shareTxt.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(!binding.title.getText().toString().isEmpty()  || !binding.content.getText().toString().isEmpty() )
                {
                    String shareContent = binding.title.getText().toString()+"\n"+binding.content.getText().toString();
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_SEND);
                    intent.putExtra(Intent.EXTRA_TEXT,shareContent);
                    intent.setType("text/plain");

                    Intent shareIntent = Intent.createChooser(intent,"ارسال با");
                    startActivity(shareIntent);

                }else
                {
                    Toast.makeText(requireActivity(),"یادداشت شما خالی است",Toast.LENGTH_SHORT).show();
                }

            }
        });

        makeCopy.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                ClipboardManager clipboardManager = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("label",binding.title.getText().toString() +"\n"+ binding.content.getText().toString());
                clipboardManager.setPrimaryClip(clipData);
                Toast.makeText(requireActivity()," کپی  شد",Toast.LENGTH_SHORT).show();

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
                    pined.setText("سنجاق نشه");
                    isPinned = true;
                }else
                {
                    pinImage.setImageResource(R.drawable.ic_baseline_push_pin_24);
                    pined.setText("سنجاق بشه");
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
                binding.content.setBackgroundColor(Color.parseColor("#3369ff"));
                binding.nestedScrollView.setBackgroundColor(Color.parseColor("#3369ff"));

                binding.title.setHintTextColor(Color.WHITE);
                binding.content.setHintTextColor(Color.WHITE);

                binding.title.setTextColor(Color.WHITE);
                binding.time.setTextColor(Color.WHITE);
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
                binding.content.setBackgroundColor(Color.parseColor("#ffda47"));
                binding.nestedScrollView.setBackgroundColor(Color.parseColor("#ffda47"));


                binding.title.setHintTextColor(Color.parseColor("#101920"));
                binding.content.setHintTextColor(Color.parseColor("#101920"));

                binding.title.setTextColor(Color.parseColor("#101920"));
                binding.time.setTextColor(Color.parseColor("#101920"));
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
                binding.content.setBackgroundColor(Color.parseColor("#FFFFFF"));
                binding.nestedScrollView.setBackgroundColor(Color.parseColor("#FFFFFF"));

                binding.title.setHintTextColor(Color.parseColor("#6c7589"));
                binding.content.setHintTextColor(Color.parseColor("#6c7589"));

                binding.title.setTextColor(Color.parseColor("#202020"));
                binding.time.setTextColor(Color.parseColor("#202020"));
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
                binding.content.setBackgroundColor(Color.parseColor("#ae3b76"));
                binding.nestedScrollView.setBackgroundColor(Color.parseColor("#ae3b76"));

                binding.title.setHintTextColor(Color.parseColor("#FFFFFF"));
                binding.content.setHintTextColor(Color.parseColor("#FFFFFF"));

                binding.title.setTextColor(Color.parseColor("#FFFFFF"));
                binding.time.setTextColor(Color.parseColor("#FFFFFF"));
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
                binding.content.setBackgroundColor(Color.parseColor("#0aebaf"));
                binding.nestedScrollView.setBackgroundColor(Color.parseColor("#0aebaf"));

                binding.title.setHintTextColor(Color.parseColor("#202020"));
                binding.content.setHintTextColor(Color.parseColor("#202020"));

                binding.title.setTextColor(Color.parseColor("#202020"));
                binding.time.setTextColor(Color.parseColor("#202020"));
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
                binding.content.setBackgroundColor(Color.parseColor("#ff7746"));
                binding.nestedScrollView.setBackgroundColor(Color.parseColor("#ff7746"));

                binding.title.setHintTextColor(Color.parseColor("#FFFFFF"));
                binding.content.setHintTextColor(Color.parseColor("#FFFFFF"));

                binding.title.setTextColor(Color.parseColor("#FFFFFF"));
                binding.time.setTextColor(Color.parseColor("#FFFFFF"));
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
                binding.content.setBackgroundColor(Color.parseColor("#0e121b"));
                binding.nestedScrollView.setBackgroundColor(Color.parseColor("#0e121b"));

                binding.title.setHintTextColor(Color.parseColor("#FFFFFF"));
                binding.content.setHintTextColor(Color.parseColor("#FFFFFF"));

                binding.title.setTextColor(Color.parseColor("#FFFFFF"));
                binding.time.setTextColor(Color.parseColor("#FFFFFF"));
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
                  EasyPermissions.requestPermissions(requireActivity(),"اگر قضد اسافه کردن عکس دارید باید اجازه دسترسی را به برنامه بدهید",REQUEST_CODE_STORAGE_PERMISSION,
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