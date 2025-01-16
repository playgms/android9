package com.example.meetingschedule;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private FrameLayout container;
    private View addMeetingView, viewMeetingsView;
    private Button addMeetingButton, viewMeetingsButton;
    private EditText meetingDateInput, meetingTimeInput, meetingAgendaInput, filterDateInput;
    private TextView meetingsList;
    private DataBaseConn db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DataBaseConn(this);

        // Initialize layout elements
        container = findViewById(R.id.container);
        addMeetingButton = findViewById(R.id.addMeetingButton);
        viewMeetingsButton = findViewById(R.id.viewMeetingsButton);

        // Inflate views
        addMeetingView = getLayoutInflater().inflate(R.layout.add_meeting_view, null);
        viewMeetingsView = getLayoutInflater().inflate(R.layout.view_meetings_view, null);

        // Default to Add Meeting View
        switchToAddMeetingView();

        // Add View Listeners
        addMeetingButton.setOnClickListener(v -> switchToAddMeetingView());
        viewMeetingsButton.setOnClickListener(v -> switchToViewMeetingsView());
    }

    private void switchToAddMeetingView() {
        container.removeAllViews();
        container.addView(addMeetingView);

        meetingDateInput = addMeetingView.findViewById(R.id.meetingDateInput);
        meetingTimeInput = addMeetingView.findViewById(R.id.meetingTimeInput);
        meetingAgendaInput = addMeetingView.findViewById(R.id.meetingAgendaInput);

        Button addMeetingButtonSubmit = addMeetingView.findViewById(R.id.addMeetingButtonSubmit);

        meetingDateInput.setOnClickListener(v -> showDatePickerDialog(meetingDateInput));

        addMeetingButtonSubmit.setOnClickListener(v -> {
            String date = meetingDateInput.getText().toString();
            String time = meetingTimeInput.getText().toString();
            String agenda = meetingAgendaInput.getText().toString();

            if (date.isEmpty() || time.isEmpty() || agenda.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else {
                boolean result = db.insertData(date, time, agenda);
                if (result) {
                    Toast.makeText(this, "Meeting Added", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Error Adding Meeting", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void switchToViewMeetingsView() {
        container.removeAllViews();
        container.addView(viewMeetingsView);

        filterDateInput = viewMeetingsView.findViewById(R.id.filterDateInput);
        meetingsList = viewMeetingsView.findViewById(R.id.meetingsList);
        Button filterMeetingsButton = viewMeetingsView.findViewById(R.id.filterMeetingsButton);

        filterDateInput.setOnClickListener(v -> showDatePickerDialog(filterDateInput));

        filterMeetingsButton.setOnClickListener(v -> {
            String date = filterDateInput.getText().toString();
            if (date.isEmpty()) {
                Toast.makeText(this, "Please select a date", Toast.LENGTH_SHORT).show();
            } else {
                Cursor cursor = db.fetchData(date);
                if (cursor.getCount() == 0) {
                    meetingsList.setText("No meetings found for this date.");
                } else {
                    StringBuilder result = new StringBuilder();
                    while (cursor.moveToNext()) {
                        result.append("Time: ").append(cursor.getString(2)).append("\n");
                        result.append("Agenda: ").append(cursor.getString(3)).append("\n\n");
                    }
                    meetingsList.setText(result.toString());
                }
            }
        });
    }

    private void showDatePickerDialog(EditText dateInput) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String date = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    dateInput.setText(date);
                },
                year, month, day
        );

        datePickerDialog.show();
    }
}
