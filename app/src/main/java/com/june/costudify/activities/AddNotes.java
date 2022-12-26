package com.june.costudify.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
//import android.content.AsyncQueryHandler;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
//import android.widget.ArrayAdapter;
import android.widget.Button;
//import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.june.costudify.R;
import com.june.costudify.database.NotesDatabase;
import com.june.costudify.entities.Notes;

import java.io.InputStream;
import java.text.SimpleDateFormat;
//import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddNotes extends AppCompatActivity {
    LinearLayout openAs;
    private EditText noteTitleInput, noteContentInput;
    private TextView noteDateTime;
    private View viewTitleColorIndicator;
    private String selectedNoteColor;
    private ImageView imageNote;
    private String selectedImagePath;
    private Notes alreadyAvailableNote;
    private AlertDialog dialogDeleteNote;

    private static final int REQUEST_CODE_STORAGE_PERMISSION = 1;
    private static final int REQUEST_CODE_SELECT_IMAGE = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_CoStudify);
        setContentView(R.layout.activity_add_notes);

        ImageView bckNotes = findViewById(R.id.backNotes);
        bckNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        ImageView saveNotes = findViewById(R.id.saveNotes);
        saveNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNote();
            }
        });

        imageNote = findViewById(R.id.imageNote);
        noteContentInput = findViewById(R.id.contentInput);
        noteTitleInput = findViewById(R.id.tileInput);
        selectedNoteColor = "#212529";
        selectedImagePath = "";
        viewTitleColorIndicator = findViewById(R.id.noteTitleIndicator);
        noteDateTime = findViewById(R.id.textDateTime);

        noteDateTime.setText(
                new SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault())
                        .format(new Date())
        );

        if (getIntent().getBooleanExtra("isViewOrUpdate", false)) {
            alreadyAvailableNote = (Notes) getIntent().getSerializableExtra("note");
            setViewOrUpdateNote();
        }

        findViewById(R.id.removeImageBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageNote.setImageBitmap(null);
                imageNote.setVisibility(View.GONE);
                findViewById(R.id.removeImageBtn).setVisibility(View.GONE);
                selectedImagePath = "";
            }
        });

        initMiscellaneous();
        setViewTitleColorIndicator();
    }

    private void setViewOrUpdateNote() {
        noteTitleInput.setText(alreadyAvailableNote.getTitle());
        noteContentInput.setText(alreadyAvailableNote.getNoteText());
        noteDateTime.setText(alreadyAvailableNote.getDateCreated());
        if (alreadyAvailableNote.getImagePath() != null && !alreadyAvailableNote.getImagePath().trim().isEmpty()) {
            imageNote.setImageBitmap(BitmapFactory.decodeFile(alreadyAvailableNote.getImagePath()));
            imageNote.setVisibility(View.VISIBLE);
            findViewById(R.id.removeImageBtn).setVisibility(View.VISIBLE);
            selectedImagePath = alreadyAvailableNote.getImagePath();
        }

    }

    private void saveNote() {
        if (noteTitleInput.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Note title can't be empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        final Notes notes = new Notes();
        notes.setTitle(noteTitleInput.getText().toString());
        notes.setNoteText(noteContentInput.getText().toString());
        notes.setDateCreated(noteDateTime.getText().toString());
        notes.setColor(selectedNoteColor);
        notes.setImagePath(selectedImagePath);

        if (alreadyAvailableNote != null) {
            notes.setId(alreadyAvailableNote.getId());
        }


        @SuppressLint("StaticFieldLeak")
        class SaveNoteTask extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids) {
                NotesDatabase.getDatabase(getApplicationContext()).noteDao().insertNote(notes);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        }

        new SaveNoteTask().execute();
    }

    private void initMiscellaneous() {
        final LinearLayout layoutMiscellaneous_color = findViewById(R.id.add_notes_miscellaneous);
        final BottomSheetBehavior<LinearLayout> bottomSheetBehavior_color = BottomSheetBehavior.from(layoutMiscellaneous_color);
        final ImageButton addColorMisc = findViewById(R.id.add_color_miscellaneous);
        final LinearLayout layoutMiscellaneous_img = findViewById(R.id.add_img_miscellaneous);
        final BottomSheetBehavior<LinearLayout> bottomSheetBehavior_img = BottomSheetBehavior.from(layoutMiscellaneous_img);
        final ImageButton addImageMisc = findViewById(R.id.add_image_miscellaneous);
        final LinearLayout layoutMiscellaneous_settings = findViewById(R.id.settings_miscellaneous);
        final BottomSheetBehavior<LinearLayout> bottomSheetBehavior_settings = BottomSheetBehavior.from(layoutMiscellaneous_settings);
        final ImageButton settingsMisc = findViewById(R.id.setting_miscellaneous);

        addColorMisc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bottomSheetBehavior_color.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                    bottomSheetBehavior_color.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    bottomSheetBehavior_color.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });

        addImageMisc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bottomSheetBehavior_img.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                    bottomSheetBehavior_img.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    bottomSheetBehavior_img.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });

        settingsMisc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bottomSheetBehavior_settings.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                    bottomSheetBehavior_settings.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    bottomSheetBehavior_settings.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });

        layoutMiscellaneous_color.findViewById(R.id.viewColor1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedNoteColor = "#dee2e6";
                setViewTitleColorIndicator();
            }
        });

        layoutMiscellaneous_color.findViewById(R.id.viewColor2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedNoteColor = "#ced4da";
                setViewTitleColorIndicator();
            }
        });

        layoutMiscellaneous_color.findViewById(R.id.viewColor3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedNoteColor = "#6c757d";
                setViewTitleColorIndicator();
            }
        });

        layoutMiscellaneous_color.findViewById(R.id.viewColor4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedNoteColor = "#343a40";
                setViewTitleColorIndicator();
            }
        });

        layoutMiscellaneous_color.findViewById(R.id.viewColor5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedNoteColor = "#212529";
                setViewTitleColorIndicator();
            }
        });

        layoutMiscellaneous_color.findViewById(R.id.viewColor6).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedNoteColor = "#CDB4DB";
                setViewTitleColorIndicator();
            }
        });

        layoutMiscellaneous_color.findViewById(R.id.viewColor7).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedNoteColor = "#FFC8DD";
                setViewTitleColorIndicator();
            }
        });

        layoutMiscellaneous_color.findViewById(R.id.viewColor8).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedNoteColor = "#FFAFCC";
                setViewTitleColorIndicator();
            }
        });

        layoutMiscellaneous_color.findViewById(R.id.viewColor9).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedNoteColor = "#BDE0FE";
                setViewTitleColorIndicator();
            }
        });

        layoutMiscellaneous_color.findViewById(R.id.viewColor10).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedNoteColor = "#A2D2FF";
                setViewTitleColorIndicator();
            }
        });

        layoutMiscellaneous_color.findViewById(R.id.viewColor11).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedNoteColor = "#A4AC86";
                setViewTitleColorIndicator();
            }
        });

        layoutMiscellaneous_color.findViewById(R.id.viewColor12).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedNoteColor = "#656D4A";
                setViewTitleColorIndicator();
            }
        });

        layoutMiscellaneous_color.findViewById(R.id.viewColor13).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedNoteColor = "#414833";
                setViewTitleColorIndicator();
            }
        });

        layoutMiscellaneous_color.findViewById(R.id.viewColor14).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedNoteColor = "#333D29";
                setViewTitleColorIndicator();
            }
        });

        layoutMiscellaneous_color.findViewById(R.id.viewColor15).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedNoteColor = "#582F0E";
                setViewTitleColorIndicator();
            }
        });

        layoutMiscellaneous_color.findViewById(R.id.viewColor16).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedNoteColor = "#CCDCFF";
                setViewTitleColorIndicator();
            }
        });

        layoutMiscellaneous_color.findViewById(R.id.viewColor17).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedNoteColor = "#9A99F2";
                setViewTitleColorIndicator();
            }
        });

        layoutMiscellaneous_color.findViewById(R.id.viewColor18).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedNoteColor = "#805EBF";
                setViewTitleColorIndicator();
            }
        });

        layoutMiscellaneous_color.findViewById(R.id.viewColor19).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedNoteColor = "#511F73";
                setViewTitleColorIndicator();
            }
        });

        layoutMiscellaneous_color.findViewById(R.id.viewColor20).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedNoteColor = "#431259";
                setViewTitleColorIndicator();
            }
        });

        layoutMiscellaneous_img.findViewById(R.id.layout_add_img_gallery).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetBehavior_img.setState(BottomSheetBehavior.STATE_COLLAPSED);
                if (ContextCompat.checkSelfPermission(
                        getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                            AddNotes.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQUEST_CODE_STORAGE_PERMISSION
                    );
                } else {
                    selectImage();
                }
            }
        });

        if (alreadyAvailableNote != null && alreadyAvailableNote.getColor() != null && !alreadyAvailableNote.getColor().trim().isEmpty()) {
            switch (alreadyAvailableNote.getColor()) {
                case "#dee2e6":
                    layoutMiscellaneous_color.findViewById(R.id.viewColor1).performClick();
                    break;
                case "#ced4da":
                    layoutMiscellaneous_color.findViewById(R.id.viewColor2).performClick();
                    break;
                case "#6c757d":
                    layoutMiscellaneous_color.findViewById(R.id.viewColor3).performClick();
                    break;
                case "#343a40":
                    layoutMiscellaneous_color.findViewById(R.id.viewColor4).performClick();
                    break;
                case "#CDB4DB":
                    layoutMiscellaneous_color.findViewById(R.id.viewColor6).performClick();
                    break;
                case "#FFC8DD":
                    layoutMiscellaneous_color.findViewById(R.id.viewColor7).performClick();
                    break;
                case "#FFAFCC":
                    layoutMiscellaneous_color.findViewById(R.id.viewColor8).performClick();
                    break;
                case "#BDE0FE":
                    layoutMiscellaneous_color.findViewById(R.id.viewColor9).performClick();
                    break;
                case "#A2D2FF":
                    layoutMiscellaneous_color.findViewById(R.id.viewColor10).performClick();
                    break;
                case "#A4AC86":
                    layoutMiscellaneous_color.findViewById(R.id.viewColor11).performClick();
                    break;
                case "#656D4A":
                    layoutMiscellaneous_color.findViewById(R.id.viewColor12).performClick();
                    break;
                case "#414833":
                    layoutMiscellaneous_color.findViewById(R.id.viewColor13).performClick();
                    break;
                case "#333D29":
                    layoutMiscellaneous_color.findViewById(R.id.viewColor14).performClick();
                    break;
                case "#582F0E":
                    layoutMiscellaneous_color.findViewById(R.id.viewColor15).performClick();
                    break;
                case "#CCDCFF":
                    layoutMiscellaneous_color.findViewById(R.id.viewColor16).performClick();
                    break;
                case "#9A99F2":
                    layoutMiscellaneous_color.findViewById(R.id.viewColor17).performClick();
                    break;
                case "#805EBF":
                    layoutMiscellaneous_color.findViewById(R.id.viewColor18).performClick();
                    break;
                case "#511F73":
                    layoutMiscellaneous_color.findViewById(R.id.viewColor19).performClick();
                    break;
                case "#431259":
                    layoutMiscellaneous_color.findViewById(R.id.viewColor20).performClick();
                    break;
            }
        }

        if (alreadyAvailableNote != null) {
            layoutMiscellaneous_settings.findViewById(R.id.layout_delete).setVisibility(View.VISIBLE);
            layoutMiscellaneous_settings.findViewById(R.id.layout_delete).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bottomSheetBehavior_settings.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    showDeleteNoteDialog();
                }
            });
        }
    }

    private void showDeleteNoteDialog() {
        if (dialogDeleteNote == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(AddNotes.this);
            View view = LayoutInflater.from(this).inflate(
                    R.layout.layout_delete_note,
                    (ViewGroup) findViewById(R.id.layoutDeleteNoteContainer)
            );
            builder.setView(view);
            dialogDeleteNote = builder.create();
            if (dialogDeleteNote.getWindow() != null){
                dialogDeleteNote.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }
            view.findViewById(R.id.textDeleteNote).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    @SuppressLint("StaticFieldLeak")
                    class DeleteNoteTask extends AsyncTask<Void, Void, Void>{

                        @Override
                        protected Void doInBackground(Void... voids) {
                            NotesDatabase.getDatabase(getApplicationContext()).noteDao()
                                    .deleteNote(alreadyAvailableNote);
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void aVoid) {
                            super.onPostExecute(aVoid);
                            Intent intent= new Intent();
                            intent.putExtra("isNoteDeleted", true);
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    }

                    new DeleteNoteTask().execute();
                }
            });
            view.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogDeleteNote.dismiss();
                }
            });
        }

        dialogDeleteNote.show();
    }

    private void setViewTitleColorIndicator() {
        GradientDrawable gradientDrawable = (GradientDrawable) viewTitleColorIndicator.getBackground();
        gradientDrawable.setColor(Color.parseColor(selectedNoteColor));
    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                selectImage();
            } else {
                Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == RESULT_OK) {
            if (data != null) {
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null) {
                    try {

                        InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        imageNote.setImageBitmap(bitmap);
                        imageNote.setVisibility(View.VISIBLE);
                        findViewById(R.id.removeImageBtn).setVisibility(View.VISIBLE);

                        selectedImagePath = getPathFromUri(selectedImageUri);


                    } catch (Exception exception) {
                        Toast.makeText(this, exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    private String getPathFromUri(Uri contentUri) {
        String filePath;
        Cursor cursor = getContentResolver()
                .query(contentUri, null, null, null, null);
        if (cursor == null) {
            filePath = contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex("_data");
            filePath = cursor.getString(index);
            cursor.close();
        }
        return filePath;
    }
}