package com.ash.note;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.ash.note.databinding.BottomSheetDialogBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BottomSheet extends BottomSheetDialogFragment
{
    BottomSheetDialogBinding sheetDialogBinding;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        sheetDialogBinding = DataBindingUtil.inflate(inflater,R.layout.bottom_sheet_dialog,container,false);








        return sheetDialogBinding.getRoot();
    }
}
