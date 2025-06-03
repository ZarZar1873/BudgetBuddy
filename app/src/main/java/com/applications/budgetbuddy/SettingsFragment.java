package com.applications.budgetbuddy;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/*
@Author Dominic Drury
Settings page that manages tutorial, all data deletion, and a future google sign in option.
 */
public class SettingsFragment extends Fragment {

    private SettingsListener settingsListener;

    public interface SettingsListener {
        void onDeleteAllBudgets();
        void onSettingsReturnButton();
        void onShowTutorial();
        void onSignInWithGoogle(); // For future use
    }

    public SettingsFragment() {
        // Required empty public constructor
    }

    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof SettingsListener) {
            settingsListener = (SettingsListener) context;
        } else {
            throw new RuntimeException(context + " must implement SettingsListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button tutorialButton = view.findViewById(R.id.tutorialButton);
        Button returnButton = view.findViewById(R.id.settingsReturnButton);
        Button deleteBudgetsButton = view.findViewById(R.id.deleteBudgetsButton);
        Button signInButton = view.findViewById(R.id.signInButton); // Future use

        tutorialButton.setOnClickListener(v -> settingsListener.onShowTutorial());
        returnButton.setOnClickListener(v -> settingsListener.onSettingsReturnButton());
        deleteBudgetsButton.setOnClickListener(v -> settingsListener.onDeleteAllBudgets());
        signInButton.setOnClickListener(v -> settingsListener.onSignInWithGoogle());
    }
}