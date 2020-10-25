package com.example.przemek.gymdiary;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.ArrayMap;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.bumptech.glide.request.RequestOptions;
import com.example.przemek.gymdiary.Activities.LoginActivity;
import com.example.przemek.gymdiary.Models.HelpfulModels.HelpfulUser;
import com.example.przemek.gymdiary.Models.User;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class Helper {

    // Metoda sprawdzająca posiadane uprawnienia do pamięci wbudowanej

    public static boolean check_storage_permissions(Activity context) {

        return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    public static int getAge(String dob) {

        Calendar dayOfBirth = Calendar.getInstance();
        Calendar today = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("DD.MM.YYYY", Locale.GERMAN);
        try {
            dayOfBirth.setTime(sdf.parse(dob));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        int age = today.get(Calendar.YEAR) - dayOfBirth.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dayOfBirth.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }


        return age;

    }

    public static void bringImagePicker(Context context, Fragment fragment) {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .start(context, fragment);
    }

    public static void bringImagePicker(Activity activity) {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .start(activity);
    }


    public static void sendToLoginActivity(Activity activity) {

        Intent i = new Intent(activity, LoginActivity.class);
        activity.startActivity(i);
        activity.finish();

    }

    public static String DateFormat(Date date, String pattern) {

        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        String formatedDate = dateFormat.format(date);

        String firstChar = formatedDate.substring(0, 1).toUpperCase();

        formatedDate = firstChar + formatedDate.substring(1);

        return formatedDate;
    }

    public static RequestOptions getPlaceholder() {

        RequestOptions placeholder = new RequestOptions();
        placeholder.placeholder(R.drawable.placeholder_photo);

        return placeholder;
    }

    public static HelpfulUser getUserFromHashMap(Map<String, Object> map) {
        User u = new User((String) map.get("name"), (String) map.get("surname"), (String) map.get("city"), (String) map.get("country"), Math.toIntExact((Long) map.get("gender")), (String) map.get("birthday"), (String) map.get("nick"), (String) map.get("profilePhotoUrl"), (Boolean) map.get("isBanned"));

        return new HelpfulUser((String) map.get("id"), u);
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static boolean arrayContainsUserId(ArrayList<String> list, String userId) {

        for (String id : list
                ) {
            if (id.equals(userId))
                return true;
        }

        return false;
    }

}

